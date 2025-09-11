package io.peng.sparrowdelivery.presentation.features.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import io.peng.sparrowdelivery.data.services.PlaceDetails
import io.peng.sparrowdelivery.data.services.*
import io.peng.sparrowdelivery.domain.repository.RoutingRepository
import io.peng.sparrowdelivery.domain.repository.LocationRepository
import io.peng.sparrowdelivery.domain.repository.PlacesRepository
import io.peng.sparrowdelivery.domain.repositories.RouteRepository
import io.peng.sparrowdelivery.domain.entities.RouteRequest as NewRouteRequest
import io.peng.sparrowdelivery.domain.entities.TransportMode
import io.peng.sparrowdelivery.domain.entities.Route as NewRoute
import io.peng.sparrowdelivery.core.common.ApiResult as NewApiResult
import io.peng.sparrowdelivery.core.di.ServiceLocator
import io.peng.sparrowdelivery.core.error.ApiResult
// Booking state machine classes are in the same package

data class DriverInfo(
    val name: String,
    val rating: Float,
    val vehicleType: String,
    val plateNumber: String,
    val photo: String? = null,
    val phone: String,
    val totalPrice: Double,
    val estimatedArrival: String
)

data class HomeUiState(
    // Location and permissions
    val currentLocation: LatLng? = null,
    val isLoadingLocation: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val errorMessage: String? = null,
    
    // Map interaction
    val mapInteractionMode: MapInteractionMode = MapInteractionMode.NONE,
    val currentRoute: List<LocationCoordinate> = emptyList(),
    
    // Dialog states
    val showDatePickerDialog: Boolean = false,
    val showRoutePreviewDialog: Boolean = false,
    
    // Bottom sheet control triggers (reset after use)
    val shouldExpandBottomSheet: Boolean = false,
    val shouldCollapseBottomSheet: Boolean = false,
    
    // NEW: Unified booking state machine
    val bookingState: BookingState = BookingState.Idle
) {
    // Computed properties from BookingState for backward compatibility
    val availableRoutes: List<RouteInfo>
        get() = when (val state = bookingState) {
            is BookingState.RoutePreview -> state.availableRoutes
            is BookingState.FindingDriver -> listOf(state.selectedRoute)
            is BookingState.DriverFound -> listOf(state.selectedRoute)
            else -> emptyList()
        }
    
    val selectedRouteIndex: Int
        get() = when (bookingState) {
            is BookingState.RoutePreview -> bookingState.selectedRouteIndex
            else -> 0
        }
    
    val isLoadingRoute: Boolean
        get() = when (bookingState) {
            is BookingState.RoutePreview -> bookingState.isLoadingRoutes
            else -> false
        }
    
    val routeError: String?
        get() = when (bookingState) {
            is BookingState.RoutePreview -> bookingState.routeError
            else -> null
        }
    
    val isFindingDrivers: Boolean
        get() = bookingState is BookingState.FindingDriver
    
    val isDriverFound: Boolean
        get() = bookingState is BookingState.DriverFound
    
    val foundDriverInfo: DriverInfo?
        get() = when (bookingState) {
            is BookingState.DriverFound -> bookingState.driver
            else -> null
        }
    
    // Additional computed properties for legacy UI compatibility
    val showingRoutePreview: Boolean
        get() = bookingState is BookingState.RoutePreview
    
    val showingDriverSearchOverlay: Boolean
        get() = bookingState is BookingState.FindingDriver
    
    val showingDriverFoundDialog: Boolean
        get() = bookingState is BookingState.DriverFound
    
    val driverSearchInProgress: Boolean
        get() = bookingState is BookingState.FindingDriver
    
    val driverFound: Boolean
        get() = bookingState is BookingState.DriverFound
    
    // Enhanced delivery form computed from state
    val enhancedDeliveryForm: EnhancedDeliveryFormState
        get() = when (val state = bookingState) {
            is BookingState.Idle -> EnhancedDeliveryFormState()
            is BookingState.LocationsEntering -> EnhancedDeliveryFormState(
                pickupLocation = state.pickupLocation,
                dropoffLocation = state.dropoffLocation,
                scheduledDateTime = state.scheduledDateTime,
                deliveryScheduleType = state.deliveryScheduleType,
                showingRoutePreview = false
            )
            is BookingState.RoutePreview -> EnhancedDeliveryFormState(
                pickupLocation = state.pickupLocation,
                dropoffLocation = state.dropoffLocation,
                scheduledDateTime = state.scheduledDateTime,
                deliveryScheduleType = state.deliveryScheduleType,
                showingRoutePreview = true,
                isLoadingRoute = state.isLoadingRoutes,
                routePreviewError = state.routeError
            )
            is BookingState.FindingDriver -> EnhancedDeliveryFormState(
                pickupLocation = state.pickupLocation,
                dropoffLocation = state.dropoffLocation,
                scheduledDateTime = state.scheduledDateTime,
                isLoadingPricing = true
            )
            is BookingState.DriverFound -> EnhancedDeliveryFormState(
                pickupLocation = state.pickupLocation,
                dropoffLocation = state.dropoffLocation,
                scheduledDateTime = state.scheduledDateTime
            )
            is BookingState.BookingConfirmed -> EnhancedDeliveryFormState(
                pickupLocation = state.pickupLocation,
                dropoffLocation = state.dropoffLocation,
                scheduledDateTime = state.scheduledDateTime
            )
        }
    
    // Legacy delivery form for backward compatibility
    val deliveryForm: DeliveryFormState
        get() = when (val state = bookingState) {
            is BookingState.LocationsEntering -> DeliveryFormState(
                pickupLocation = state.pickupLocation,
                destination = state.dropoffLocation
            )
            is BookingState.RoutePreview -> DeliveryFormState(
                pickupLocation = state.pickupLocation,
                destination = state.dropoffLocation
            )
            is BookingState.FindingDriver -> DeliveryFormState(
                pickupLocation = state.pickupLocation,
                destination = state.dropoffLocation,
                isLoadingPricing = true
            )
            else -> DeliveryFormState()
        }
}

