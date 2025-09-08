package io.peng.sparrowdelivery.domain.repository

import io.peng.sparrowdelivery.core.error.ApiResult
import io.peng.sparrowdelivery.data.services.RouteInfo
import io.peng.sparrowdelivery.data.services.RouteRequest
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for routing operations
 * 
 * Provides a clean abstraction over multiple routing providers (Google, Mapbox, HERE)
 * with automatic fallback, caching, and error handling.
 */
interface RoutingRepository {
    
    /**
     * Get route alternatives between origin and destination
     * 
     * Uses multiple providers with automatic fallback:
     * 1. Google Directions (primary)
     * 2. Mapbox Directions (fallback)
     * 3. HERE Routing (final fallback)
     * 
     * @param origin Starting location coordinate
     * @param destination End location coordinate
     * @param waypoints Optional intermediate stops
     * @return Flow of API results containing route alternatives
     */
    suspend fun getRouteAlternatives(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate> = emptyList()
    ): Flow<ApiResult<List<RouteInfo>>>
    
    /**
     * Get route alternatives using custom request parameters
     * 
     * @param request Custom route request with additional parameters
     * @return Flow of API results containing route alternatives
     */
    suspend fun getRouteAlternatives(
        request: RouteRequest
    ): Flow<ApiResult<List<RouteInfo>>>
    
    /**
     * Get the best available route (fastest with current traffic)
     * 
     * @param origin Starting location coordinate
     * @param destination End location coordinate
     * @param waypoints Optional intermediate stops
     * @return Flow of API result containing the optimal route
     */
    suspend fun getBestRoute(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate> = emptyList()
    ): Flow<ApiResult<RouteInfo?>>
    
    /**
     * Clear any cached route data
     */
    suspend fun clearCache()
    
    /**
     * Get the status of available routing providers
     * 
     * @return Map of provider names to their availability status
     */
    suspend fun getProviderStatus(): Map<String, Boolean>
}
