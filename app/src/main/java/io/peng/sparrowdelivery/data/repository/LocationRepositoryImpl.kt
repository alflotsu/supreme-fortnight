package io.peng.sparrowdelivery.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import io.peng.sparrowdelivery.core.error.ApiResult
import io.peng.sparrowdelivery.core.error.AppError
import io.peng.sparrowdelivery.domain.repository.FavoriteLocation
import io.peng.sparrowdelivery.domain.repository.LocationRepository
import io.peng.sparrowdelivery.domain.repository.RecentLocation
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

/**
 * Implementation of LocationRepository
 * 
 * Handles location services including:
 * - Current location fetching with proper permissions
 * - Geocoding and reverse geocoding
 * - Location favorites and history management
 * - Coordinate conversions for Maps integration
 * 
 * Features:
 * - Permission-aware location access
 * - Enhanced error handling for location services
 * - In-memory storage for favorites and recent locations
 * - Ghana-optimized address formatting
 */
class LocationRepositoryImpl(
    private val context: Context
) : LocationRepository {
    
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    private val geocoder: Geocoder by lazy {
        Geocoder(context, Locale.getDefault())
    }
    
    // In-memory storage (in production, use Room database)
    private val favoriteLocations = mutableListOf<FavoriteLocation>()
    private val recentLocations = mutableListOf<RecentLocation>()
    
    override suspend fun getCurrentLocation(): Flow<ApiResult<LocationCoordinate>> = flow {
        emit(ApiResult.Loading)
        
        // Check permissions first
        if (!hasLocationPermission()) {
            emit(ApiResult.Error(AppError.NoInternet)) // Reuse existing error for now
            return@flow
        }
        
        try {
            val location = getCurrentLocationInternal()
            if (location != null) {
                val coordinate = LocationCoordinate(location.latitude, location.longitude)
                emit(ApiResult.Success(coordinate))
            } else {
                emit(ApiResult.Error(AppError.LocationNotFound))
            }
        } catch (e: SecurityException) {
            emit(ApiResult.Error(AppError.NoInternet)) // Permission error
        } catch (e: Exception) {
            emit(ApiResult.Error(AppError.Unknown(e)))
        }
    }
    
    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun reverseGeocode(coordinate: LocationCoordinate): Flow<ApiResult<String>> = flow {
        emit(ApiResult.Loading)
        
        try {
            val addresses = getAddressesFromCoordinate(coordinate)
            if (addresses.isNotEmpty()) {
                val formattedAddress = formatAddressForGhana(addresses[0], coordinate)
                emit(ApiResult.Success(formattedAddress))
            } else {
                val fallbackAddress = "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
                emit(ApiResult.Success(fallbackAddress))
            }
        } catch (e: Exception) {
            val fallbackAddress = "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
            emit(ApiResult.Success(fallbackAddress))
        }
    }
    
    override suspend fun getAddressFromCoordinate(coordinate: LocationCoordinate): Flow<ApiResult<List<Address>>> = flow {
        emit(ApiResult.Loading)
        
        try {
            val addresses = getAddressesFromCoordinate(coordinate)
            emit(ApiResult.Success(addresses))
        } catch (e: Exception) {
            emit(ApiResult.Error(AppError.Unknown(e)))
        }
    }
    
    override fun coordinateToLatLng(coordinate: LocationCoordinate): LatLng {
        return LatLng(coordinate.latitude, coordinate.longitude)
    }
    
    override fun latLngToCoordinate(latLng: LatLng): LocationCoordinate {
        return LocationCoordinate(latLng.latitude, latLng.longitude)
    }
    
    override suspend fun saveFavoriteLocation(
        coordinate: LocationCoordinate,
        name: String,
        address: String
    ) {
        val newFavorite = FavoriteLocation(
            id = UUID.randomUUID().toString(),
            name = name,
            address = address,
            coordinate = coordinate,
            createdAt = System.currentTimeMillis()
        )
        
        // Remove existing favorite with same name (update)
        favoriteLocations.removeAll { it.name == name }
        favoriteLocations.add(newFavorite)
        
        // Limit to 20 favorites
        if (favoriteLocations.size > 20) {
            favoriteLocations.removeAt(0)
        }
    }
    
    override suspend fun getFavoriteLocations(): Flow<List<FavoriteLocation>> = flow {
        // Sort by creation time, most recent first
        val sortedFavorites = favoriteLocations.sortedByDescending { it.createdAt }
        emit(sortedFavorites)
    }
    
    override suspend fun removeFavoriteLocation(locationId: String) {
        favoriteLocations.removeAll { it.id == locationId }
    }
    
    override suspend fun getRecentLocations(limit: Int): Flow<List<RecentLocation>> = flow {
        val sortedRecent = recentLocations
            .sortedByDescending { it.lastUsed }
            .take(limit)
        emit(sortedRecent)
    }
    
    // Internal helper methods
    
    private suspend fun getCurrentLocationInternal(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener { exception ->
                continuation.resume(null)
            }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }
    
    private suspend fun getAddressesFromCoordinate(coordinate: LocationCoordinate): List<Address> {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                // Use modern API with callback
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1) { addresses ->
                        continuation.resume(addresses ?: emptyList())
                    }
                }
            } else {
                // Use deprecated synchronous API
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Format address for Ghana with local context and landmarks
     */
    private fun formatAddressForGhana(address: Address, coordinate: LocationCoordinate): String {
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
    
    /**
     * Add a location to recent locations history
     * Called internally when locations are used
     */
    fun addToRecentLocations(address: String, coordinate: LocationCoordinate) {
        // Check if location already exists
        val existing = recentLocations.find { 
            kotlin.math.abs(it.coordinate.latitude - coordinate.latitude) < 0.001 &&
            kotlin.math.abs(it.coordinate.longitude - coordinate.longitude) < 0.001
        }
        
        if (existing != null) {
            // Update existing entry
            val updated = existing.copy(
                lastUsed = System.currentTimeMillis(),
                usageCount = existing.usageCount + 1
            )
            recentLocations.remove(existing)
            recentLocations.add(updated)
        } else {
            // Add new entry
            val newRecent = RecentLocation(
                id = UUID.randomUUID().toString(),
                address = address,
                coordinate = coordinate,
                lastUsed = System.currentTimeMillis(),
                usageCount = 1
            )
            recentLocations.add(newRecent)
        }
        
        // Limit to 50 recent locations
        if (recentLocations.size > 50) {
            val oldest = recentLocations.minByOrNull { it.lastUsed }
            oldest?.let { recentLocations.remove(it) }
        }
    }
}
