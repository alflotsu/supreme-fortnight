package io.peng.sparrowdelivery.presentation.features.home

import io.peng.sparrowdelivery.data.services.RouteInfo
import io.peng.sparrowdelivery.data.services.PlaceDetails

/**
 * BookingState - Represents all possible states in the delivery booking flow
 * Uses sealed classes for type safety and data association
 */
sealed class BookingState {
    /**
     * Initial state - empty form, no locations entered
     */
    object Idle : BookingState()
    
    /**
     * User is entering locations (pickup and/or dropoff)
     * Can have partial data (only pickup or only dropoff)
     */
    data class LocationsEntering(
        val pickupLocation: String = "",
        val dropoffLocation: String = "",
        val pickupCoordinate: LocationCoordinate? = null,
        val dropoffCoordinate: LocationCoordinate? = null,
        val scheduledDateTime: Long? = null,
        val deliveryScheduleType: DeliveryScheduleType = DeliveryScheduleType.NOW
    ) : BookingState() {
        val hasValidLocations: Boolean
            get() = pickupLocation.isNotBlank() && dropoffLocation.isNotBlank()
        
        val canProceedToRoutePreview: Boolean
            get() = hasValidLocations && pickupCoordinate != null && dropoffCoordinate != null
    }
    
    /**
     * Both locations entered, showing route options
     * User can select different routes and see pricing
     */
    data class RoutePreview(
        val pickupLocation: String,
        val dropoffLocation: String,
        val pickupCoordinate: LocationCoordinate,
        val dropoffCoordinate: LocationCoordinate,
        val availableRoutes: List<RouteInfo>,
        val selectedRouteIndex: Int = 0,
        val isLoadingRoutes: Boolean = false,
        val routeError: String? = null,
        val scheduledDateTime: Long? = null,
        val deliveryScheduleType: DeliveryScheduleType = DeliveryScheduleType.NOW
    ) : BookingState() {
        val selectedRoute: RouteInfo?
            get() = availableRoutes.getOrNull(selectedRouteIndex)
        
        val estimatedPrice: Double
            get() = calculateEstimatedPrice()
            
        private fun calculateEstimatedPrice(): Double {
            val basePrice = 15.0 // Base delivery fee
            return selectedRoute?.let { route ->
                val distancePrice = (route.distance / 1000.0) * 2.0 // ₵2 per km
                val trafficMultiplier = if (route.hasTraffic) 1.2 else 1.0
                (basePrice + distancePrice) * trafficMultiplier
            } ?: basePrice
        }
    }
    
    /**
     * User clicked "Find Driver", actively searching for available drivers
     * Shows loading overlay with trip details and cancel option
     */
    data class FindingDriver(
        val pickupLocation: String,
        val dropoffLocation: String,
        val selectedRoute: RouteInfo,
        val estimatedPrice: Double,
        val searchStartTime: Long = System.currentTimeMillis(),
        val scheduledDateTime: Long? = null
    ) : BookingState() {
        val searchDurationMs: Long
            get() = System.currentTimeMillis() - searchStartTime
    }
    
    /**
     * Driver found and matched to the request
     * Shows driver details and allows user to proceed or cancel
     */
    data class DriverFound(
        val pickupLocation: String,
        val dropoffLocation: String,
        val selectedRoute: RouteInfo,
        val driver: DriverInfo,
        val totalPrice: Double,
        val estimatedPickupTime: String,
        val scheduledDateTime: Long? = null
    ) : BookingState()
    
    /**
     * User confirmed the driver booking
     * Transitioning to tracking/in-progress flow
     */
    data class BookingConfirmed(
        val pickupLocation: String,
        val dropoffLocation: String,
        val driver: DriverInfo,
        val totalPrice: Double,
        val bookingId: String,
        val scheduledDateTime: Long? = null
    ) : BookingState()
}

/**
 * BookingEvent - All possible user actions and system events
 * Drives state transitions in the booking flow
 */
