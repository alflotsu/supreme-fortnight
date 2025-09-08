package io.peng.sparrowdelivery.data.services

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
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// HERE Routing API Interface
interface HereRoutingApi {
    @GET("v8/routes")
    suspend fun getRoutes(
        @Query("apikey") apiKey: String,
        @QueryMap params: Map<String, String>
    ): HereRoutingResponse
}

class HereRoutingService {
    private val api: HereRoutingApi by lazy {
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
            .baseUrl("https://router.hereapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HereRoutingApi::class.java)
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _routes = MutableStateFlow<List<RouteInfo>>(emptyList())
    val routes: StateFlow<List<RouteInfo>> = _routes.asStateFlow()
    
    /**
     * Calculate route between origin and destination with optional waypoints
     */
    suspend fun calculateRoute(request: RouteRequest): List<RouteInfo> {
        _isLoading.value = true
        _error.value = null
        
        return try {
            val queryParams = request.toQueryMap()
            println("HERE API Request params: $queryParams")
            
            val response = api.getRoutes(
                apiKey = getApiKey(),
                params = queryParams
            )
            
            println("HERE API Response: ${response.routes.size} routes found")
            
            val routes = response.routes.mapIndexed { index, hereRoute ->
                println("Route $index: ${hereRoute.sections.size} sections, first section polyline length: ${hereRoute.sections.firstOrNull()?.polyline?.length ?: 0}")
                convertToRouteInfo(hereRoute, request.routeTypes.getOrNull(index) ?: RouteType.FAST)
            }
            
            _routes.value = routes
            _isLoading.value = false
            routes
            
        } catch (e: Exception) {
            val errorMessage = "Failed to calculate route: ${e.message}"
            println("HERE API Error: $errorMessage")
            _error.value = errorMessage
            _routes.value = emptyList()
            _isLoading.value = false
            emptyList()
        }
    }
    
    /**
     * Get multiple route alternatives (fast, short, balanced)
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
            distance < 10000 -> "${
                "%.1f".format(distance / 1000.0)
            }km"
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
     * Clear current routes and error state
     */
    fun clearRoutes() {
        _routes.value = emptyList()
        _error.value = null
    }
    
    /**
     * Get HERE API key securely
     */
    private fun getApiKey(): String {
        return try {
            ApiKeyManager.getHereApiKey()
        } catch (e: SecurityException) {
            throw IllegalStateException(
                "HERE API key not found. Please add HERE_API_KEY to your local.properties file.",
                e
            )
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
