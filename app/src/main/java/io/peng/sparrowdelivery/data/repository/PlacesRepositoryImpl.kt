package io.peng.sparrowdelivery.data.repository

import android.content.Context
import io.peng.sparrowdelivery.core.error.ApiResult
import io.peng.sparrowdelivery.core.error.AppError
import io.peng.sparrowdelivery.data.services.EnhancedPlacesAutocompleteService
import io.peng.sparrowdelivery.data.services.PlaceDetails
import io.peng.sparrowdelivery.data.services.PlacePrediction
import io.peng.sparrowdelivery.domain.repository.PlacesRepository
import io.peng.sparrowdelivery.domain.repository.PopularPlace
import io.peng.sparrowdelivery.domain.repository.SearchHistoryItem
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import io.peng.sparrowdelivery.presentation.features.home.PointOfInterest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

/**
 * Implementation of PlacesRepository
 * 
 * Provides places search, autocomplete, and details with caching and
 * Ghana-specific optimizations.
 */
class PlacesRepositoryImpl(
    private val context: Context,
    private val placesService: EnhancedPlacesAutocompleteService
) : PlacesRepository {
    
    // Simple in-memory caches
    private val searchCache = mutableMapOf<String, CachedSearch>()
    private val placeDetailsCache = mutableMapOf<String, CachedPlaceDetails>()
    private val cacheValidityMs = 10 * 60 * 1000L // 10 minutes
    
    // Search history
    private val searchHistory = mutableListOf<SearchHistoryItem>()
    
    override suspend fun searchPlaces(
        query: String,
        biasLocation: LocationCoordinate?
    ): Flow<ApiResult<List<PlacePrediction>>> = flow {
        emit(ApiResult.Loading)
        
        if (query.isBlank()) {
            emit(ApiResult.Success(emptyList()))
            return@flow
        }
        
        // Check cache
        val cached = searchCache[query]
        if (cached != null && isCacheValid(cached.timestamp)) {
            emit(ApiResult.Success(cached.results))
            return@flow
        }
        
        try {
            val results = placesService.searchPlaces(query)
            // Cache results
            searchCache[query] = CachedSearch(results, System.currentTimeMillis())
            emit(ApiResult.Success(results))
        } catch (e: Exception) {
            emit(ApiResult.Error(AppError.Unknown(e)))
        }
    }
    
    override suspend fun getPlaceDetails(placeId: String): Flow<ApiResult<PlaceDetails>> = flow {
        emit(ApiResult.Loading)
        
        // Check cache
        val cached = placeDetailsCache[placeId]
        if (cached != null && isCacheValid(cached.timestamp)) {
            emit(ApiResult.Success(cached.details))
            return@flow
        }
        
        try {
            val details = placesService.fetchPlaceDetails(placeId)
            if (details != null) {
                placeDetailsCache[placeId] = CachedPlaceDetails(details, System.currentTimeMillis())
                emit(ApiResult.Success(details))
            } else {
                emit(ApiResult.Error(AppError.LocationNotFound))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(AppError.Unknown(e)))
        }
    }
    
    override suspend fun getNearbyPlaces(
        location: LocationCoordinate,
        radius: Int,
        types: List<String>
    ): Flow<ApiResult<List<PointOfInterest>>> = flow {
        // Not implemented: would require Places Nearby Search API or custom data source
        emit(ApiResult.Success(emptyList()))
    }
    
    override suspend fun getPopularPlaces(): Flow<List<PopularPlace>> = flow {
        // Ghana popular places (could be loaded from local asset or remote config)
        val popular = listOf(
            PopularPlace(
                id = UUID.randomUUID().toString(),
                name = "Accra Mall",
                address = "Tetteh Quarshie Interchange, Accra",
                coordinate = LocationCoordinate(5.6295, -0.1718),
                category = "Shopping Mall",
                icon = "shopping_bag"
            ),
            PopularPlace(
                id = UUID.randomUUID().toString(),
                name = "University of Ghana, Legon",
                address = "Legon, Accra",
                coordinate = LocationCoordinate(5.6506, -0.1965),
                category = "University",
                icon = "school"
            ),
            PopularPlace(
                id = UUID.randomUUID().toString(),
                name = "Kotoka International Airport",
                address = "Airport, Accra",
                coordinate = LocationCoordinate(5.6061, -0.1697),
                category = "Airport",
                icon = "flight"
            )
        )
        emit(popular)
    }
    
    override suspend fun saveToSearchHistory(place: PlacePrediction, coordinate: LocationCoordinate?) {
        val item = SearchHistoryItem(
            id = UUID.randomUUID().toString(),
            query = place.primaryText,
            placeName = place.primaryText,
            address = place.fullText,
            coordinate = coordinate ?: LocationCoordinate(0.0, 0.0),
            searchedAt = System.currentTimeMillis(),
            usageCount = 1
        )
        searchHistory.add(0, item)
        if (searchHistory.size > 50) {
            searchHistory.removeLastOrNull()
        }
    }
    
    override suspend fun getSearchHistory(limit: Int): Flow<List<SearchHistoryItem>> = flow {
        emit(searchHistory.take(limit))
    }
    
    override suspend fun clearSearchHistory() {
        searchHistory.clear()
    }
    
    override suspend fun clearCache() {
        searchCache.clear()
        placeDetailsCache.clear()
    }
    
    override suspend fun isPlacesServiceAvailable(): Boolean {
        return try {
            placesService.getConnectionStatus() != "No Connection"
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isCacheValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < cacheValidityMs
    }
}

private data class CachedSearch(
    val results: List<PlacePrediction>,
    val timestamp: Long
)

private data class CachedPlaceDetails(
    val details: PlaceDetails,
    val timestamp: Long
)

