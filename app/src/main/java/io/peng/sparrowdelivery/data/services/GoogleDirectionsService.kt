package io.peng.sparrowdelivery.data.services

import com.google.gson.annotations.SerializedName
import io.peng.sparrowdelivery.BuildConfig
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import io.peng.sparrowdelivery.core.security.ApiKeyManager
import io.peng.sparrowdelivery.core.error.*
import io.peng.sparrowdelivery.core.di.ServiceLocator
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Google Directions API Models
data class GoogleDirectionsResponse(
    @SerializedName("routes")
    val routes: List<GoogleRoute>,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("error_message")
    val errorMessage: String?
)

data class GoogleRoute(
    @SerializedName("legs")
    val legs: List<GoogleRouteLeg>,
    
    @SerializedName("overview_polyline")
    val overviewPolyline: GooglePolyline,
    
    @SerializedName("summary")
    val summary: String
)

data class GoogleRouteLeg(
    @SerializedName("distance")
    val distance: GoogleDistance,
    
    @SerializedName("duration")
    val duration: GoogleDuration,
    
    @SerializedName("duration_in_traffic")
    val durationInTraffic: GoogleDuration?
)

data class GoogleDistance(
    @SerializedName("text")
    val text: String,
    
    @SerializedName("value")
    val value: Int // meters
)

data class GoogleDuration(
    @SerializedName("text")
    val text: String,
    
    @SerializedName("value")
    val value: Int // seconds
)

data class GooglePolyline(
    @SerializedName("points")
    val points: String
)

// Google Directions API Interface
interface GoogleDirectionsApi {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String? = null,
        @Query("alternatives") alternatives: Boolean = false,
        @Query("departure_time") departureTime: String? = "now",
        @Query("traffic_model") trafficModel: String = "best_guess",
        @Query("key") apiKey: String
    ): GoogleDirectionsResponse
}

class GoogleDirectionsService(private val context: Context? = null) {
    private val errorHandler: ErrorHandler? by lazy { 
        context?.let { ServiceLocator.getErrorHandler(it) }
    }
    
