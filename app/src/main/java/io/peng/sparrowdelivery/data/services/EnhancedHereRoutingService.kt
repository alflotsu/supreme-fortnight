package io.peng.sparrowdelivery.data.services

import android.content.Context
import io.peng.sparrowdelivery.BuildConfig
import io.peng.sparrowdelivery.core.di.ServiceLocator
import io.peng.sparrowdelivery.core.error.*
import io.peng.sparrowdelivery.core.security.ApiKeyManager
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

/**
 * Enhanced HERE Routing Service with comprehensive error handling
 * 
 * Features:
 * - Dynamic timeouts based on connection quality
 * - Automatic retry with exponential backoff
 * - User-friendly error messages
 * - Network status checking
 * - Proper timeout configuration
 */
class EnhancedHereRoutingService(private val context: Context) {
    
    private val errorHandler by lazy { ServiceLocator.getErrorHandler(context) }
    
    private val api: HereRoutingApi by lazy {
        createApiClient()
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<AppError?>(null)
    val error: StateFlow<AppError?> = _error.asStateFlow()
    
    private val _routes = MutableStateFlow<List<RouteInfo>>(emptyList())
    val routes: StateFlow<List<RouteInfo>> = _routes.asStateFlow()
    
    /**
     * Create API client with dynamic timeouts based on connection quality
     */
    private fun createApiClient(): HereRoutingApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        // Get timeout configuration based on connection quality
        val timeoutConfig = errorHandler.getTimeoutConfig()
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(timeoutConfig.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutConfig.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(timeoutConfig.writeTimeoutMs, TimeUnit.MILLISECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl("https://router.hereapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HereRoutingApi::class.java)
    }
    
    /**
     * Calculate route between origin and destination with optional waypoints
     * with comprehensive error handling
     */
    suspend fun calculateRoute(request: RouteRequest): List<RouteInfo> {
        _isLoading.value = true
        _error.value = null
        
        // Configure retry policy based on connection quality
        val retryConfig = when (errorHandler.getConnectionQuality()) {
            ConnectionQuality.WIFI -> RetryConfig(maxAttempts = 2, initialDelayMs = 500L)
            ConnectionQuality.CELLULAR_GOOD -> RetryConfig(maxAttempts = 3, initialDelayMs = 1000L)
            ConnectionQuality.CELLULAR_POOR, ConnectionQuality.POOR -> RetryConfig(maxAttempts = 4, initialDelayMs = 2000L)
            ConnectionQuality.NONE -> RetryConfig(maxAttempts = 1, initialDelayMs = 0L) // Fail fast
        }
        
        val result = safeApiCall(
            errorHandler = errorHandler,
            retryConfig = retryConfig
        ) {
            makeHereRoutingRequest(request)
        }
        
        return when (result) {
            is ApiResult.Success -> {
                _isLoading.value = false
                _routes.value = result.data
                result.data
            }
            is ApiResult.Error -> {
                _isLoading.value = false
                _error.value = result.error
                _routes.value = emptyList()
                
                // Log error for debugging
                println("HERE Routing API Error: ${result.error.technicalMessage}")
                
                emptyList()
            }
            is ApiResult.Loading -> {
                // This shouldn't happen with our implementation
                emptyList()
            }
        }
    }
    
    /**
     * Get multiple route alternatives (fast, short, balanced) with enhanced error handling
     */
    suspend fun getRouteAlternatives(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate> = emptyList()
    ): List<RouteInfo> {
        val request = RouteRequest(
            origin = origin,
            destination = destination,
            waypoints = waypoints,
            routeTypes = listOf(RouteType.FAST, RouteType.SHORT, RouteType.BALANCED)
        )
        
        return calculateRoute(request)
    }
    
    /**
     * Make the actual HERE routing request
     */
    private suspend fun makeHereRoutingRequest(request: RouteRequest): List<RouteInfo> {
        val queryParams = request.toQueryMap()
        
        val response = api.getRoutes(
            apiKey = getApiKey(),
            params = queryParams
        )
        
        // Handle HERE API specific errors
        if (response.routes.isEmpty()) {
            throw IllegalArgumentException("No routes found between the specified locations")
        }
        
        return response.routes.mapIndexed { index, hereRoute ->
            convertToRouteInfo(hereRoute, request.routeTypes.getOrNull(index) ?: RouteType.FAST)
        }
    }
    
    /**
     * Convert HERE route response to our domain model
     */
    private fun convertToRouteInfo(hereRoute: HereRoute, routeType: RouteType): RouteInfo {
        // Combine all sections for the complete route
        val totalDistance = hereRoute.sections.sumOf { it.summary.length }
        val totalDuration = hereRoute.sections.sumOf { it.summary.duration }
        val totalTrafficDelay = hereRoute.sections.mapNotNull { it.summary.trafficDelay }.sumOrNull()
        
        // Decode polyline from all sections
        val allPolylinePoints = hereRoute.sections.flatMap { section ->
            decodeHerePolyline(section.polyline)
        }.distinct() // Remove duplicate points at section boundaries
        
        // Generate summary text
        val summary = generateRouteSummary(totalDistance, totalDuration, totalTrafficDelay, routeType)
        
        return RouteInfo(
            id = hereRoute.id,
            polylinePoints = allPolylinePoints,
            distance = totalDistance,
            duration = totalDuration,
            trafficDelay = totalTrafficDelay,
            routeType = routeType,
            summary = summary
        )
    }
    
    /**
     * Decode HERE polyline format to list of coordinates
     * HERE uses their own polyline encoding format
     */
    private fun decodeHerePolyline(encoded: String): List<LocationCoordinate> {
        val coordinates = mutableListOf<LocationCoordinate>()
        var index = 0
        var lat = 0
        var lng = 0
        
        while (index < encoded.length) {
            var shift = 0
            var result = 0
            var byte: Int
            
            // Decode latitude
            do {
                byte = encoded[index++].code - 63
                result = result or (byte and 0x1f shl shift)
                shift += 5
            } while (byte >= 0x20)
            
            val deltaLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += deltaLat
            
            shift = 0
            result = 0
            
            // Decode longitude
            do {
                byte = encoded[index++].code - 63
                result = result or (byte and 0x1f shl shift)
                shift += 5
            } while (byte >= 0x20)
            
            val deltaLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += deltaLng
            
            coordinates.add(
                LocationCoordinate(
                    latitude = lat / 100000.0,
                    longitude = lng / 100000.0
                )
            )
        }
        
        return coordinates
    }
    
    /**
     * Generate human-readable route summary
     */
    private fun generateRouteSummary(
        distance: Int,
        duration: Int,
        trafficDelay: Int?,
        routeType: RouteType
    ): String {
        val distanceText = when {
            distance < 1000 -> "${distance}m"
            distance < 10000 -> "${"%.1f".format(distance / 1000.0)}km"
            else -> "${(distance / 1000)}km"
        }
        
        val durationText = run {
            val minutes = (duration + 30) / 60
            when {
                minutes < 60 -> "${minutes}min"
                else -> {
                    val hours = minutes / 60
                    val remainingMinutes = minutes % 60
                    if (remainingMinutes == 0) "${hours}h"
                    else "${hours}h ${remainingMinutes}min"
                }
            }
        }
        
        val baseText = "$distanceText • $durationText"
        
        return when {
            trafficDelay != null && trafficDelay > 60 -> {
                val delayMinutes = (trafficDelay + 30) / 60
                "$baseText (+${delayMinutes}min traffic)"
            }
            routeType != RouteType.FAST -> "$baseText • ${routeType.displayName}"
            else -> baseText
        }
    }
    
    /**
     * Get API key securely
     */
    private fun getApiKey(): String {
        return try {
            ApiKeyManager.getHereApiKey()
        } catch (e: SecurityException) {
            throw IllegalStateException(
                "HERE API key not configured. Please add HERE_API_KEY to local.properties",
                e
            )
        }
    }
    
    /**
     * Clear current routes and error state
     */
    fun clearRoutes() {
        _routes.value = emptyList()
        _error.value = null
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Check if service is currently loading
     */
    fun isCurrentlyLoading(): Boolean = _isLoading.value
    
    /**
     * Get last error if any
     */
    fun getLastError(): AppError? = _error.value
    
    /**
     * Get connection status for debugging
     */
    fun getConnectionStatus(): String {
        return when (errorHandler.getConnectionQuality()) {
            ConnectionQuality.WIFI -> "WiFi - Excellent"
            ConnectionQuality.CELLULAR_GOOD -> "Mobile - Good"
            ConnectionQuality.CELLULAR_POOR -> "Mobile - Poor"
            ConnectionQuality.POOR -> "Poor Connection"
            ConnectionQuality.NONE -> "No Connection"
        }
    }
}

/**
 * Extension to safely sum nullable integers
 */
private fun List<Int?>.sumOrNull(): Int? {
    val nonNullValues = this.filterNotNull()
    return if (nonNullValues.isEmpty()) null else nonNullValues.sum()
}
