package io.peng.sparrowdelivery.core.di

import android.content.Context
import io.peng.sparrowdelivery.data.auth.AuthRepository
import io.peng.sparrowdelivery.data.services.*
import io.peng.sparrowdelivery.data.repository.*
import io.peng.sparrowdelivery.domain.repository.*
import io.peng.sparrowdelivery.core.error.ErrorHandler
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate

/**
 * Lightweight Service Locator for Dependency Injection
 * 
 * Provides singleton instances of repositories and services to prevent memory leaks
 * and unnecessary object creation. Perfect for small-medium apps.
 * 
 * Updated to use Repository pattern:
 * - Repositories provide clean abstractions over services
 * - Enhanced error handling and caching
 * - Better separation of concerns
 * - Easier to test and maintain
 * 
 * Benefits:
 * - Single HTTP client shared across services
 * - Proper lifecycle management
 * - Memory efficient
 * - Simple to understand and maintain
 */
object ServiceLocator {
    
    // Error Handler (required by all enhanced services)
    private var _errorHandler: ErrorHandler? = null
    
    // Repository layer (main access point)
    private var _routingRepository: RoutingRepository? = null
    private var _locationRepository: LocationRepository? = null
    private var _placesRepository: PlacesRepository? = null
    private var _deliveryTrackingRepository: DeliveryTrackingRepository? = null
    
    // Enhanced services (used by repositories)
    private var _enhancedGoogleDirectionsService: EnhancedGoogleDirectionsService? = null
    private var _enhancedHereRoutingService: EnhancedHereRoutingService? = null
    private var _enhancedMapboxDirectionsService: EnhancedMapboxDirectionsService? = null
    private var _enhancedPlacesAutocompleteService: EnhancedPlacesAutocompleteService? = null
    
    // Legacy services - kept for backward compatibility during migration
    private var _googleDirectionsService: GoogleDirectionsService? = null
    private var _placesAutocompleteService: PlacesAutocompleteService? = null
    
    // Services that don't require context
    private val _authRepository: AuthRepository by lazy { 
        AuthRepository() 
    }
    
    /**
     * Get Routing Repository singleton
     * Main interface for all routing operations
     */
    fun getRoutingRepository(context: Context): RoutingRepository {
        return _routingRepository ?: run {
            val repository = RoutingRepositoryImpl(
                context = context.applicationContext,
                googleDirectionsService = getGoogleDirectionsService(context),
                mapboxDirectionsService = getMapboxDirectionsService(context),
                hereRoutingService = getHereRoutingService(context)
            )
            _routingRepository = repository
            repository
        }
    }
    
    /**
     * Get Location Repository singleton
     * Main interface for location operations
     */
    fun getLocationRepository(context: Context): LocationRepository {
        return _locationRepository ?: run {
            val repository = LocationRepositoryImpl(context.applicationContext)
            _locationRepository = repository
            repository
        }
    }
    
    /**
     * Get Places Repository singleton
     * Main interface for places search and details
     */
    fun getPlacesRepository(context: Context): PlacesRepository {
        return _placesRepository ?: run {
            val repository = PlacesRepositoryImpl(
                context = context.applicationContext,
                placesService = getEnhancedPlacesAutocompleteService(context)
            )
            _placesRepository = repository
            repository
        }
    }
    
    /**
     * Get Delivery Tracking Repository singleton
     * Main interface for delivery tracking operations
     */
    fun getDeliveryTrackingRepository(): DeliveryTrackingRepository {
        return _deliveryTrackingRepository ?: run {
            val repository = MockDeliveryTrackingRepository()
            _deliveryTrackingRepository = repository
            repository
        }
    }
    
    /**
     * Get Enhanced Google Directions Service singleton (used by repositories)
     * Requires context for enhanced error handling
     */
    fun getGoogleDirectionsService(context: Context): EnhancedGoogleDirectionsService {
        return _enhancedGoogleDirectionsService ?: run {
            val service = EnhancedGoogleDirectionsService(context.applicationContext)
            _enhancedGoogleDirectionsService = service
            service
        }
    }
    
    /**
     * Get legacy Google Directions Service (deprecated) 
     * Only use during migration period
     */
    @Deprecated("Use getGoogleDirectionsService with context parameter instead")
    fun getLegacyGoogleDirectionsService(context: Context? = null): GoogleDirectionsService {
        return _googleDirectionsService ?: run {
            val service = GoogleDirectionsService(context?.applicationContext)
            _googleDirectionsService = service
            service
        }
    }
    
    /**
     * Get Enhanced HERE Routing Service singleton (used by repositories)
     */
    fun getHereRoutingService(context: Context): EnhancedHereRoutingService {
        return _enhancedHereRoutingService ?: run {
            val service = EnhancedHereRoutingService(context.applicationContext)
            _enhancedHereRoutingService = service
            service
        }
    }
    
