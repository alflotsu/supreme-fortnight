package io.peng.sparrowdelivery.domain.repository

import io.peng.sparrowdelivery.core.error.ApiResult
import io.peng.sparrowdelivery.data.services.PlaceDetails
import io.peng.sparrowdelivery.data.services.PlacePrediction
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import io.peng.sparrowdelivery.presentation.features.home.PointOfInterest
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for places operations
 * 
 * Provides a clean abstraction for places search, autocomplete, and place details
 * with intelligent caching and Ghana-specific optimizations.
 */
interface PlacesRepository {
    
    /**
     * Search for places with autocomplete suggestions
     * 
     * Provides real-time search suggestions as user types, with intelligent
     * caching to minimize API calls and improve performance.
     * 
     * @param query Search query string
     * @param biasLocation Optional location to bias results towards (defaults to Ghana)
     * @return Flow of API results containing place predictions
     */
    suspend fun searchPlaces(
        query: String,
        biasLocation: LocationCoordinate? = null
    ): Flow<ApiResult<List<PlacePrediction>>>
    
    /**
     * Get detailed information about a specific place
     * 
     * @param placeId Google Places API place ID
     * @return Flow of API result containing detailed place information
     */
    suspend fun getPlaceDetails(placeId: String): Flow<ApiResult<PlaceDetails>>
    
    /**
     * Get nearby points of interest
     * 
     * @param location Center location for nearby search
     * @param radius Search radius in meters (default 5000m)
     * @param types Optional place types to filter by
     * @return Flow of API results containing nearby POIs
     */
    suspend fun getNearbyPlaces(
        location: LocationCoordinate,
        radius: Int = 5000,
        types: List<String> = emptyList()
    ): Flow<ApiResult<List<PointOfInterest>>>
    
    /**
     * Get popular places in Ghana (cached data)
     * 
     * Returns frequently searched locations like major landmarks,
     * universities, malls, etc. for quick access.
     * 
     * @return Flow of popular places list
     */
    suspend fun getPopularPlaces(): Flow<List<PopularPlace>>
    
    /**
     * Save a place to search history for better suggestions
     * 
     * @param place Place prediction that was selected
     * @param coordinate Final coordinate if different from place
     */
    suspend fun saveToSearchHistory(
        place: PlacePrediction,
        coordinate: LocationCoordinate? = null
    )
    
    /**
     * Get search history for better autocomplete suggestions
     * 
     * @param limit Maximum number of history items to return
     * @return Flow of recent search history
     */
    suspend fun getSearchHistory(limit: Int = 20): Flow<List<SearchHistoryItem>>
    
    /**
     * Clear search history
     */
    suspend fun clearSearchHistory()
    
    /**
     * Clear cached places data
     */
    suspend fun clearCache()
    
    /**
     * Check if places service is available
     * 
     * @return true if Places API is accessible, false otherwise
     */
    suspend fun isPlacesServiceAvailable(): Boolean
}

/**
 * Data class for popular places in Ghana
 */
data class PopularPlace(
    val id: String,
    val name: String,
    val address: String,
    val coordinate: LocationCoordinate,
    val category: String,
    val icon: String
)

/**
 * Data class for search history items
 */
data class SearchHistoryItem(
    val id: String,
    val query: String,
    val placeName: String,
    val address: String,
    val coordinate: LocationCoordinate,
    val searchedAt: Long,
    val usageCount: Int
)