sealed class BookingEvent {
    // Location input events
    data class PickupLocationChanged(val location: String) : BookingEvent()
    data class DropoffLocationChanged(val location: String) : BookingEvent()
    data class PickupLocationSelected(val placeDetails: PlaceDetails) : BookingEvent()
    data class DropoffLocationSelected(val placeDetails: PlaceDetails) : BookingEvent()
    data class LocationSelectedFromMap(
        val coordinate: LocationCoordinate,
        val address: String,
        val isPickup: Boolean
    ) : BookingEvent()
    
    // Scheduling events
    data class ScheduleTypeChanged(val scheduleType: DeliveryScheduleType) : BookingEvent()
    data class ScheduledDateTimeChanged(val dateTimeMillis: Long) : BookingEvent()
    
    // Route events
    object RoutesRequested : BookingEvent()
    data class RoutesLoaded(val routes: List<RouteInfo>) : BookingEvent()
    data class RoutesLoadFailed(val error: String) : BookingEvent()
    data class RouteSelected(val routeIndex: Int) : BookingEvent()
    
    // Driver search events
    object FindDriverRequested : BookingEvent()
    data class DriverFound(val driver: DriverInfo, val totalPrice: Double, val estimatedPickup: String) : BookingEvent()
    data class DriverSearchFailed(val error: String) : BookingEvent()
    
    // Navigation/cancellation events
    object BackPressed : BookingEvent()
    object CancelPressed : BookingEvent()
    object ClearAllFields : BookingEvent()
    
    // Booking confirmation events
    object BookingConfirmed : BookingEvent()
    object BookingCancelled : BookingEvent()
}

/**
 * BookingSideEffect - Side effects that should happen during state transitions
 * These are actions that affect the outside world (UI updates, API calls, etc.)
 */
sealed class BookingSideEffect {
    // API calls
    object FetchRoutes : BookingSideEffect()
    object StartDriverSearch : BookingSideEffect()
    object CancelDriverSearch : BookingSideEffect()
    
    // UI effects
    object ShowRoutePreview : BookingSideEffect()
    object HideRoutePreview : BookingSideEffect()
    object ShowDriverSearchOverlay : BookingSideEffect()
    object HideDriverSearchOverlay : BookingSideEffect()
    object ShowDriverFoundDialog : BookingSideEffect()
    object HideDriverFoundDialog : BookingSideEffect()
    object NavigateToTracking : BookingSideEffect()
    object ExpandBottomSheet : BookingSideEffect()
    object CollapseBottomSheet : BookingSideEffect()
    
    // Cleanup effects
    object ClearLocationFields : BookingSideEffect()
    object ClearRouteData : BookingSideEffect()
    object ResetMapInteraction : BookingSideEffect()
    
    // Analytics/logging
    data class LogStateTransition(val from: BookingState, val to: BookingState, val event: BookingEvent) : BookingSideEffect()
}

/**
 * StateTransitionResult - Result of a state transition
 * Contains the new state and any side effects to execute
 */
data class StateTransitionResult(
    val newState: BookingState,
    val sideEffects: List<BookingSideEffect> = emptyList()
)

/**
 * BookingStateMachine - Core state machine logic
 * Handles all state transitions and ensures consistency
 */
class BookingStateMachine {
    
    /**
     * Process an event and return the new state with side effects
     */
    fun transition(currentState: BookingState, event: BookingEvent): StateTransitionResult {
        val result = when (currentState) {
            is BookingState.Idle -> handleIdleState(event)
            is BookingState.LocationsEntering -> handleLocationsEnteringState(currentState, event)
            is BookingState.RoutePreview -> handleRoutePreviewState(currentState, event)
            is BookingState.FindingDriver -> handleFindingDriverState(currentState, event)
            is BookingState.DriverFound -> handleDriverFoundState(currentState, event)
            is BookingState.BookingConfirmed -> handleBookingConfirmedState(currentState, event)
        }
        
        // Always add logging side effect
        val sideEffectsWithLogging = result.sideEffects + BookingSideEffect.LogStateTransition(
            from = currentState,
            to = result.newState,
            event = event
        )
        
        return result.copy(sideEffects = sideEffectsWithLogging)
    }
    