    /**
     * Get Enhanced Mapbox Directions Service singleton (used by repositories)
     */
    fun getMapboxDirectionsService(context: Context): EnhancedMapboxDirectionsService {
        return _enhancedMapboxDirectionsService ?: run {
            val service = EnhancedMapboxDirectionsService(context.applicationContext)
            _enhancedMapboxDirectionsService = service
            service
        }
    }
    
    /**
     * Get Authentication Repository singleton
     */
    fun getAuthRepository(): AuthRepository {
        return _authRepository
    }
    
    /**
     * Get Enhanced Places Autocomplete Service singleton (used by repositories)
     * Requires context for enhanced error handling
     */
    fun getEnhancedPlacesAutocompleteService(context: Context): EnhancedPlacesAutocompleteService {
        return _enhancedPlacesAutocompleteService ?: run {
            val service = EnhancedPlacesAutocompleteService(context.applicationContext)
            _enhancedPlacesAutocompleteService = service
            service
        }
    }
    
    /**
     * Get Enhanced Places Autocomplete Service singleton (deprecated - use getPlacesRepository)
     * @deprecated Use getPlacesRepository() instead for better abstraction
     */
    @Deprecated("Use getPlacesRepository() instead for better abstraction")
    fun getPlacesAutocompleteService(context: Context): EnhancedPlacesAutocompleteService {
        return getEnhancedPlacesAutocompleteService(context)
    }
    
    /**
     * Get legacy Places Autocomplete Service (deprecated)
     * Only use during migration period
     */
    @Deprecated("Use getPlacesAutocompleteService with enhanced implementation instead")
    fun getLegacyPlacesAutocompleteService(context: Context): PlacesAutocompleteService {
        return _placesAutocompleteService ?: run {
            val service = PlacesAutocompleteService(context.applicationContext)
            _placesAutocompleteService = service
            service
        }
    }
    
    /**
     * Get Error Handler singleton
     * Requires context, so initialized lazily when first requested
     */
    fun getErrorHandler(context: Context): ErrorHandler {
        return _errorHandler ?: run {
            val handler = ErrorHandler(context.applicationContext)
            _errorHandler = handler
            handler
        }
    }
    
    /**
     * Clear all services and repositories (call this in Application.onTerminate or for testing)
     */
    fun clearServices() {
        // Clear repositories
        _routingRepository = null
        _locationRepository = null
        _placesRepository = null
        _deliveryTrackingRepository = null
        
        // Clear services  
        _placesAutocompleteService = null
        _enhancedPlacesAutocompleteService = null
        _enhancedGoogleDirectionsService = null
        _enhancedHereRoutingService = null
        _enhancedMapboxDirectionsService = null
        _googleDirectionsService = null
        _errorHandler = null
        
        // Other services will be garbage collected when ServiceLocator is cleared
    }
    
    /**
     * Get routing service provider for fallback routing (deprecated)
     * @deprecated Use getRoutingRepository() instead for better abstraction
     * 
     * Returns services in preferred order: Google -> HERE -> Mapbox
     * Requires context for enhanced service implementations
     */
    @Deprecated("Use getRoutingRepository() instead for better abstraction")
    fun getRoutingServices(context: Context): List<RoutingService> {
        return listOf(
            RoutingServiceWrapper("Google", getGoogleDirectionsService(context)),
            RoutingServiceWrapper("HERE", getHereRoutingService(context)),
            RoutingServiceWrapper("Mapbox", getMapboxDirectionsService(context))
        )
    }
}

/**
 * Common interface for routing services (adapter pattern)
 */
interface RoutingService {
    val name: String
    suspend fun getRouteAlternatives(
        origin: io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate,
        destination: io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate,
        waypoints: List<io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate> = emptyList()
    ): List<RouteInfo>
}

/**
 * Wrapper to make existing services conform to common interface
 */
private class RoutingServiceWrapper(
    override val name: String,
    private val service: Any
) : RoutingService {
    
    override suspend fun getRouteAlternatives(
        origin: LocationCoordinate,
        destination: LocationCoordinate,
        waypoints: List<LocationCoordinate>
    ): List<RouteInfo> {
        return when (service) {
            is EnhancedGoogleDirectionsService -> service.getRouteAlternatives(origin, destination, waypoints)
            is EnhancedHereRoutingService -> service.getRouteAlternatives(origin, destination, waypoints)
            is EnhancedMapboxDirectionsService -> service.getRouteAlternatives(origin, destination, waypoints)
            is GoogleDirectionsService -> service.getRouteAlternatives(origin, destination, waypoints)
            is HereRoutingService -> service.getRouteAlternatives(origin, destination, waypoints)
            is MapboxDirectionsService -> service.getRouteAlternatives(origin, destination, waypoints)
            else -> emptyList()
        }
    }
}
