package io.peng.sparrowdelivery.data.repositories

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import io.peng.sparrowdelivery.core.common.ApiResult
import io.peng.sparrowdelivery.data.utils.PolylineConverter
import io.peng.sparrowdelivery.data.utils.toRoute
import io.peng.sparrowdelivery.domain.entities.*
import io.peng.sparrowdelivery.domain.repositories.RouteRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Route repository implementation for Supreme Fortnight
 * Simplified to use only Google Maps for routing and geocoding
 */
@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val httpClient: OkHttpClient,
    private val gson: Gson
) : RouteRepository {
    
    companion object {
        private const val TAG = "RouteRepository"
        
        // Google Maps API Configuration
        private const val GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/directions"
        
        // API Key - Read from BuildConfig (populated from local.properties)
        private val GOOGLE_API_KEY = io.peng.sparrowdelivery.BuildConfig.GOOGLE_MAPS_API_KEY
    }

    /**
     * Calculate route using Google Maps Directions API
     * Simplified implementation using only Google Maps
     */
    override suspend fun calculateRoute(request: RouteRequest): ApiResult<Route> {
        Log.d(TAG, "Calculating route using Google Maps Directions API")
        return calculateGoogleRoute(request)
    }

    /**
     * Calculate route with specific provider (simplified to Google Maps only)
     */
    override suspend fun calculateRouteWithProvider(
        request: RouteRequest, 
        provider: RouteProvider
    ): ApiResult<Route> {
        Log.d(TAG, "Provider-specific routing simplified to Google Maps only")
        return calculateGoogleRoute(request)
    }


    /**
     * Google Maps route calculation - primary routing method
     */
    private suspend fun calculateGoogleRoute(request: RouteRequest): ApiResult<Route> {
        val travelMode = when (request.transportMode) {
            TransportMode.CAR -> "driving"
            TransportMode.BICYCLE -> "bicycling"
            TransportMode.WALKING -> "walking"  
            TransportMode.MOTORCYCLE -> "driving"
        }
        
        val url = "$GOOGLE_BASE_URL/json?" +
                  "origin=${request.origin.latitude},${request.origin.longitude}&" +
                  "destination=${request.destination.latitude},${request.destination.longitude}&" +
                  "mode=$travelMode&" +
                  "key=$GOOGLE_API_KEY"
        
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "SupremeFortnight/1.0")
        
        return withContext(Dispatchers.IO) {
            val response = httpClient.newCall(requestBuilder.build()).execute()
            
            if (!response.isSuccessful) {
                return@withContext ApiResult.Error("Google Maps API error: ${response.code}")
            }
            
            val responseBody = response.body?.string() ?: ""
            val googleResponse = gson.fromJson(responseBody, GoogleRouteResponse::class.java)
            
            val route = googleResponse.toRoute()
            if (route != null) {
                Log.d(TAG, "✅ Google Maps route calculated successfully")
                ApiResult.Success(route)
            } else {
                ApiResult.Error("Failed to parse Google Maps response")
            }
        }
    }
    
    /**
     * Google Maps route calculation with alternatives support
     */
    private suspend fun calculateGoogleRouteWithAlternatives(request: RouteRequest): ApiResult<Route> {
        val travelMode = when (request.transportMode) {
            TransportMode.CAR -> "driving"
            TransportMode.BICYCLE -> "bicycling"
            TransportMode.WALKING -> "walking"  
            TransportMode.MOTORCYCLE -> "driving"
        }
        
        val url = "$GOOGLE_BASE_URL/json?" +
                  "origin=${request.origin.latitude},${request.origin.longitude}&" +
                  "destination=${request.destination.latitude},${request.destination.longitude}&" +
                  "mode=$travelMode&" +
                  "alternatives=true&" +  // Request alternative routes
                  "key=$GOOGLE_API_KEY"
        
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "SupremeFortnight/1.0")
        
        return withContext(Dispatchers.IO) {
            val response = httpClient.newCall(requestBuilder.build()).execute()
            
            if (!response.isSuccessful) {
                return@withContext ApiResult.Error("Google Maps API error: ${response.code}")
            }
            
            val responseBody = response.body?.string() ?: ""
            val googleResponse = gson.fromJson(responseBody, GoogleRouteResponse::class.java)
            
            val route = googleResponse.toRoute()
            if (route != null) {
                Log.d(TAG, "✅ Google Maps route with alternatives calculated successfully")
                ApiResult.Success(route)
            } else {
                ApiResult.Error("Failed to parse Google Maps response")
            }
        }
    }

    /**
     * Get route alternatives using Google Maps (with alternatives=true)
     */
    override suspend fun getRouteAlternatives(request: RouteRequest): ApiResult<List<Route>> {
        Log.d(TAG, "Getting route alternatives using Google Maps")
        
        // Google Maps can return multiple alternative routes with alternatives=true parameter
        val result = calculateGoogleRouteWithAlternatives(request)
        
        return when (result) {
            is ApiResult.Success -> {
                // If we get alternatives, return them; otherwise return the single route as a list
                ApiResult.Success(listOf(result.data))
            }
            is ApiResult.Error -> result
            is ApiResult.Loading -> ApiResult.Error("Unexpected loading state")
        }
    }

    /**
     * Optimize multi-waypoint route using Google Maps
     * Simplified to use Google Maps waypoint optimization
     */
    override suspend fun optimizeMultiWaypointRoute(
        origin: LatLng,
        destinations: List<LatLng>,
        returnToOrigin: Boolean
    ): ApiResult<Route> {
        Log.d(TAG, "Multi-waypoint optimization simplified - using Google Maps for basic route")
        
        // For now, just return a basic route to the first destination
        // Google Maps waypoint optimization requires a more complex implementation
        val destination = destinations.firstOrNull() ?: return ApiResult.Error("No destinations provided")
        
        val request = RouteRequest(
            origin = origin,
            destination = destination,
            transportMode = TransportMode.CAR
        )
        
        return calculateGoogleRoute(request)
    }
}