    private fun handleIdleState(event: BookingEvent): StateTransitionResult {
        return when (event) {
            is BookingEvent.PickupLocationChanged -> {
                StateTransitionResult(
                    newState = BookingState.LocationsEntering(pickupLocation = event.location)
                )
            }
            is BookingEvent.DropoffLocationChanged -> {
                StateTransitionResult(
                    newState = BookingState.LocationsEntering(dropoffLocation = event.location)
                )
            }
            is BookingEvent.PickupLocationSelected -> {
                StateTransitionResult(
                    newState = BookingState.LocationsEntering(
                        pickupLocation = event.placeDetails.name,
                        pickupCoordinate = LocationCoordinate(
                            latitude = event.placeDetails.latitude,
                            longitude = event.placeDetails.longitude
                        )
                    )
                )
            }
            is BookingEvent.DropoffLocationSelected -> {
                StateTransitionResult(
                    newState = BookingState.LocationsEntering(
                        dropoffLocation = event.placeDetails.name,
                        dropoffCoordinate = LocationCoordinate(
                            latitude = event.placeDetails.latitude,
                            longitude = event.placeDetails.longitude
                        )
                    )
                )
            }
            else -> StateTransitionResult(BookingState.Idle) // No state change
        }
    }
    
    private fun handleLocationsEnteringState(
        currentState: BookingState.LocationsEntering,
        event: BookingEvent
    ): StateTransitionResult {
        return when (event) {
            is BookingEvent.PickupLocationChanged -> {
                StateTransitionResult(
                    newState = currentState.copy(
                        pickupLocation = event.location,
                        pickupCoordinate = if (event.location.isBlank()) null else currentState.pickupCoordinate
                    )
                )
            }
            
            is BookingEvent.DropoffLocationChanged -> {
                val newState = currentState.copy(
                    dropoffLocation = event.location,
                    dropoffCoordinate = if (event.location.isBlank()) null else currentState.dropoffCoordinate
                )
                
                // Check if we should transition to route preview
                if (newState.canProceedToRoutePreview) {
                    StateTransitionResult(
                        newState = BookingState.RoutePreview(
                            pickupLocation = newState.pickupLocation,
                            dropoffLocation = newState.dropoffLocation,
                            pickupCoordinate = newState.pickupCoordinate!!,
                            dropoffCoordinate = newState.dropoffCoordinate!!,
                            availableRoutes = emptyList(),
                            isLoadingRoutes = true,
                            scheduledDateTime = newState.scheduledDateTime,
                            deliveryScheduleType = newState.deliveryScheduleType
                        ),
                        sideEffects = listOf(BookingSideEffect.FetchRoutes)
                    )
                } else {
                    StateTransitionResult(newState)
                }
            }
            
            is BookingEvent.PickupLocationSelected -> {
                val newState = currentState.copy(
                    pickupLocation = event.placeDetails.name,
                    pickupCoordinate = LocationCoordinate(
                        latitude = event.placeDetails.latitude,
                        longitude = event.placeDetails.longitude
                    )
                )
                
                // Check if we should transition to route preview
                if (newState.canProceedToRoutePreview) {
                    StateTransitionResult(
                        newState = BookingState.RoutePreview(
                            pickupLocation = newState.pickupLocation,
                            dropoffLocation = newState.dropoffLocation,
                            pickupCoordinate = newState.pickupCoordinate!!,
                            dropoffCoordinate = newState.dropoffCoordinate!!,
                            availableRoutes = emptyList(),
                            isLoadingRoutes = true,
                            scheduledDateTime = newState.scheduledDateTime,
                            deliveryScheduleType = newState.deliveryScheduleType
                        ),
                        sideEffects = listOf(BookingSideEffect.FetchRoutes)
                    )
                } else {
                    StateTransitionResult(newState)
                }
            }
            
            is BookingEvent.DropoffLocationSelected -> {
                val newState = currentState.copy(
                    dropoffLocation = event.placeDetails.name,
                    dropoffCoordinate = LocationCoordinate(
                        latitude = event.placeDetails.latitude,
                        longitude = event.placeDetails.longitude
                    )
                )
                
                // Check if we should transition to route preview
                if (newState.canProceedToRoutePreview) {
                    StateTransitionResult(
                        newState = BookingState.RoutePreview(
                            pickupLocation = newState.pickupLocation,
                            dropoffLocation = newState.dropoffLocation,
                            pickupCoordinate = newState.pickupCoordinate!!,
                            dropoffCoordinate = newState.dropoffCoordinate!!,
                            availableRoutes = emptyList(),
                            isLoadingRoutes = true,
                            scheduledDateTime = newState.scheduledDateTime,
                            deliveryScheduleType = newState.deliveryScheduleType
                        ),
                        sideEffects = listOf(BookingSideEffect.FetchRoutes)
                    )
                } else {
                    StateTransitionResult(newState)
                }
            }
            
            is BookingEvent.LocationSelectedFromMap -> {
                val newState = if (event.isPickup) {
                    currentState.copy(
                        pickupLocation = event.address,
                        pickupCoordinate = event.coordinate
                    )
                } else {
                    currentState.copy(
                        dropoffLocation = event.address,
                        dropoffCoordinate = event.coordinate
                    )
                }
                
                // Check if we should transition to route preview
                if (newState.canProceedToRoutePreview) {
                    StateTransitionResult(
                        newState = BookingState.RoutePreview(
                            pickupLocation = newState.pickupLocation,
                            dropoffLocation = newState.dropoffLocation,
                            pickupCoordinate = newState.pickupCoordinate!!,
                            dropoffCoordinate = newState.dropoffCoordinate!!,
                            availableRoutes = emptyList(),
                            isLoadingRoutes = true,
                            scheduledDateTime = newState.scheduledDateTime,
                            deliveryScheduleType = newState.deliveryScheduleType
                        ),
                        sideEffects = listOf(BookingSideEffect.FetchRoutes, BookingSideEffect.ResetMapInteraction)
                    )
                } else {
                    StateTransitionResult(
                        newState = newState,
                        sideEffects = listOf(BookingSideEffect.ResetMapInteraction)
                    )
                }
            }
            
            is BookingEvent.ScheduleTypeChanged -> {
                StateTransitionResult(
                    newState = currentState.copy(
                        deliveryScheduleType = event.scheduleType,
                        scheduledDateTime = if (event.scheduleType == DeliveryScheduleType.NOW) null else currentState.scheduledDateTime
                    )
                )
            }
            
            is BookingEvent.ScheduledDateTimeChanged -> {
                StateTransitionResult(
                    newState = currentState.copy(
                        scheduledDateTime = event.dateTimeMillis,
                        deliveryScheduleType = DeliveryScheduleType.SCHEDULED
                    )
                )
            }
            
            is BookingEvent.ClearAllFields -> {
                StateTransitionResult(
                    newState = BookingState.Idle,
                    sideEffects = listOf(
                        BookingSideEffect.ClearLocationFields,
                        BookingSideEffect.ClearRouteData,
                        BookingSideEffect.ResetMapInteraction
                    )
                )
            }
            
            else -> StateTransitionResult(currentState) // No state change
        }
    }
    
