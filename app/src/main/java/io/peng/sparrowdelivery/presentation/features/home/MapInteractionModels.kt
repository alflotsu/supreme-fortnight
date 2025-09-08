package io.peng.sparrowdelivery.presentation.features.home

import com.google.android.gms.maps.model.LatLng

// Map interaction modes for selecting pickup/dropoff locations
enum class MapInteractionMode {
    NONE,
    SELECTING_PICKUP,
    SELECTING_DROPOFF
}

// Location coordinate data class with utilities
data class LocationCoordinate(
    val latitude: Double,
    val longitude: Double
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
    
    companion object {
        fun fromLatLng(latLng: LatLng): LocationCoordinate {
            return LocationCoordinate(latLng.latitude, latLng.longitude)
        }
    }
}

// Package types similar to SwiftTouches
enum class PackageType(
    val displayName: String,
    val icon: String,
    val color: String
) {
    MEDICINE("Medicine", "medical_services", "#f44336"),
    RAW_FOOD("Raw Food", "eco", "#4caf50"),
    COOKED_FOOD("Cooked Food", "restaurant", "#ff9800"),
    SENSITIVE("Sensitive", "warning", "#ffeb3b"),
    ELECTRONIC("Electronic", "electrical_services", "#2196f3"),
    EXTREME_CARE("Extreme Care", "favorite", "#9c27b0");
    
    companion object {
        fun fromDisplayName(name: String): PackageType? {
            return values().find { it.displayName == name }
        }
    }
}

// Delivery time options (renamed to avoid conflict with existing DeliveryTime)
enum class DeliveryScheduleType(val displayName: String, val icon: String) {
    NOW("Now", "flash_on"),
    SCHEDULED("Schedule", "schedule");
}

// Delivery stop data for multi-stop deliveries
data class DeliveryStop(
    val id: String = java.util.UUID.randomUUID().toString(),
    var address: String = "",
    var recipientName: String = "",
    var phoneNumber: String = "",
    var instructions: String = "",
    var coordinate: LocationCoordinate? = null
)

// Enhanced delivery form state
data class EnhancedDeliveryFormState(
    // Basic locations
    val pickupLocation: String = "",
    val dropoffLocation: String = "",
    val pickupCoordinate: LocationCoordinate? = null,
    val dropoffCoordinate: LocationCoordinate? = null,
    
    // Package information
    val packageType: PackageType? = null,
    val packageWeight: String = "",
    val packageDimensions: String = "",
    val isFragile: Boolean = false,
    val requiresSignature: Boolean = false,
    
    // Delivery timing
    val deliveryScheduleType: DeliveryScheduleType = DeliveryScheduleType.NOW,
    val scheduledDateTime: Long? = null, // Timestamp for scheduled delivery
    
    // Multi-stop delivery
    val deliveryStops: List<DeliveryStop> = emptyList(),
    val specialInstructions: String = "",
    
    // Route information
    val showingRoutePreview: Boolean = false,
    val routePreviewError: String? = null,
    
    // Loading states
    val isLoadingRoute: Boolean = false,
    val isLoadingPricing: Boolean = false
) {
    // Computed properties
    val canShowRoutePreview: Boolean
        get() = pickupCoordinate != null && dropoffCoordinate != null
    
    val canProceed: Boolean
        get() = pickupLocation.isNotBlank() && dropoffLocation.isNotBlank()
    
    val hasValidStops: Boolean
        get() = deliveryStops.any { it.address.isNotBlank() }
    
    val estimatedPrice: Double
        get() = calculateEstimatedPrice()
    
    private fun calculateEstimatedPrice(): Double {
        var basePrice = 15.0 // Base delivery fee ₵15
        
        // Add cost per additional stop
        val additionalStops = maxOf(0, deliveryStops.filter { it.address.isNotBlank() }.size)
        basePrice += additionalStops * 8.0 // ₵8 per additional stop
        
        // Add premium for fragile items
        if (isFragile) {
            basePrice += 5.0 // ₵5 fragile handling fee
        }
        
        // Add premium for signature requirement
        if (requiresSignature) {
            basePrice += 3.0 // ₵3 signature service fee
        }
        
        // Add weight-based pricing if specified
        val weightString = packageWeight.replace(" kg", "").replace("kg", "")
        weightString.toDoubleOrNull()?.let { weight ->
            if (weight > 5) {
                basePrice += (weight - 5) * 2.0 // ₵2 per kg over 5kg
            }
        }
        
        return basePrice
    }
}

// Quick action types for shortcuts
enum class QuickActionType(val displayName: String, val icon: String) {
    RECENT("Recent", "history"),
    FAVORITES("Favorites", "star"),
//    HOME("Home", "home"),
    TRACK("Track", "track_changes");
}

// Map marker types for visual distinction
enum class MapMarkerType {
    PICKUP,
    DROPOFF,
    INTERMEDIATE_STOP,
    CURRENT_LOCATION,
    SELECTED_LOCATION
}

// POI (Point of Interest) data from Google Places
data class PointOfInterest(
    val placeId: String,
    val name: String,
    val address: String,
    val coordinate: LocationCoordinate,
    val types: List<String> = emptyList()
) {
    val icon: String
        get() = getIconForPlaceTypes(types)
    
    private fun getIconForPlaceTypes(types: List<String>): String {
        return when {
            types.any { it.contains("restaurant") || it.contains("food") } -> "restaurant"
            types.any { it.contains("hospital") || it.contains("pharmacy") } -> "local_hospital"
            types.any { it.contains("bank") || it.contains("atm") } -> "account_balance"
            types.any { it.contains("gas_station") } -> "local_gas_station"
            types.any { it.contains("shopping") || it.contains("store") } -> "shopping_bag"
            types.any { it.contains("school") || it.contains("university") } -> "school"
            types.any { it.contains("lodging") } -> "hotel"
            types.any { it.contains("transit") || it.contains("bus") } -> "directions_bus"
            types.any { it.contains("point_of_interest") } -> "business"
            else -> "place"
        }
    }
}