data class DeliveryFormState(
    val pickupLocation: String = "",
    val destination: String = "",
    val intermediateStops: List<String> = emptyList(),
    val numberOfStops: Int = 0,
    val isLoadingPricing: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    
    // Repository layer for clean architecture
    private var routingRepository: RoutingRepository? = null
    private var locationRepository: LocationRepository? = null
    private var placesRepository: PlacesRepository? = null
    
    // Enhanced routing with Google Maps
    private var newRouteRepository: RouteRepository? = null
    
    // State machine for booking flow
    private val bookingStateMachine = BookingStateMachine()
    
    // Keep track of driver search job to allow cancellation
    private var driverSearchJob: kotlinx.coroutines.Job? = null
    
    // Flag to prevent unwanted side effects during cancellation
    private var isCancellationInProgress = false
    
    /**
     * Handle booking events through the state machine
     * All booking-related state changes should go through this method
     */
    private fun handleBookingEvent(event: BookingEvent) {
        val currentState = _uiState.value.bookingState
        val transitionResult = bookingStateMachine.transition(currentState, event)
        
        // Update the booking state
        _uiState.update { uiState ->
            uiState.copy(bookingState = transitionResult.newState)
        }
        
        // Execute side effects
        transitionResult.sideEffects.forEach { sideEffect ->
            executeSideEffect(sideEffect)
        }
    }
    
    /**
     * Execute side effects from state transitions
     */
    private fun executeSideEffect(sideEffect: BookingSideEffect) {
        when (sideEffect) {
            BookingSideEffect.FetchRoutes -> {
                println("📏 executeSideEffect: FetchRoutes side effect triggered")
                    // Guard: Don't fetch routes during cancellation
                if (!isCancellationInProgress) {
                    // Try real API route fetching first, fallback to simple route if it fails
                    if (newRouteRepository != null) {
                        println("🚀 executeSideEffect: Using Google Maps route repository")
                        fetchRoutePreviewEnhanced()
                    } else {
                        println("🔄 executeSideEffect: Enhanced repository null, trying legacy")
                        // Fallback to original system if new repository isn't initialized
                        fetchRoutePreview()
                    }
                } else {
                    println("🚫 executeSideEffect: Skipped route fetch during cancellation")
                }
            }
            BookingSideEffect.StartDriverSearch -> startDriverSearch()
            BookingSideEffect.CancelDriverSearch -> cancelDriverSearchInternal()
            BookingSideEffect.ShowDriverSearchOverlay -> {
                // UI effect - already handled by state change
            }
            BookingSideEffect.HideDriverSearchOverlay -> {
                // UI effect - already handled by state change
            }
            BookingSideEffect.ShowDriverFoundDialog -> {
                // UI effect - already handled by state change
            }
            BookingSideEffect.HideDriverFoundDialog -> {
                // UI effect - already handled by state change
            }
            BookingSideEffect.NavigateToTracking -> {
                // TODO: Implement navigation to tracking screen
            }
            BookingSideEffect.ExpandBottomSheet -> {
                // Trigger UI to expand bottom sheet
                _uiState.update { it.copy(shouldExpandBottomSheet = true) }
            }
            BookingSideEffect.CollapseBottomSheet -> {
                // Trigger UI to collapse bottom sheet
                _uiState.update { it.copy(shouldCollapseBottomSheet = true) }
            }
            BookingSideEffect.ClearLocationFields -> {
                // Clear any cached location data if needed
            }
            BookingSideEffect.ClearRouteData -> {
                _uiState.update { it.copy(currentRoute = emptyList()) }
            }
            BookingSideEffect.ResetMapInteraction -> {
                _uiState.update { it.copy(mapInteractionMode = MapInteractionMode.NONE) }
            }
            is BookingSideEffect.LogStateTransition -> {
                // Debug logging for development
                val fromState = sideEffect.from::class.simpleName
                val toState = sideEffect.to::class.simpleName  
                val eventName = sideEffect.event::class.simpleName
                println("🔄 BookingState: $fromState -> $toState (via $eventName)")
                
                // TODO: Replace with actual analytics in production
                // analyticsService.track("booking_state_transition", ...)
            }
            else -> {
                // Handle other side effects as needed
            }
        }
    }
    
    /**
     * Start the driver search process
     * This is called as a side effect from the state machine
     */
    private fun startDriverSearch() {
        // Cancel any existing search
        driverSearchJob?.cancel()
        
        // Start new search
        driverSearchJob = viewModelScope.launch {
            try {
                // Simulate driver search delay (15 seconds)
                kotlinx.coroutines.delay(15000)
                
                // Generate mock driver info
                val mockDriver = DriverInfo(
                    name = "Kwame Asante",
                    rating = 4.8f,
                    vehicleType = "Toyota Camry",
                    plateNumber = "GR-4587-20",
                    phone = "+233-24-123-4567",
                    totalPrice = 25.0, // Base price
                    estimatedArrival = "5-8 mins"
                )
                
                // Simulate found driver
                handleBookingEvent(BookingEvent.DriverFound(
                    driver = mockDriver,
                    totalPrice = mockDriver.totalPrice,
                    estimatedPickup = mockDriver.estimatedArrival
                ))
                
            } catch (exception: Exception) {
                // Handle driver search error
                handleBookingEvent(BookingEvent.DriverSearchFailed("Failed to find drivers: ${exception.message}"))
            }
        }
    }
    
    /**
     * Cancel the ongoing driver search
     * This is called as a side effect from the state machine
     */
    private fun cancelDriverSearchInternal() {
        // Cancel the ongoing search coroutine
        driverSearchJob?.cancel()
        driverSearchJob = null
        println("🚫 Driver search cancelled")
    }
    
    fun initializeLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        geocoder = Geocoder(context, Locale.getDefault())
        
        // Initialize repositories
        routingRepository = ServiceLocator.getRoutingRepository(context)
        locationRepository = ServiceLocator.getLocationRepository(context)
        placesRepository = ServiceLocator.getPlacesRepository(context)
        
        // Initialize Google Maps route repository
        newRouteRepository = ServiceLocator.getNewRouteRepository(context)
        
        checkLocationPermission(context)
    }

    fun checkLocationPermission(context: Context) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        _uiState.value = _uiState.value.copy(hasLocationPermission = hasPermission)

        if (hasPermission) {
            getCurrentLocation()
        }
    }

    fun onLocationPermissionGranted() {
        _uiState.value = _uiState.value.copy(hasLocationPermission = true)
        getCurrentLocation()
    }

    fun onLocationPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            hasLocationPermission = false,
            errorMessage = "Location permission is required for the delivery app"
        )
    }

    private fun getCurrentLocation() {
        val repository = locationRepository ?: return
        
        _uiState.value = _uiState.value.copy(isLoadingLocation = true, errorMessage = null)
        
        viewModelScope.launch {
            repository.getCurrentLocation().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val latLng = repository.coordinateToLatLng(result.data)
                        _uiState.value = _uiState.value.copy(
                            currentLocation = latLng,
                            isLoadingLocation = false,
                            errorMessage = null
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingLocation = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                    is ApiResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingLocation = true,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun refreshLocation() {
        if (_uiState.value.hasLocationPermission && !_uiState.value.isLoadingLocation) {
            getCurrentLocation()
        }
    }

    // Delivery Form Functions
    fun updatePickupLocation(location: String) {
        // Update the pickup location in the booking state
        val currentBookingState = _uiState.value.bookingState
        val updatedState = when (currentBookingState) {
            is BookingState.LocationsEntering -> currentBookingState.copy(pickupLocation = location)
            is BookingState.RoutePreview -> currentBookingState.copy(pickupLocation = location)
            is BookingState.FindingDriver -> currentBookingState.copy(pickupLocation = location)
            else -> BookingState.LocationsEntering(pickupLocation = location)
        }
        
        // Update the state directly
        _uiState.update { it.copy(bookingState = updatedState) }
        
        // Trigger location change event
        handleBookingEvent(BookingEvent.PickupLocationChanged(location))
    }

    fun updateDestination(destination: String) {
        // Update the dropoff location in the booking state
        val currentBookingState = _uiState.value.bookingState
        val updatedState = when (currentBookingState) {
            is BookingState.LocationsEntering -> currentBookingState.copy(dropoffLocation = destination)
            is BookingState.RoutePreview -> currentBookingState.copy(dropoffLocation = destination)
            is BookingState.FindingDriver -> currentBookingState.copy(dropoffLocation = destination)
            else -> BookingState.LocationsEntering(dropoffLocation = destination)
        }
        
        // Update the state directly
        _uiState.update { it.copy(bookingState = updatedState) }
        
        // Trigger location change event
        handleBookingEvent(BookingEvent.DropoffLocationChanged(destination))
    }

    fun updateNumberOfStops(stops: Int) {
        // This method is deprecated - intermediate stops are not yet supported in the new state machine
        // TODO: Implement intermediate stops in BookingState when needed
        println("updateNumberOfStops called but not yet implemented in state machine")
    }

    fun updateIntermediateStop(index: Int, location: String) {
        // This method is deprecated - intermediate stops are not yet supported in the new state machine
        // TODO: Implement intermediate stops in BookingState when needed
        println("updateIntermediateStop called but not yet implemented in state machine")
    }

    fun findDriverAndPricing() {
        // Use state machine to start driver search
        handleBookingEvent(BookingEvent.FindDriverRequested)
    }
    
    // Continue to tracking screen
    fun continueToTracking() {
        // Use state machine to confirm booking
        handleBookingEvent(BookingEvent.BookingConfirmed)
    }
    
    // Cancel driver booking
    fun cancelDriverBooking() {
        val currentState = _uiState.value.bookingState
        println("🚫 cancelDriverBooking called from state: ${currentState::class.simpleName}")
        
        // Set cancellation context to prevent unwanted side effects
        isCancellationInProgress = true
        
        try {
            // Use correct event based on current state
            when (currentState) {
                is BookingState.FindingDriver -> {
                    println("🔄 Sending CancelPressed event during driver search")
                    handleBookingEvent(BookingEvent.CancelPressed)
                }
                is BookingState.DriverFound -> {
                    println("🔄 Sending BookingCancelled event when driver found")
                    handleBookingEvent(BookingEvent.BookingCancelled)
                }
                else -> {
                    println("🔄 Fallback: Sending BackPressed event from state ${currentState::class.simpleName}")
                    handleBookingEvent(BookingEvent.BackPressed)
                }
            }
        } finally {
            // Always reset cancellation context
            isCancellationInProgress = false
        }
    }
    
    // Public methods for UI to trigger booking events
    
    fun requestDriverSearch() {
        handleBookingEvent(BookingEvent.FindDriverRequested)
    }
    
    fun cancelDriverSearch() {
        handleBookingEvent(BookingEvent.CancelPressed)
    }
    
    fun showRoutePreview() {
        handleBookingEvent(BookingEvent.RoutesRequested)
    }
    
    fun hideRoutePreview() {
        handleBookingEvent(BookingEvent.BackPressed)
    }
    
    fun confirmDriver() {
        handleBookingEvent(BookingEvent.BookingConfirmed)
    }
    
    fun resetBooking() {
        handleBookingEvent(BookingEvent.ClearAllFields)
    }
    
    // Methods to reset UI triggers after they've been handled
    fun onBottomSheetExpanded() {
        _uiState.update { it.copy(shouldExpandBottomSheet = false) }
    }
    
    fun onBottomSheetCollapsed() {
        _uiState.update { it.copy(shouldCollapseBottomSheet = false) }
    }
    
    // MARK: - Map Interaction Functions
    
    // Set map interaction mode (pickup/dropoff selection)
    fun setMapInteractionMode(mode: MapInteractionMode) {
        _uiState.update { it.copy(mapInteractionMode = mode) }
    }
    
    // Cancel map selection mode
    fun cancelMapSelection() {
        _uiState.update { it.copy(mapInteractionMode = MapInteractionMode.NONE) }
    }
    
    // Handle location selection from map
    fun handleLocationSelection(coordinate: LocationCoordinate) {
        val repository = locationRepository ?: return
        
        viewModelScope.launch {
            // Get geocoded address using repository
            repository.reverseGeocode(coordinate).collect { result ->
                val formattedAddress = when (result) {
                    is ApiResult.Success -> result.data
                    else -> "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
                }
                
                // Update booking state based on map interaction mode
                when (_uiState.value.mapInteractionMode) {
                    MapInteractionMode.SELECTING_PICKUP -> {
                        val currentBookingState = _uiState.value.bookingState
                        val updatedState = when (currentBookingState) {
                            is BookingState.LocationsEntering -> currentBookingState.copy(
                                pickupLocation = formattedAddress,
                                pickupCoordinate = coordinate
                            )
                            is BookingState.RoutePreview -> currentBookingState.copy(
                                pickupLocation = formattedAddress,
                                pickupCoordinate = coordinate
                            )
                            else -> BookingState.LocationsEntering(
                                pickupLocation = formattedAddress,
                                pickupCoordinate = coordinate
                            )
                        }
                        _uiState.update { it.copy(bookingState = updatedState) }
                        handleBookingEvent(BookingEvent.PickupLocationChanged(formattedAddress))
                    }
                    MapInteractionMode.SELECTING_DROPOFF -> {
                        val currentBookingState = _uiState.value.bookingState
                        val updatedState = when (currentBookingState) {
                            is BookingState.LocationsEntering -> currentBookingState.copy(
                                dropoffLocation = formattedAddress,
                                dropoffCoordinate = coordinate
                            )
                            is BookingState.RoutePreview -> currentBookingState.copy(
                                dropoffLocation = formattedAddress,
                                dropoffCoordinate = coordinate
                            )
                            else -> BookingState.LocationsEntering(
                                dropoffLocation = formattedAddress,
                                dropoffCoordinate = coordinate
                            )
                        }
                        _uiState.update { it.copy(bookingState = updatedState) }
                        handleBookingEvent(BookingEvent.DropoffLocationChanged(formattedAddress))
                    }
                    else -> { /* No action needed */ }
                }
                
                // Reset interaction mode
                cancelMapSelection()
            }
        }
    }
    
    // Handle POI selection from map
    fun handlePOISelection(poi: PointOfInterest) {
        when (_uiState.value.mapInteractionMode) {
            MapInteractionMode.SELECTING_PICKUP -> {
                val currentBookingState = _uiState.value.bookingState
                val updatedState = when (currentBookingState) {
                    is BookingState.LocationsEntering -> currentBookingState.copy(
                        pickupLocation = poi.name,
                        pickupCoordinate = poi.coordinate
                    )
                    is BookingState.RoutePreview -> currentBookingState.copy(
                        pickupLocation = poi.name,
                        pickupCoordinate = poi.coordinate
                    )
                    else -> BookingState.LocationsEntering(
                        pickupLocation = poi.name,
                        pickupCoordinate = poi.coordinate
                    )
                }
                _uiState.update { it.copy(bookingState = updatedState) }
                handleBookingEvent(BookingEvent.PickupLocationChanged(poi.name))
            }
            MapInteractionMode.SELECTING_DROPOFF -> {
                val currentBookingState = _uiState.value.bookingState
                val updatedState = when (currentBookingState) {
                    is BookingState.LocationsEntering -> currentBookingState.copy(
                        dropoffLocation = poi.name,
                        dropoffCoordinate = poi.coordinate
                    )
                    is BookingState.RoutePreview -> currentBookingState.copy(
                        dropoffLocation = poi.name,
                        dropoffCoordinate = poi.coordinate
                    )
                    else -> BookingState.LocationsEntering(
                        dropoffLocation = poi.name,
                        dropoffCoordinate = poi.coordinate
                    )
                }
                _uiState.update { it.copy(bookingState = updatedState) }
                handleBookingEvent(BookingEvent.DropoffLocationChanged(poi.name))
            }
            else -> { /* No action needed */ }
        }
        
        // Reset interaction mode
        cancelMapSelection()
    }
    
    // Helper function to reverse geocode coordinates to address
    private fun reverseGeocode(coordinate: LocationCoordinate, callback: (String) -> Unit) {
        val geoc = geocoder ?: run {
            callback("Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})")
            return
        }
        
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                geoc.getFromLocation(coordinate.latitude, coordinate.longitude, 1) { addresses ->
                    val formattedAddress = if (addresses.isNotEmpty()) {
                        formatAddress(addresses, coordinate)
                    } else {
                        "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
                    }
                    callback(formattedAddress)
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geoc.getFromLocation(coordinate.latitude, coordinate.longitude, 1)
                val formattedAddress = if (addresses != null && addresses.isNotEmpty()) {
                    formatAddress(addresses, coordinate)
                } else {
                    "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
                }
                callback(formattedAddress)
            }
        } catch (e: Exception) {
            callback("Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})")
        }
    }
    
    // Format address from geocoding results
    private fun formatAddress(addresses: List<Address>, coordinate: LocationCoordinate): String {
        if (addresses.isEmpty()) {
            return "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
        }
        
        val address = addresses[0]
        val addressComponents = mutableListOf<String>()
        
        // Check if we have a landmark or POI name
        address.featureName?.let { featureName ->
            val locality = address.locality ?: ""
            val subLocality = address.subLocality ?: ""
            val thoroughfare = address.thoroughfare ?: ""
            
            // Check if feature name is a landmark (not just a street address)
            val isLandmark = !featureName.contains(thoroughfare) &&
                    featureName != locality &&
                    featureName != subLocality &&
                    featureName.length > 3 &&
                    (featureName.isEmpty() || !featureName[0].isDigit())
            
            if (isLandmark) {
                addressComponents.add(featureName)
                // Add context location
                if (subLocality.isNotEmpty() && subLocality != featureName) {
                    addressComponents.add(subLocality)
                } else if (locality.isNotEmpty() && locality != featureName) {
                    addressComponents.add(locality)
                }
                return addressComponents.joinToString(", ")
            }
        }
        
        // Regular address formatting
        address.subThoroughfare?.let { number ->
            address.thoroughfare?.let { street ->
                addressComponents.add("$number $street")
            }
        } ?: address.thoroughfare?.let { street ->
            addressComponents.add(street)
        }
        
        address.subLocality?.let { addressComponents.add(it) }
        address.locality?.let { addressComponents.add(it) }
        
        if (addressComponents.isEmpty()) {
            address.featureName?.let { addressComponents.add(it) }
        }
        
        return if (addressComponents.isEmpty()) {
            "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
        } else {
            // Limit to first 2 components for readability
            val finalComponents = addressComponents.take(2)
            val baseAddress = finalComponents.joinToString(", ")
            
            // Add coordinates for less precise addresses
            if (address.subThoroughfare != null && address.thoroughfare != null) {
                baseAddress
            } else {
                "$baseAddress (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
            }
        }
    }
    
    // Enhanced route calculation using Google Maps
    private fun fetchRoutePreviewEnhanced() {
        println("🗺️ fetchRoutePreviewEnhanced: Starting enhanced route fetch")
        val bookingState = _uiState.value.bookingState
        
        val (pickupCoord, dropoffCoord) = when (bookingState) {
            is BookingState.LocationsEntering -> Pair(bookingState.pickupCoordinate, bookingState.dropoffCoordinate)
            is BookingState.RoutePreview -> Pair(bookingState.pickupCoordinate, bookingState.dropoffCoordinate)
            is BookingState.FindingDriver -> {
                val firstPoint = bookingState.selectedRoute.polylinePoints.firstOrNull()
                val lastPoint = bookingState.selectedRoute.polylinePoints.lastOrNull()
                Pair(firstPoint, lastPoint)
            }
            else -> Pair(null, null)
        }
        
        if (pickupCoord == null || dropoffCoord == null) {
            println("❌ fetchRoutePreviewEnhanced: Missing coordinates - pickup: $pickupCoord, dropoff: $dropoffCoord")
            return
        }
        
        println("📍 fetchRoutePreviewEnhanced: Pickup: ${pickupCoord.latitude}, ${pickupCoord.longitude}")
        println("📍 fetchRoutePreviewEnhanced: Dropoff: ${dropoffCoord.latitude}, ${dropoffCoord.longitude}")
        
        viewModelScope.launch {
            val repository = newRouteRepository
            if (repository == null) {
                println("⚠️ fetchRoutePreviewEnhanced: newRouteRepository is null, falling back to legacy")
                fetchRoutePreview() // Fallback to legacy system
                return@launch
            }
            
            try {
                println("🔧 fetchRoutePreviewEnhanced: Creating route request")
                // Create route request for our new system
                val routeRequest = NewRouteRequest(
                    origin = com.google.android.gms.maps.model.LatLng(
                        pickupCoord.latitude,
                        pickupCoord.longitude
                    ),
                    destination = com.google.android.gms.maps.model.LatLng(
                        dropoffCoord.latitude,
                        dropoffCoord.longitude
                    ),
                    transportMode = TransportMode.CAR // Default to car for delivery
                )
                
                println("🚀 fetchRoutePreviewEnhanced: Calling repository.calculateRoute()")
                // Using Google Maps routing
                when (val result = repository.calculateRoute(routeRequest)) {
                    is NewApiResult.Success -> {
                        val route = result.data
                        println("✅ fetchRoutePreviewEnhanced: Route calculated successfully!")
                        println("📏 Route distance: ${route.distanceMeters}m, duration: ${route.durationSeconds}s")
                        println("🧭 Route coordinates: ${route.coordinates.size} points")
                        println("🏷️ Route provider: ${route.provider}")
                        
                        // Convert to our RouteInfo format
                        val routeInfo = RouteInfo(
                            id = "route_${System.currentTimeMillis()}",
                            distance = route.distanceMeters.toInt(),
                            duration = route.durationSeconds,
                            polylinePoints = route.coordinates.map { latLng ->
                                LocationCoordinate(
                                    latitude = latLng.latitude,
                                    longitude = latLng.longitude
                                )
                            },
                            trafficDelay = null,
                            routeType = RouteType.FAST,
                            summary = "Route via ${route.provider.name}"
                        )
                        
                        println("🎯 fetchRoutePreviewEnhanced: RouteInfo created with ${routeInfo.polylinePoints.size} polyline points")
                        
                        // Update UI state
                        _uiState.update { state ->
                            state.copy(
                                currentRoute = routeInfo.polylinePoints
                            )
                        }
                        
                        println("📱 fetchRoutePreviewEnhanced: UI state updated, triggering RoutesLoaded event")
                        // Trigger state machine
                        handleBookingEvent(BookingEvent.RoutesLoaded(listOf(routeInfo)))
                    }
                    is NewApiResult.Error -> {
                        println("❌ fetchRoutePreviewEnhanced: Route calculation failed - ${result.message}")
                        println("🔄 fetchRoutePreviewEnhanced: Falling back to simple route")
                        
                        // Auto-fallback to simple route when API fails
                        val bookingState = _uiState.value.bookingState
                        val (pickupCoord, dropoffCoord) = when (bookingState) {
                            is BookingState.LocationsEntering -> Pair(bookingState.pickupCoordinate, bookingState.dropoffCoordinate)
                            is BookingState.RoutePreview -> Pair(bookingState.pickupCoordinate, bookingState.dropoffCoordinate)
                            else -> Pair(null, null)
                        }
                        
                        if (pickupCoord != null && dropoffCoord != null) {
                            fallbackToSimpleRoute(pickupCoord, dropoffCoord, "API failed: ${result.message}")
                        } else {
                            handleBookingEvent(BookingEvent.RoutesLoadFailed(result.message))
                        }
                    }
                    is NewApiResult.Loading -> {
                        println("⏳ fetchRoutePreviewEnhanced: Loading state from repository")
                        // Loading state is handled by the state machine
                    }
                }
            } catch (e: Exception) {
                println("⚠️ fetchRoutePreviewEnhanced: Exception caught - ${e.message}")
                handleBookingEvent(BookingEvent.RoutesLoadFailed("Route calculation error: ${e.message}"))
            }
        }
    }
    
    // Original route preview using existing system (keep for compatibility)
    private fun fetchRoutePreview() {
        println("🗺️ fetchRoutePreview: Starting legacy route fetch")
        val bookingState = _uiState.value.bookingState
        
        val (pickupCoord, dropoffCoord) = when (bookingState) {
            is BookingState.LocationsEntering -> Pair(bookingState.pickupCoordinate, bookingState.dropoffCoordinate)
            is BookingState.RoutePreview -> Pair(bookingState.pickupCoordinate, bookingState.dropoffCoordinate)
            is BookingState.FindingDriver -> {
                // For FindingDriver state, get coordinates from the selected route
                val firstPoint = bookingState.selectedRoute.polylinePoints.firstOrNull()
                val lastPoint = bookingState.selectedRoute.polylinePoints.lastOrNull()
                Pair(firstPoint, lastPoint)
            }
            else -> Pair(null, null)
        }
        
        if (pickupCoord == null || dropoffCoord == null) {
            println("❌ fetchRoutePreview: Missing coordinates - pickup: $pickupCoord, dropoff: $dropoffCoord")
            return
        }
        
        println("📍 fetchRoutePreview: Pickup: ${pickupCoord.latitude}, ${pickupCoord.longitude}")
        println("📍 fetchRoutePreview: Dropoff: ${dropoffCoord.latitude}, ${dropoffCoord.longitude}")
        
        // State machine will handle loading states through computed properties
        
        viewModelScope.launch {
            val repository = routingRepository
            if (repository == null) {
                println("⚠️ fetchRoutePreview: routingRepository is null, creating fallback route")
                fallbackToSimpleRoute(pickupCoord, dropoffCoord, "No routing service available")
                return@launch
            }
            
            println("🚀 fetchRoutePreview: Calling routingRepository.getRouteAlternatives()")
            repository.getRouteAlternatives(
                origin = pickupCoord,
                destination = dropoffCoord,
                waypoints = emptyList() // TODO: Add intermediate stops support when needed
            ).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val routes = result.data
                        println("✅ fetchRoutePreview: Received ${routes.size} routes from legacy system")
                        if (routes.isNotEmpty()) {
                            val selectedRoute = routes[0] // Default to first (fastest) route
                            println("📱 fetchRoutePreview: Selected route has ${selectedRoute.polylinePoints.size} points")
                            
                            // Trigger state machine to handle loaded routes
                            handleBookingEvent(BookingEvent.RoutesLoaded(routes))
                            
                            // Update current route for display
                            _uiState.update { state ->
                                state.copy(currentRoute = selectedRoute.polylinePoints)
                            }
                            
                            println("🎯 fetchRoutePreview: UI state updated with route polyline")
                        } else {
                            println("❌ fetchRoutePreview: No routes found")
                            // No routes found
                            handleBookingEvent(BookingEvent.RoutesLoadFailed("No routes found"))
                        }
                    }
                    is ApiResult.Error -> {
                        println("❌ fetchRoutePreview: Routing failed with error: ${result.error.userMessage}")
                        println("🔄 fetchRoutePreview: Falling back to simple route")
                        
                        // Auto-fallback to simple route when legacy API fails
                        fallbackToSimpleRoute(pickupCoord, dropoffCoord, "Legacy API failed: ${result.error.userMessage}")
                    }
                    is ApiResult.Loading -> {
                        println("⏳ fetchRoutePreview: Loading routes...")
                        // Loading state is handled by the state machine
                    }
                }
            }
        }
    }
    
    // Note: Fallback logic is now handled by RoutingRepository
    
    private fun fallbackToSimpleRoute(pickupCoord: LocationCoordinate, dropoffCoord: LocationCoordinate, errorMessage: String) {
        println("🔄 fallbackToSimpleRoute: Creating simple direct route fallback")
        println("📍 Fallback route: ${pickupCoord.latitude}, ${pickupCoord.longitude} -> ${dropoffCoord.latitude}, ${dropoffCoord.longitude}")
        
        // Show simple direct route for fallback
        val simpleRoute = listOf(pickupCoord, dropoffCoord)
        _uiState.update { state ->
            state.copy(currentRoute = simpleRoute)
        }
        
        println("🎯 fallbackToSimpleRoute: Updated UI state with ${simpleRoute.size} route points")
        
        // Create a simple RouteInfo for the direct path
        val simpleRouteInfo = RouteInfo(
            id = "fallback_route_${System.currentTimeMillis()}",
            distance = calculateDirectDistance(pickupCoord, dropoffCoord),
            duration = 300, // 5 minutes estimated
            polylinePoints = simpleRoute,
            trafficDelay = null,
            routeType = RouteType.FAST,
            summary = "Direct path (fallback)"
        )
        
        // Trigger state machine with fallback route instead of error
        handleBookingEvent(BookingEvent.RoutesLoaded(listOf(simpleRouteInfo)))
        
        println("📱 fallbackToSimpleRoute: Triggered RoutesLoaded with fallback route")
    }
    
    private fun calculateDirectDistance(pickup: LocationCoordinate, dropoff: LocationCoordinate): Int {
        val R = 6371000 // Earth's radius in meters
        val lat1Rad = Math.toRadians(pickup.latitude)
        val lat2Rad = Math.toRadians(dropoff.latitude)
        val deltaLatRad = Math.toRadians(dropoff.latitude - pickup.latitude)
        val deltaLngRad = Math.toRadians(dropoff.longitude - pickup.longitude)
        
        val a = kotlin.math.sin(deltaLatRad / 2) * kotlin.math.sin(deltaLatRad / 2) +
                kotlin.math.cos(lat1Rad) * kotlin.math.cos(lat2Rad) *
                kotlin.math.sin(deltaLngRad / 2) * kotlin.math.sin(deltaLngRad / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        
        return (R * c).toInt()
    }
    
    // Select a different route from available alternatives
    fun selectRoute(routeIndex: Int) {
        val routes = _uiState.value.availableRoutes
        if (routeIndex < routes.size) {
            val selectedRoute = routes[routeIndex]
            // Update the current route display
            _uiState.update { state ->
                state.copy(currentRoute = selectedRoute.polylinePoints)
            }
            // Notify state machine of route selection
            handleBookingEvent(BookingEvent.RouteSelected(routeIndex))
        }
    }
    
    // MARK: - Route Preview Dialog Functions
    
    // Show route preview dialog
    fun showRoutePreviewDialog() {
        _uiState.update { state ->
            state.copy(showRoutePreviewDialog = true)
        }
    }
    
    // Hide route preview dialog (keeps route data)
    fun hideRoutePreviewDialog() {
        _uiState.update { state ->
            state.copy(showRoutePreviewDialog = false)
        }
    }
    
    // Close route preview dialog (same as hide, for clarity)
    fun closeRoutePreviewDialog() {
        hideRoutePreviewDialog()
    }
    
    // Cancel route - clear all route data and reset state
    fun cancelRoute() {
        // Use state machine to hide route preview
        handleBookingEvent(BookingEvent.BackPressed)
    }
    
    // Confirm route - keep route data but close dialog (ready for next step like driver selection)
    fun confirmRoute() {
        // Close dialog but keep route data
        _uiState.update { state ->
            state.copy(showRoutePreviewDialog = false)
        }
        
        // TODO: Navigate to driver selection/booking confirmation screen
        // This could trigger a state transition if needed
    }
    
    // Calculate estimated pricing based on route info
    private fun calculateEstimatedPrice(route: RouteInfo): Double {
        // Basic pricing calculation - in production this would be more sophisticated
        val basePrice = 5.0 // Base price in Ghana Cedis
        val pricePerKm = 2.0
        val distanceInKm = route.distance / 1000.0
        val timeMultiplier = if (route.hasTraffic) 1.2 else 1.0
        
        return (basePrice + (distanceInKm * pricePerKm)) * timeMultiplier
    }
    
    // Handle place selection from autocomplete for pickup
    fun updatePickupLocationFromPlace(placeDetails: PlaceDetails) {
        println("📍 updatePickupLocationFromPlace: ${placeDetails.name} at (${placeDetails.latitude}, ${placeDetails.longitude})")
        
        // Update the pickup location in the booking state
        val currentBookingState = _uiState.value.bookingState
        val updatedState = when (currentBookingState) {
            is BookingState.LocationsEntering -> currentBookingState.copy(
                pickupLocation = placeDetails.name,
                pickupCoordinate = LocationCoordinate(
                    latitude = placeDetails.latitude,
                    longitude = placeDetails.longitude
                )
            )
            is BookingState.RoutePreview -> currentBookingState.copy(
                pickupLocation = placeDetails.name,
                pickupCoordinate = LocationCoordinate(
                    latitude = placeDetails.latitude,
                    longitude = placeDetails.longitude
                )
            )
            else -> BookingState.LocationsEntering(
                pickupLocation = placeDetails.name,
                pickupCoordinate = LocationCoordinate(
                    latitude = placeDetails.latitude,
                    longitude = placeDetails.longitude
                )
            )
        }
        
        // Update the state directly
        _uiState.update { it.copy(bookingState = updatedState) }
        
        // Trigger location change event
        handleBookingEvent(BookingEvent.PickupLocationChanged(placeDetails.name))
    }
    
    // Handle place selection from autocomplete for dropoff
    fun updateDropoffLocationFromPlace(placeDetails: PlaceDetails) {
        println("📍 updateDropoffLocationFromPlace: ${placeDetails.name} at (${placeDetails.latitude}, ${placeDetails.longitude})")
        
        // Update the dropoff location in the booking state
        val currentBookingState = _uiState.value.bookingState
        val updatedState = when (currentBookingState) {
            is BookingState.LocationsEntering -> currentBookingState.copy(
                dropoffLocation = placeDetails.name,
                dropoffCoordinate = LocationCoordinate(
                    latitude = placeDetails.latitude,
                    longitude = placeDetails.longitude
                )
            )
            is BookingState.RoutePreview -> currentBookingState.copy(
                dropoffLocation = placeDetails.name,
                dropoffCoordinate = LocationCoordinate(
                    latitude = placeDetails.latitude,
                    longitude = placeDetails.longitude
                )
            )
            else -> BookingState.LocationsEntering(
                dropoffLocation = placeDetails.name,
                dropoffCoordinate = LocationCoordinate(
                    latitude = placeDetails.latitude,
                    longitude = placeDetails.longitude
                )
            )
        }
        
        // Update the state directly
        _uiState.update { it.copy(bookingState = updatedState) }
        
        // Trigger location change event
        handleBookingEvent(BookingEvent.DropoffLocationChanged(placeDetails.name))
    }
    
    // MARK: - Delivery Schedule Functions
    
    // Update delivery schedule type (Now/Schedule)
    fun updateDeliveryScheduleType(scheduleType: DeliveryScheduleType) {
        val currentBookingState = _uiState.value.bookingState
        val updatedState = when (currentBookingState) {
            is BookingState.LocationsEntering -> currentBookingState.copy(
                deliveryScheduleType = scheduleType,
                scheduledDateTime = if (scheduleType == DeliveryScheduleType.NOW) null else currentBookingState.scheduledDateTime
            )
            is BookingState.RoutePreview -> currentBookingState.copy(
                deliveryScheduleType = scheduleType,
                scheduledDateTime = if (scheduleType == DeliveryScheduleType.NOW) null else currentBookingState.scheduledDateTime
            )
            else -> BookingState.LocationsEntering(deliveryScheduleType = scheduleType)
        }
        
        _uiState.update { it.copy(bookingState = updatedState) }
    }
    
    // Update scheduled delivery date/time
    fun updateScheduledDateTime(dateTimeMillis: Long) {
        val currentBookingState = _uiState.value.bookingState
        val updatedState = when (currentBookingState) {
            is BookingState.LocationsEntering -> currentBookingState.copy(
                deliveryScheduleType = DeliveryScheduleType.SCHEDULED,
                scheduledDateTime = dateTimeMillis
            )
            is BookingState.RoutePreview -> currentBookingState.copy(
                deliveryScheduleType = DeliveryScheduleType.SCHEDULED,
                scheduledDateTime = dateTimeMillis
            )
            else -> BookingState.LocationsEntering(
                deliveryScheduleType = DeliveryScheduleType.SCHEDULED,
                scheduledDateTime = dateTimeMillis
            )
        }
        
        _uiState.update { state ->
            state.copy(
                bookingState = updatedState,
                showDatePickerDialog = false // Close dialog after selection
            )
        }
    }
    
    // Show date picker dialog
    fun showDatePickerDialog() {
        _uiState.update { state ->
            state.copy(showDatePickerDialog = true)
        }
    }
    
    // Hide date picker dialog
    fun hideDatePickerDialog() {
        _uiState.update { state ->
            state.copy(showDatePickerDialog = false)
        }
    }
    
    // Clear all delivery fields and route data
    fun clearDeliveryFields() {
        // Use state machine to reset booking
        handleBookingEvent(BookingEvent.ClearAllFields)
    }
    
    // Clear only route data to return to form view
    fun clearRoutePreview() {
        // Use state machine to hide route preview
        handleBookingEvent(BookingEvent.BackPressed)
    }
    
    // Manual test function for debugging route fetching
    fun testRouteManually() {
        println("🧪 testRouteManually: Starting manual route test")
        
        // Set test coordinates (Accra locations)
        val pickupCoord = LocationCoordinate(5.6037, -0.1870) // Central Accra
        val dropoffCoord = LocationCoordinate(5.6052, -0.1669) // Kotoka Airport
        
        // Update state with test locations
        val testState = BookingState.LocationsEntering(
            pickupLocation = "Central Accra",
            dropoffLocation = "Kotoka International Airport",
            pickupCoordinate = pickupCoord,
            dropoffCoordinate = dropoffCoord
        )
        
        _uiState.update { it.copy(bookingState = testState) }
        
        println("🧪 testRouteManually: State updated, triggering route fetch")
        
        // Directly call the route fetching methods
        if (newRouteRepository != null) {
            println("🧪 testRouteManually: Using Google Maps route repository")
            fetchRoutePreviewEnhanced()
        } else {
            println("🧪 testRouteManually: Using legacy route repository")
            fetchRoutePreview()
        }
    }
}