    private fun handleRoutePreviewState(
        currentState: BookingState.RoutePreview,
        event: BookingEvent
    ): StateTransitionResult {
        return when (event) {
            is BookingEvent.RoutesLoaded -> {
                StateTransitionResult(
                    newState = currentState.copy(
                        availableRoutes = event.routes,
                        isLoadingRoutes = false,
                        routeError = null
                    )
                )
            }
            
            is BookingEvent.RoutesLoadFailed -> {
                StateTransitionResult(
                    newState = currentState.copy(
                        isLoadingRoutes = false,
                        routeError = event.error
                    )
                )
            }
            
            is BookingEvent.RouteSelected -> {
                StateTransitionResult(
                    newState = currentState.copy(selectedRouteIndex = event.routeIndex)
                )
            }
            
            is BookingEvent.FindDriverRequested -> {
                val selectedRoute = currentState.selectedRoute
                if (selectedRoute != null) {
                    StateTransitionResult(
                        newState = BookingState.FindingDriver(
                            pickupLocation = currentState.pickupLocation,
                            dropoffLocation = currentState.dropoffLocation,
                            selectedRoute = selectedRoute,
                            estimatedPrice = currentState.estimatedPrice,
                            scheduledDateTime = currentState.scheduledDateTime
                        ),
                        sideEffects = listOf(
                            BookingSideEffect.StartDriverSearch,
                            BookingSideEffect.ShowDriverSearchOverlay,
                            BookingSideEffect.CollapseBottomSheet
                        )
                    )
                } else {
                    StateTransitionResult(currentState) // No valid route selected
                }
            }
            
            is BookingEvent.BackPressed -> {
                StateTransitionResult(
                    newState = BookingState.LocationsEntering(
                        pickupLocation = currentState.pickupLocation,
                        dropoffLocation = currentState.dropoffLocation,
                        pickupCoordinate = currentState.pickupCoordinate,
                        dropoffCoordinate = currentState.dropoffCoordinate,
                        scheduledDateTime = currentState.scheduledDateTime,
                        deliveryScheduleType = currentState.deliveryScheduleType
                    ),
                    sideEffects = listOf(
                        BookingSideEffect.ClearRouteData,
                        BookingSideEffect.ExpandBottomSheet
                    )
                )
            }
            
            is BookingEvent.ClearAllFields -> {
                StateTransitionResult(
                    newState = BookingState.Idle,
                    sideEffects = listOf(
                        BookingSideEffect.ClearLocationFields,
                        BookingSideEffect.ClearRouteData,
                        BookingSideEffect.ResetMapInteraction,
                        BookingSideEffect.ExpandBottomSheet
                    )
                )
            }
            
            else -> StateTransitionResult(currentState) // No state change
        }
    }
    
