package io.peng.sparrowdelivery.data.services

import com.google.android.libraries.places.api.model.Place
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate

// Data class for autocomplete predictions
data class PlacePrediction(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String,
    val fullText: String,
    val placeTypes: List<Place.Type>,
    val distanceMeters: Int?
) {
    val icon: String
        get() = getIconForPlaceTypes(placeTypes)
    
    private fun getIconForPlaceTypes(types: List<Place.Type>): String {
        return when {
            types.contains(Place.Type.RESTAURANT) || types.contains(Place.Type.MEAL_TAKEAWAY) || 
            types.contains(Place.Type.FOOD) -> "restaurant"
            types.contains(Place.Type.HOSPITAL) || types.contains(Place.Type.PHARMACY) -> "local_hospital"
            types.contains(Place.Type.BANK) || types.contains(Place.Type.ATM) -> "account_balance"
            types.contains(Place.Type.GAS_STATION) -> "local_gas_station"
            types.contains(Place.Type.SHOPPING_MALL) || types.contains(Place.Type.STORE) -> "shopping_bag"
            types.contains(Place.Type.SCHOOL) || types.contains(Place.Type.UNIVERSITY) -> "school"
            types.contains(Place.Type.LODGING) -> "hotel"
            types.contains(Place.Type.TRANSIT_STATION) || types.contains(Place.Type.BUS_STATION) -> "directions_bus"
            types.contains(Place.Type.POINT_OF_INTEREST) -> "business"
            types.contains(Place.Type.ROUTE) || types.contains(Place.Type.STREET_ADDRESS) -> "location_on"
            else -> "place"
        }
    }
}

// Detailed place information after fetching place details
data class PlaceDetails(
    val placeId: String,
    val name: String,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val placeTypes: List<Place.Type>,
    val businessStatus: Place.BusinessStatus?
) {
    val displayName: String
        get() = formatDisplayName()
    
    private fun formatDisplayName(): String {
        address?.let { addr ->
            // Extract locality/area from address for context
            val addressComponents = addr.split(",").map { it.trim() }
            
            // Find a good context (neighborhood or city)
            val context = addressComponents.find { component ->
                component != name && 
                component.length > 2 && 
                !component.contains(name, ignoreCase = true)
            }
            
            return if (context != null) {
                "$name, $context"
            } else {
                name
            }
        } ?: return name
    }
}
