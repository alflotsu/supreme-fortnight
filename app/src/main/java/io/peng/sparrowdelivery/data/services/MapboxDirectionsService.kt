package io.peng.sparrowdelivery.data.services

import com.google.gson.annotations.SerializedName
import io.peng.sparrowdelivery.BuildConfig
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import io.peng.sparrowdelivery.core.security.ApiKeyManager
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

// Mapbox Directions API Models
data class MapboxDirectionsResponse(
    @SerializedName("routes")
    val routes: List<MapboxRoute>,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("message")
    val message: String?
)

data class MapboxRoute(
    @SerializedName("geometry")
    val geometry: String, // This is the polyline string
    
    @SerializedName("legs")
    val legs: List<MapboxRouteLeg>,
    
    @SerializedName("distance")
    val distance: Double, // meters
    
    @SerializedName("duration")
    val duration: Double, // seconds
    
    @SerializedName("weight_name")
    val weightName: String,
    
    @SerializedName("weight")
    val weight: Double
)

data class MapboxRouteLeg(
    @SerializedName("distance")
    val distance: Double,
    
    @SerializedName("duration")
    val duration: Double,
    
    @SerializedName("summary")
    val summary: String,
    
    @SerializedName("steps")
    val steps: List<MapboxStep>?
)

data class MapboxStep(
    @SerializedName("geometry")
    val geometry: String,
    
    @SerializedName("maneuver")
    val maneuver: MapboxManeuver,
    
    @SerializedName("distance")
    val distance: Double,
    
    @SerializedName("duration")
    val duration: Double,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("instruction")
    val instruction: String?
)

data class MapboxManeuver(
    @SerializedName("location")
    val location: List<Double>, // [longitude, latitude]
    
    @SerializedName("bearing_after")
    val bearingAfter: Int,
    
    @SerializedName("bearing_before")
    val bearingBefore: Int,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("instruction")
    val instruction: String
)

// Mapbox Directions API Interface
interface MapboxDirectionsApi {
    @GET("directions/v5/mapbox/{profile}/{coordinates}")
    suspend fun getDirections(
        @Path("profile") profile: String, // driving-traffic, driving, walking, cycling
        @Path("coordinates") coordinates: String, // "lng,lat;lng,lat;..."
        @Query("alternatives") alternatives: String = "true",
        @Query("geometries") geometries: String = "polyline", // polyline, polyline6, geojson
        @Query("language") language: String = "en",
        @Query("overview") overview: String = "full", // full, simplified, false
        @Query("steps") steps: String = "true",
        @Query("continue_straight") continueStraight: String = "default",
        @Query("waypoint_names") waypointNames: String = "",
        @Query("access_token") accessToken: String
    ): MapboxDirectionsResponse
}

class MapboxDirectionsService {
    private val api: MapboxDirectionsApi by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        
        Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapboxDirectionsApi::class.java)
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Get route alternatives using Mapbox Directions API
     */
    suspend fun getRouteAlternatives(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate> = emptyList(),
        profile: MapboxProfile = MapboxProfile.DRIVING_TRAFFIC
    ): List<RouteInfo> {
        _isLoading.value = true
        _error.value = null
        
        return try {
            // Build coordinates string: "lng,lat;lng,lat;..."
            val coordinatesList = mutableListOf<LocationCoordinate>()
            coordinatesList.add(origin)
            coordinatesList.addAll(waypoints)
            coordinatesList.add(destination)
            
            val coordinatesString = coordinatesList.joinToString(";") { 
                "${it.longitude},${it.latitude}" 
            }
            
            val apiKey = getApiKey()
            println("=== MAPBOX API REQUEST ===")
            println("Coordinates: $coordinatesString")
            println("Profile: ${profile.value}")
            println("API Key: ${if (apiKey.isNotEmpty()) "Valid (${apiKey.take(10)}...)" else "MISSING"}")
            
            val response = api.getDirections(
                profile = profile.value,
                coordinates = coordinatesString,
                alternatives = "true",
                accessToken = apiKey
            )
            
            println("=== MAPBOX API RESPONSE ===")
            println("Status: ${response.code}")
            println("Message: ${response.message ?: "None"}")
            println("Routes found: ${response.routes.size}")
            
            if (response.code == "Ok") {
                val routes = response.routes.mapIndexed { index, mapboxRoute ->
                    convertToRouteInfo(mapboxRoute, index, profile)
                }
                _isLoading.value = false
                routes
            } else {
                val errorMessage = response.message ?: "Mapbox Directions API error: ${response.code}"
                _error.value = errorMessage
                println("Mapbox API Error: $errorMessage")
                _isLoading.value = false
                emptyList()
            }
            
        } catch (e: Exception) {
            val errorMessage = "Failed to get Mapbox directions: ${e.message}"
            println("Mapbox Directions API Error: $errorMessage")
            _error.value = errorMessage
            _isLoading.value = false
            emptyList()
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
        
        // For traffic-aware profiles, we could estimate traffic delay
        // Mapbox doesn't directly provide traffic delay like Google, but we can estimate
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
     * Get Mapbox access token securely
     */
    private fun getApiKey(): String {
        return try {
            ApiKeyManager.getMapboxAccessToken()
        } catch (e: SecurityException) {
            throw IllegalStateException(
                "Mapbox access token not found. Please add MAPBOX_ACCESS_TOKEN to your local.properties file.\n" +
                "Get a free token at: https://account.mapbox.com/access-tokens/",
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
 * Mapbox routing profiles
 */
enum class MapboxProfile(val value: String, val displayName: String) {
    DRIVING_TRAFFIC("driving-traffic", "Driving (Traffic-aware)"),
    DRIVING("driving", "Driving"),
    WALKING("walking", "Walking"),
    CYCLING("cycling", "Cycling");
}