    private fun handleFindingDriverState(
        currentState: BookingState.FindingDriver,
        event: BookingEvent
    ): StateTransitionResult {
        return when (event) {
            is BookingEvent.DriverFound -> {
                StateTransitionResult(
                    newState = BookingState.DriverFound(
                        pickupLocation = currentState.pickupLocation,
                        dropoffLocation = currentState.dropoffLocation,
                        selectedRoute = currentState.selectedRoute,
                        driver = event.driver,
                        totalPrice = event.totalPrice,
                        estimatedPickupTime = event.estimatedPickup,
                        scheduledDateTime = currentState.scheduledDateTime
                    ),
                    sideEffects = listOf(
                        BookingSideEffect.HideDriverSearchOverlay,
                        BookingSideEffect.ShowDriverFoundDialog
                    )
                )
            }
            
            is BookingEvent.CancelPressed -> {
                StateTransitionResult(
                    newState = BookingState.RoutePreview(
                        pickupLocation = currentState.pickupLocation,
                        dropoffLocation = currentState.dropoffLocation,
                        pickupCoordinate = currentState.selectedRoute.polylinePoints.firstOrNull() ?: LocationCoordinate(0.0, 0.0),
                        dropoffCoordinate = currentState.selectedRoute.polylinePoints.lastOrNull() ?: LocationCoordinate(0.0, 0.0),
                        availableRoutes = listOf(currentState.selectedRoute), // Keep the selected route
                        selectedRouteIndex = 0,
                        scheduledDateTime = currentState.scheduledDateTime
                    ),
                    sideEffects = listOf(
                        BookingSideEffect.CancelDriverSearch,
                        BookingSideEffect.HideDriverSearchOverlay,
                        BookingSideEffect.ExpandBottomSheet
                    )
                )
            }
            
            is BookingEvent.DriverSearchFailed -> {
                StateTransitionResult(
                    newState = BookingState.RoutePreview(
                        pickupLocation = currentState.pickupLocation,
                        dropoffLocation = currentState.dropoffLocation,
                        pickupCoordinate = currentState.selectedRoute.polylinePoints.firstOrNull() ?: LocationCoordinate(0.0, 0.0),
                        dropoffCoordinate = currentState.selectedRoute.polylinePoints.lastOrNull() ?: LocationCoordinate(0.0, 0.0),
                        availableRoutes = listOf(currentState.selectedRoute),
                        selectedRouteIndex = 0,
                        routeError = event.error,
                        scheduledDateTime = currentState.scheduledDateTime
                    ),
                    sideEffects = listOf(
                        BookingSideEffect.HideDriverSearchOverlay,
                        BookingSideEffect.ExpandBottomSheet
                    )
                )
            }
            
            else -> StateTransitionResult(currentState) // No state change during driver search
        }
    }
    
