package io.peng.sparrowdelivery.data.repository

import android.content.Context
import io.peng.sparrowdelivery.core.error.ApiResult
import io.peng.sparrowdelivery.core.error.AppError
import io.peng.sparrowdelivery.data.services.*
import io.peng.sparrowdelivery.domain.repository.RoutingRepository
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementation of RoutingRepository
 * 
 * Provides routing services with intelligent fallback strategy:
 * 1. Enhanced Google Directions (Primary) - Most accurate for Ghana
 * 2. Enhanced Mapbox Directions (Secondary) - Good traffic data
 * 3. Enhanced HERE Routing (Fallback) - Reliable backup
 * 
 * Features:
 * - Automatic provider fallback on failures
 * - Simple route caching to avoid redundant calls
 * - Comprehensive error handling and logging
 * - Provider health monitoring
 */
class RoutingRepositoryImpl(
    private val context: Context,
    private val googleDirectionsService: EnhancedGoogleDirectionsService,
    private val mapboxDirectionsService: EnhancedMapboxDirectionsService,
    private val hereRoutingService: EnhancedHereRoutingService
) : RoutingRepository {
    
    // Simple in-memory cache for recent route requests
    private val routeCache = mutableMapOf<String, CachedRoute>()
    private val cacheValidityMs = 5 * 60 * 1000L // 5 minutes
    
    // Provider health tracking
    private val providerHealth = mutableMapOf<String, ProviderHealth>()
    
    override suspend fun getRouteAlternatives(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate>
    ): Flow<ApiResult<List<RouteInfo>>> = flow {
        
        emit(ApiResult.Loading)
        
        val cacheKey = generateCacheKey(origin, destination, waypoints)
        
        // Check cache first
        val cachedRoute = routeCache[cacheKey]
        if (cachedRoute != null && isCacheValid(cachedRoute)) {
            emit(ApiResult.Success(cachedRoute.routes))
            return@flow
        }
        
        // Try providers in order with fallback
        val providers = getOrderedProviders()
        var lastError: AppError? = null
        
        for (provider in providers) {
            try {
                when (provider) {
                    "google" -> {
                        val routes = googleDirectionsService.getRouteAlternatives(origin, destination, waypoints)
                        if (routes.isNotEmpty()) {
                            cacheRoutes(cacheKey, routes)
                            updateProviderHealth("google", true)
                            emit(ApiResult.Success(routes))
                            return@flow
                        }
                    }
                    "mapbox" -> {
                        val routes = mapboxDirectionsService.getRouteAlternatives(origin, destination, waypoints)
                        if (routes.isNotEmpty()) {
                            cacheRoutes(cacheKey, routes)
                            updateProviderHealth("mapbox", true)
                            emit(ApiResult.Success(routes))
                            return@flow
                        }
                    }
                    "here" -> {
                        val routes = hereRoutingService.getRouteAlternatives(origin, destination, waypoints)
                        if (routes.isNotEmpty()) {
                            cacheRoutes(cacheKey, routes)
                            updateProviderHealth("here", true)
                            emit(ApiResult.Success(routes))
                            return@flow
                        }
                    }
                }
            } catch (e: Exception) {
                updateProviderHealth(provider, false)
                lastError = AppError.Unknown(e)
                println("RoutingRepository: $provider failed with: ${e.message}")
                continue // Try next provider
            }
        }
        
        // If all providers failed
        val error = lastError ?: AppError.LocationNotFound
        emit(ApiResult.Error(error))
    }
    
    override suspend fun getRouteAlternatives(request: RouteRequest): Flow<ApiResult<List<RouteInfo>>> = flow {
        emit(ApiResult.Loading)
        
        try {
            // Use HERE service for custom requests as it has the best RouteRequest support
            val routes = hereRoutingService.calculateRoute(request)
            if (routes.isNotEmpty()) {
                updateProviderHealth("here", true)
                emit(ApiResult.Success(routes))
            } else {
                // Fallback to basic coordinate-based routing
                val basicRoutes = getRouteAlternatives(
                    origin = request.origin,
                    destination = request.destination,
                    waypoints = request.waypoints
                )
                basicRoutes.collect { emit(it) }
            }
        } catch (e: Exception) {
            updateProviderHealth("here", false)
            emit(ApiResult.Error(AppError.Unknown(e)))
        }
    }
    
    override suspend fun getBestRoute(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate>
    ): Flow<ApiResult<RouteInfo?>> = flow {
        
        // Get all route alternatives
        getRouteAlternatives(origin, destination, waypoints).collect { result ->
            when (result) {
                is ApiResult.Success -> {
                    // Return the first route (typically the fastest/best)
                    val bestRoute = result.data.minByOrNull { route ->
                        // Prefer routes with traffic data, then by duration
                        if (route.trafficDelay != null) {
                            route.duration + route.trafficDelay
                        } else {
                            route.duration
                        }
                    }
                    emit(ApiResult.Success(bestRoute))
                }
                is ApiResult.Error -> emit(ApiResult.Error(result.error))
                is ApiResult.Loading -> emit(ApiResult.Loading)
            }
        }
    }
    
    override suspend fun clearCache() {
        routeCache.clear()
    }
    
    override suspend fun getProviderStatus(): Map<String, Boolean> {
        return mapOf(
            "google" to getProviderHealth("google").isHealthy,
            "mapbox" to getProviderHealth("mapbox").isHealthy,
            "here" to getProviderHealth("here").isHealthy
        )
    }
    
    // Private helper methods
    
    private fun generateCacheKey(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate>
    ): String {
        val originKey = "${origin.latitude},${origin.longitude}"
        val destinationKey = "${destination.latitude},${destination.longitude}"
        val waypointsKey = waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }
        return "$originKey->$destinationKey:$waypointsKey"
    }
    
    private fun isCacheValid(cachedRoute: CachedRoute): Boolean {
        return System.currentTimeMillis() - cachedRoute.timestamp < cacheValidityMs
    }
    
    private fun cacheRoutes(cacheKey: String, routes: List<RouteInfo>) {
        routeCache[cacheKey] = CachedRoute(
            routes = routes,
            timestamp = System.currentTimeMillis()
        )
        
        // Clean old cache entries (simple LRU)
        if (routeCache.size > 50) {
            val oldestKey = routeCache.entries
                .minByOrNull { it.value.timestamp }?.key
            oldestKey?.let { routeCache.remove(it) }
        }
    }
    
    private fun getOrderedProviders(): List<String> {
        // Order providers by health and preference
        val providers = listOf("google", "mapbox", "here")
        return providers.sortedByDescending { provider ->
            val health = getProviderHealth(provider)
            if (health.isHealthy) {
                // Prefer Google, then Mapbox, then HERE
                when (provider) {
                    "google" -> 3
                    "mapbox" -> 2
                    "here" -> 1
                    else -> 0
                }
            } else {
                -1 // Unhealthy providers go last
            }
        }
    }
    
    private fun updateProviderHealth(provider: String, isHealthy: Boolean) {
        val currentHealth = providerHealth[provider] ?: ProviderHealth()
        
        val updatedHealth = if (isHealthy) {
            currentHealth.copy(
                isHealthy = true,
                consecutiveFailures = 0,
                lastSuccessTime = System.currentTimeMillis()
            )
        } else {
            currentHealth.copy(
                isHealthy = currentHealth.consecutiveFailures < 2, // Mark unhealthy after 3 failures
                consecutiveFailures = currentHealth.consecutiveFailures + 1,
                lastFailureTime = System.currentTimeMillis()
            )
        }
        
        providerHealth[provider] = updatedHealth
    }
    
    private fun getProviderHealth(provider: String): ProviderHealth {
        return providerHealth[provider] ?: ProviderHealth()
    }
}

/**
 * Data class for cached route information
 */
private data class CachedRoute(
    val routes: List<RouteInfo>,
    val timestamp: Long
)

/**
 * Data class for tracking provider health
 */
private data class ProviderHealth(
    val isHealthy: Boolean = true,
    val consecutiveFailures: Int = 0,
    val lastSuccessTime: Long = 0L,
    val lastFailureTime: Long = 0L
)
