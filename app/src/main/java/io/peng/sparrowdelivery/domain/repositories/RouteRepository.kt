package io.peng.sparrowdelivery.domain.repositories

import io.peng.sparrowdelivery.domain.entities.Route
import io.peng.sparrowdelivery.domain.entities.RouteRequest
import io.peng.sparrowdelivery.core.common.ApiResult

/**
 * Repository interface for route calculation
 * Following Clean Architecture pattern from WARP.md
 */
interface RouteRepository {
    /**
     * Calculate route using the best available routing API
     * Implements fallback strategy: Mapbox -> HERE -> Google Maps
     */
    suspend fun calculateRoute(request: RouteRequest): ApiResult<Route>
    
    /**
     * Calculate route with specific provider (for testing/preferences)
     */
    suspend fun calculateRouteWithProvider(
        request: RouteRequest, 
        provider: io.peng.sparrowdelivery.domain.entities.RouteProvider
    ): ApiResult<Route>
    
    /**
     * Get multiple route options (different routes for same origin/destination)
     */
    suspend fun getRouteAlternatives(request: RouteRequest): ApiResult<List<Route>>
    
    /**
     * Optimize route for multiple waypoints (for multiple deliveries)
     * Perfect for campus food delivery optimization
     */
    suspend fun optimizeMultiWaypointRoute(
        origin: com.google.android.gms.maps.model.LatLng,
        destinations: List<com.google.android.gms.maps.model.LatLng>,
        returnToOrigin: Boolean = true
    ): ApiResult<Route>
}