    private val api: GoogleDirectionsApi by lazy {
        createApiClient()
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<AppError?>(null)
    val error: StateFlow<AppError?> = _error.asStateFlow()
    
    /**
     * Create API client with dynamic timeouts
     */
    private fun createApiClient(): GoogleDirectionsApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        // Get timeout configuration based on connection quality
        val timeoutConfig = errorHandler?.getTimeoutConfig() ?: TimeoutConfig(
            connectTimeoutMs = 30_000L,
            readTimeoutMs = 30_000L,
            writeTimeoutMs = 15_000L
        )
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(timeoutConfig.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutConfig.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(timeoutConfig.writeTimeoutMs, TimeUnit.MILLISECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleDirectionsApi::class.java)
    }

    /**
     * Get route alternatives with enhanced error handling
     */
    suspend fun getRouteAlternatives(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate> = emptyList()
    ): List<RouteInfo> {
        _isLoading.value = true
        _error.value = null
        
        // If no error handler available, use basic implementation
        if (errorHandler == null) {
            return getRouteAlternativesBasic(origin, destination, waypoints)
        }
        
        // Configure retry policy based on connection quality
        val handler = errorHandler
        val retryConfig = when (handler?.getConnectionQuality()) {
            ConnectionQuality.WIFI -> RetryConfig(maxAttempts = 2, initialDelayMs = 500L)
            ConnectionQuality.CELLULAR_GOOD -> RetryConfig(maxAttempts = 3, initialDelayMs = 1000L)
            ConnectionQuality.CELLULAR_POOR, ConnectionQuality.POOR -> RetryConfig(maxAttempts = 4, initialDelayMs = 2000L)
            ConnectionQuality.NONE -> RetryConfig(maxAttempts = 1, initialDelayMs = 0L)
            null -> RetryConfig(maxAttempts = 2, initialDelayMs = 1000L)
        }
        
        val result = safeApiCall(
            errorHandler = handler!!,
            retryConfig = retryConfig
        ) {
            makeDirectionsRequest(origin, destination, waypoints)
        }
        
        return when (result) {
            is ApiResult.Success -> {
                _isLoading.value = false
                result.data
            }
            is ApiResult.Error -> {
                _isLoading.value = false
                _error.value = result.error
                println("Google Directions API Error: ${result.error.technicalMessage}")
                emptyList()
            }
            is ApiResult.Loading -> emptyList()
        }
    }
    
    /**
     * Basic implementation without error handler (fallback)
     */
    private suspend fun getRouteAlternativesBasic(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate>
    ): List<RouteInfo> {
        return try {
            makeDirectionsRequest(origin, destination, waypoints)
        } catch (e: Exception) {
            val errorMessage = "Failed to get directions: ${e.message}"
            println("Google Directions API Error: $errorMessage")
            _error.value = AppError.Unknown(e)
            _isLoading.value = false
            emptyList()
        }
    }
    
    /**
     * Make the actual directions request
     */
    private suspend fun makeDirectionsRequest(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate>
    ): List<RouteInfo> {
        val originString = "${origin.latitude},${origin.longitude}"
        val destinationString = "${destination.latitude},${destination.longitude}"
        val waypointsString = if (waypoints.isNotEmpty()) {
            waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }
        } else null
        
        val response = api.getDirections(
            origin = originString,
            destination = destinationString,
            waypoints = waypointsString,
            alternatives = true,
            apiKey = getApiKey()
        )
        
        return when (response.status) {
            "OK" -> {
                response.routes.mapIndexed { index, googleRoute ->
                    convertToRouteInfo(googleRoute, index)
                }
            }
            "ZERO_RESULTS" -> {
                throw IllegalArgumentException("No route found between the specified locations")
            }
            "OVER_QUERY_LIMIT" -> {
                throw retrofit2.HttpException(
                    retrofit2.Response.error<Any>(429, okhttp3.ResponseBody.create(null, "Rate limit exceeded"))
                )
            }
            "REQUEST_DENIED" -> {
                throw retrofit2.HttpException(
                    retrofit2.Response.error<Any>(403, okhttp3.ResponseBody.create(null, "API key invalid"))
                )
            }
            else -> {
                throw Exception("Google Directions API error: ${response.status}")
            }
        }
    }
    
    /**
     * Convert Google Directions response to RouteInfo
     */
    private fun convertToRouteInfo(googleRoute: GoogleRoute, routeIndex: Int): RouteInfo {
        val totalDistance = googleRoute.legs.sumOf { it.distance.value }
        val totalDuration = googleRoute.legs.sumOf { it.duration.value }
        val trafficDuration = googleRoute.legs.mapNotNull { it.durationInTraffic?.value }.sumOrNull()
        val trafficDelay = trafficDuration?.let { it - totalDuration }?.takeIf { it > 0 }
        
        // Decode Google polyline
        val polylinePoints = decodeGooglePolyline(googleRoute.overviewPolyline.points)
        
        val routeType = when (routeIndex) {
            0 -> RouteType.FAST // Primary route is usually fastest
            else -> RouteType.BALANCED // Alternative routes
        }
        
        val summary = generateRouteSummary(totalDistance, totalDuration, trafficDelay, routeType)
        
        return RouteInfo(
            id = "google_route_$routeIndex",
            polylinePoints = polylinePoints,
            distance = totalDistance,
            duration = totalDuration,
            trafficDelay = trafficDelay,
            routeType = routeType,
            summary = summary
        )
    }
    
    /**
     * Decode Google polyline format to coordinates
     * Google uses standard polyline encoding algorithm
     */
    private fun decodeGooglePolyline(encoded: String): List<LocationCoordinate> {
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
     * Generate route summary
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
     * Get Google Maps API key securely
     */
    private fun getApiKey(): String {
        return try {
            ApiKeyManager.getGoogleMapsApiKey()
        } catch (e: SecurityException) {
            throw IllegalStateException(
                "Google Maps API key not found. Please add GOOGLE_MAPS_API_KEY to your local.properties file.",
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
}

/**
 * Extension to safely sum nullable integers
 */
private fun List<Int?>.sumOrNull(): Int? {
    val nonNullValues = this.filterNotNull()
    return if (nonNullValues.isEmpty()) null else nonNullValues.sum()
}
