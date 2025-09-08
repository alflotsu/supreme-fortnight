package io.peng.sparrowdelivery.domain.repository

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import io.peng.sparrowdelivery.core.error.ApiResult
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for location operations
 * 
 * Provides a clean abstraction for location services including:
 * - Current location access
 * - Geocoding and reverse geocoding
 * - Location permission handling
 * - Location history management
 */
interface LocationRepository {
    
    /**
     * Get the current device location
     * 
     * @return Flow of API result containing current location coordinate
     */
    suspend fun getCurrentLocation(): Flow<ApiResult<LocationCoordinate>>
    
    /**
     * Check if location permissions are granted
     * 
     * @return true if location permissions are available, false otherwise
     */
    fun hasLocationPermission(): Boolean
    
    /**
     * Reverse geocode coordinates to human-readable address
     * 
     * @param coordinate Location coordinate to reverse geocode
     * @return Flow of API result containing formatted address string
     */
    suspend fun reverseGeocode(coordinate: LocationCoordinate): Flow<ApiResult<String>>
    
    /**
     * Get detailed address information from coordinates
     * 
     * @param coordinate Location coordinate to reverse geocode
     * @return Flow of API result containing detailed address list
     */
    suspend fun getAddressFromCoordinate(coordinate: LocationCoordinate): Flow<ApiResult<List<Address>>>
    
    /**
     * Convert coordinates to LatLng for Maps integration
     * 
     * @param coordinate Location coordinate to convert
     * @return LatLng object for Google Maps
     */
    fun coordinateToLatLng(coordinate: LocationCoordinate): LatLng
    
    /**
     * Convert LatLng to LocationCoordinate
     * 
     * @param latLng LatLng from Google Maps
     * @return LocationCoordinate for domain use
     */
    fun latLngToCoordinate(latLng: LatLng): LocationCoordinate
    
    /**
     * Save a location to favorites for quick access
     * 
     * @param coordinate Location to save
     * @param name Display name for the location
     * @param address Formatted address string
     */
    suspend fun saveFavoriteLocation(
        coordinate: LocationCoordinate,
        name: String,
        address: String
    )
    
    /**
     * Get saved favorite locations
     * 
     * @return Flow of favorite locations with names and addresses
     */
    suspend fun getFavoriteLocations(): Flow<List<FavoriteLocation>>
    
    /**
     * Remove a location from favorites
     * 
     * @param locationId ID of the favorite location to remove
     */
    suspend fun removeFavoriteLocation(locationId: String)
    
    /**
     * Get recent locations used in the app
     * 
     * @param limit Maximum number of recent locations to return
     * @return Flow of recent locations
     */
    suspend fun getRecentLocations(limit: Int = 10): Flow<List<RecentLocation>>
}

/**
 * Data class for favorite locations
 */
data class FavoriteLocation(
    val id: String,
    val name: String,
    val address: String,
    val coordinate: LocationCoordinate,
    val createdAt: Long
)

/**
 * Data class for recent locations
 */
data class RecentLocation(
    val id: String,
    val address: String,
    val coordinate: LocationCoordinate,
    val lastUsed: Long,
    val usageCount: Int
)
