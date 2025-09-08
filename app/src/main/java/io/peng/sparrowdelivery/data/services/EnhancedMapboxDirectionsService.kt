package io.peng.sparrowdelivery.data.services

import android.content.Context
import com.google.gson.annotations.SerializedName
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
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Enhanced Mapbox Directions Service with comprehensive error handling
 * 
 * Features:
 * - Dynamic timeouts based on connection quality
 * - Automatic retry with exponential backoff
 * - User-friendly error messages
 * - Network status checking
 * - Proper timeout configuration
 */
class EnhancedMapboxDirectionsService(private val context: Context) {
    
    private val errorHandler by lazy { ServiceLocator.getErrorHandler(context) }
    
    private val api: MapboxDirectionsApi by lazy {
        createApiClient()
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<AppError?>(null)
    val error: StateFlow<AppError?> = _error.asStateFlow()
    
    /**
     * Create API client with dynamic timeouts based on connection quality
     */
    private fun createApiClient(): MapboxDirectionsApi {
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
            .baseUrl("https://api.mapbox.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapboxDirectionsApi::class.java)
    }
    
    /**
     * Get route alternatives with comprehensive error handling
     */
    suspend fun getRouteAlternatives(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate> = emptyList(),
        profile: MapboxProfile = MapboxProfile.DRIVING_TRAFFIC
    ): List<RouteInfo> {
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
            makeDirectionsRequest(origin, destination, waypoints, profile)
        }
        
        return when (result) {
            is ApiResult.Success -> {
                _isLoading.value = false
                result.data
            }
            is ApiResult.Error -> {
                _isLoading.value = false
                _error.value = result.error
                
                // Log error for debugging
                println("Mapbox Directions API Error: ${result.error.technicalMessage}")
                
                emptyList()
            }
            is ApiResult.Loading -> {
                // This shouldn't happen with our implementation
                emptyList()
            }
        }
    }
    
    /**
     * Make the actual directions request
     */
    private suspend fun makeDirectionsRequest(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate>,
        profile: MapboxProfile
    ): List<RouteInfo> {
        // Build coordinates string: "lng,lat;lng,lat;..."
        val coordinatesList = mutableListOf<LocationCoordinate>()
        coordinatesList.add(origin)
        coordinatesList.addAll(waypoints)
        coordinatesList.add(destination)
        
        val coordinatesString = coordinatesList.joinToString(";") { 
            "${it.longitude},${it.latitude}" 
        }
        
        val response = api.getDirections(
            profile = profile.value,
            coordinates = coordinatesString,
            alternatives = "true",
            accessToken = getApiKey()
        )
        
        return when (response.code) {
            "Ok" -> {
                response.routes.mapIndexed { index, mapboxRoute ->
                    convertToRouteInfo(mapboxRoute, index, profile)
                }
            }
            "NoRoute" -> {
                throw IllegalArgumentException("No route found between the specified locations")
            }
            "NoSegment" -> {
                throw IllegalArgumentException("No routable path found between waypoints")
            }
            "ProfileNotFound" -> {
                throw IllegalArgumentException("Invalid routing profile: ${profile.value}")
            }
            "InvalidInput" -> {
                throw IllegalArgumentException("Invalid coordinates or parameters provided")
            }
            else -> {
                throw Exception("Mapbox Directions API error: ${response.code} - ${response.message}")
            }
        }
    }
    
    /**
     * Convert Mapbox route to RouteInfo
     */
    private fun convertToRouteInfo(mapboxRoute: MapboxRoute, routeIndex: Int, profile: MapboxProfile): RouteInfo {
        // Mapbox returns distance in meters and duration in seconds (as doubles)
        val totalDistance = mapboxRoute.distance.toInt()
        val totalDuration = mapboxRoute.duration.toInt()
        
        // Decode Mapbox polyline (same format as Google polyline)
        val polylinePoints = decodePolyline(mapboxRoute.geometry)
        
        // Determine route type based on index and profile
        val routeType = when {
            routeIndex == 0 && profile == MapboxProfile.DRIVING_TRAFFIC -> RouteType.FAST
            routeIndex == 0 -> RouteType.BALANCED
            mapboxRoute.weightName.contains("duration", ignoreCase = true) -> RouteType.FAST
            mapboxRoute.weightName.contains("distance", ignoreCase = true) -> RouteType.SHORT
            else -> RouteType.BALANCED
        }
        
        // For traffic-aware profiles, estimate traffic delay
        val trafficDelay: Int? = if (profile == MapboxProfile.DRIVING_TRAFFIC && routeIndex == 0) {
            // Estimate: assume 10-20% of duration could be traffic in urban areas
            val estimatedBaseTime = (totalDuration * 0.85).toInt()
            val delay = totalDuration - estimatedBaseTime
            if (delay > 60) delay else null // Only show if significant
        } else null
        
        val summary = generateRouteSummary(totalDistance, totalDuration, trafficDelay, routeType)
        
        return RouteInfo(
            id = "mapbox_route_$routeIndex",
            polylinePoints = polylinePoints,
            distance = totalDistance,
            duration = totalDuration,
            trafficDelay = trafficDelay,
            routeType = routeType,
            summary = summary
        )
    }
    
    /**
     * Decode polyline string to coordinates
     * Mapbox uses the same polyline encoding format as Google
     */
    private fun decodePolyline(encoded: String): List<LocationCoordinate> {
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
                    latitude = lat / 1e5,
                    longitude = lng / 1e5
                )
            )
        }
        
        return coordinates
    }
    
    /**
     * Generate route summary text
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
            ApiKeyManager.getMapboxAccessToken()
        } catch (e: SecurityException) {
            throw IllegalStateException(
                "Mapbox access token not configured. Please add MAPBOX_ACCESS_TOKEN to local.properties",
                e
            )
        }
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