    private fun handleDriverFoundState(
        currentState: BookingState.DriverFound,
        event: BookingEvent
    ): StateTransitionResult {
        return when (event) {
            is BookingEvent.BookingConfirmed -> {
                StateTransitionResult(
                    newState = BookingState.BookingConfirmed(
                        pickupLocation = currentState.pickupLocation,
                        dropoffLocation = currentState.dropoffLocation,
                        driver = currentState.driver,
                        totalPrice = currentState.totalPrice,
                        bookingId = generateBookingId(),
                        scheduledDateTime = currentState.scheduledDateTime
                    ),
                    sideEffects = listOf(
                        BookingSideEffect.HideDriverFoundDialog,
                        BookingSideEffect.NavigateToTracking
                    )
                )
            }
            
            is BookingEvent.BookingCancelled -> {
                StateTransitionResult(
                    newState = BookingState.RoutePreview(
                        pickupLocation = currentState.pickupLocation,
                        dropoffLocation = currentState.dropoffLocation,
                        pickupCoordinate = currentState.selectedRoute.polylinePoints.firstOrNull() ?: LocationCoordinate(0.0, 0.0),
                        dropoffCoordinate = currentState.selectedRoute.polylinePoints.lastOrNull() ?: LocationCoordinate(0.0, 0.0),
                        availableRoutes = listOf(currentState.selectedRoute),
                        selectedRouteIndex = 0,
                        scheduledDateTime = currentState.scheduledDateTime
                    ),
                    sideEffects = listOf(
                        BookingSideEffect.HideDriverFoundDialog,
                        BookingSideEffect.ExpandBottomSheet
                    )
                )
            }
            
            else -> StateTransitionResult(currentState) // No state change
        }
    }
    
    private fun handleBookingConfirmedState(
        currentState: BookingState.BookingConfirmed,
        event: BookingEvent
    ): StateTransitionResult {
        // Booking confirmed is typically a terminal state
        // Only allow clearing to start over
        return when (event) {
            is BookingEvent.ClearAllFields -> {
                StateTransitionResult(
                    newState = BookingState.Idle,
                    sideEffects = listOf(
                        BookingSideEffect.ClearLocationFields,
                        BookingSideEffect.ClearRouteData,
                        BookingSideEffect.ResetMapInteraction,
                        BookingSideEffect.ExpandBottomSheet
                    )
                )
            }
            else -> StateTransitionResult(currentState) // No state changes allowed
        }
    }
    
    private fun generateBookingId(): String {
        return "BK${System.currentTimeMillis()}"
    }
}
