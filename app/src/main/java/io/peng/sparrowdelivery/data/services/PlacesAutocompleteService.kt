package io.peng.sparrowdelivery.data.services

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import io.peng.sparrowdelivery.core.security.ApiKeyManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PlacesAutocompleteService(private val context: Context) {
    private val placesClient: PlacesClient by lazy {
        if (!Places.isInitialized()) {
            Places.initialize(context, getApiKey())
        }
        Places.createClient(context)
    }
    
    private val _predictions = MutableStateFlow<List<PlacePrediction>>(emptyList())
    val predictions: StateFlow<List<PlacePrediction>> = _predictions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Ghana bias coordinates (Accra center - matching SwiftTouches)
    private val ghanaLocation = LatLng(5.61, -0.14)
    private val biasRadius = 50000 // 50km radius
    
    private fun getApiKey(): String {
        return try {
            // First, validate that ApiKeyManager has a valid key
            val validatedKey = ApiKeyManager.getGoogleMapsApiKey()
            
            // Then get it from manifest (required by Places SDK)
            val appInfo: ApplicationInfo = context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val bundle: Bundle = appInfo.metaData
            val manifestKey = bundle.getString("com.google.android.geo.API_KEY") 
                ?: throw IllegalStateException("Google Maps API key not found in manifest")
            
            // Ensure both keys match for security
            if (validatedKey != manifestKey) {
                throw SecurityException("API key mismatch between BuildConfig and manifest")
            }
            
            manifestKey
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to get Google Maps API key from manifest. Make sure GOOGLE_MAPS_API_KEY is set in local.properties",
                e
            )
        }
    }
    
    suspend fun searchPlaces(query: String): List<PlacePrediction> {
        if (query.isBlank()) {
            _predictions.value = emptyList()
            return emptyList()
        }
        
        _isLoading.value = true
        _error.value = null
        
        return try {
            val predictions = performAutocompleteSearch(query)
            _predictions.value = predictions
            _isLoading.value = false
            predictions
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error occurred"
            _predictions.value = emptyList()
            _isLoading.value = false
            emptyList()
        }
    }
    
    private suspend fun performAutocompleteSearch(query: String): List<PlacePrediction> = suspendCoroutine { continuation ->
        // Create autocomplete request with Ghana bias
        val request = try {
            FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(
                    RectangularBounds.newInstance(
                        LatLng(4.5, -3.5), // Southwest corner of Ghana
                        LatLng(11.5, 1.5)  // Northeast corner of Ghana
                    )
                )
                .setCountries("GH") // Bias towards Ghana
                .setQuery(query)
                // Remove type filter to get all results (addresses and establishments)
                // Google Places API doesn't allow mixing ESTABLISHMENT with other types
                .build()
        } catch (e: Exception) {
            // Fallback request without location bias if there are issues
            FindAutocompletePredictionsRequest.builder()
                .setCountries("GH")
                .setQuery(query)
                .build()
        }
        
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions.map { prediction ->
                    PlacePrediction(
                        placeId = prediction.placeId,
                        primaryText = prediction.getPrimaryText(null).toString(),
                        secondaryText = prediction.getSecondaryText(null).toString(),
                        fullText = prediction.getFullText(null).toString(),
                        placeTypes = prediction.placeTypes ?: emptyList(),
                        distanceMeters = prediction.distanceMeters
                    )
                }
                continuation.resume(predictions)
            }
            .addOnFailureListener { exception ->
                continuation.resume(emptyList())
                _error.value = exception.message ?: "Failed to fetch predictions"
            }
    }
    
    suspend fun fetchPlaceDetails(placeId: String): PlaceDetails? = suspendCoroutine { continuation ->
        // Define the fields to return
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.TYPES,
            Place.Field.BUSINESS_STATUS
        )
        
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val placeDetails = PlaceDetails(
                    placeId = place.id ?: placeId,
                    name = place.name ?: "Unknown Place",
                    address = place.address,
                    latitude = place.latLng?.latitude ?: 0.0,
                    longitude = place.latLng?.longitude ?: 0.0,
                    placeTypes = place.types ?: emptyList(),
                    businessStatus = place.businessStatus
                )
                continuation.resume(placeDetails)
            }
            .addOnFailureListener { exception ->
                _error.value = exception.message ?: "Failed to fetch place details"
                continuation.resume(null)
            }
    }
    
    fun clearPredictions() {
        _predictions.value = emptyList()
        _error.value = null
    }
}
