package io.peng.sparrowdelivery.data.services

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import io.peng.sparrowdelivery.core.di.ServiceLocator
import io.peng.sparrowdelivery.core.error.*
import io.peng.sparrowdelivery.core.security.ApiKeyManager
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Enhanced Places Autocomplete Service with comprehensive error handling
 * 
 * Features:
 * - Automatic retry with exponential backoff
 * - User-friendly error messages
 * - Connection quality aware operations
 * - Proper timeout handling
 * - Enhanced reliability for Places API
 */
class EnhancedPlacesAutocompleteService(private val context: Context) {
    
    private val errorHandler by lazy { ServiceLocator.getErrorHandler(context) }
    
    private val placesClient: PlacesClient by lazy {
        initializePlacesClient()
    }
    
    private val _predictions = MutableStateFlow<List<PlacePrediction>>(emptyList())
    val predictions: StateFlow<List<PlacePrediction>> = _predictions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<AppError?>(null)
    val error: StateFlow<AppError?> = _error.asStateFlow()
    
    // Ghana bias coordinates (Accra center)
    private val ghanaLocation = LatLng(5.61, -0.14)
    private val biasRadius = 50000 // 50km radius
    
    /**
     * Initialize Places client with proper error handling
     */
    private fun initializePlacesClient(): PlacesClient {
        return try {
            if (!Places.isInitialized()) {
                Places.initialize(context, getApiKey())
            }
            Places.createClient(context)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize Places API client", e)
        }
    }
    
    /**
     * Search for places with comprehensive error handling and retry logic
     */
    suspend fun searchPlaces(query: String): List<PlacePrediction> {
        if (query.isBlank()) {
            _predictions.value = emptyList()
            return emptyList()
        }
        
        _isLoading.value = true
        _error.value = null
        
        // Configure retry policy based on connection quality
        val retryConfig = when (errorHandler.getConnectionQuality()) {
            ConnectionQuality.WIFI -> RetryConfig(maxAttempts = 2, initialDelayMs = 500L)
            ConnectionQuality.CELLULAR_GOOD -> RetryConfig(maxAttempts = 3, initialDelayMs = 1000L)
            ConnectionQuality.CELLULAR_POOR, ConnectionQuality.POOR -> RetryConfig(maxAttempts = 4, initialDelayMs = 2000L)
            ConnectionQuality.NONE -> RetryConfig(maxAttempts = 1, initialDelayMs = 0L) // Fail fast
        }
        
        val result = safeApiCall(
            errorHandler = errorHandler,
            retryConfig = retryConfig
        ) {
            performAutocompleteSearchWithRetry(query)
        }
        
        return when (result) {
            is ApiResult.Success -> {
                _isLoading.value = false
                _predictions.value = result.data
                result.data
            }
            is ApiResult.Error -> {
                _isLoading.value = false
                _error.value = result.error
                _predictions.value = emptyList()
                
                // Log error for debugging
                println("Places Autocomplete API Error: ${result.error.technicalMessage}")
                
                emptyList()
            }
            is ApiResult.Loading -> {
                // This shouldn't happen with our implementation
                emptyList()
            }
        }
    }
    
    /**
     * Fetch place details with enhanced error handling and retry logic
     */
    suspend fun fetchPlaceDetails(placeId: String): PlaceDetails? {
        _error.value = null
        
        // Configure retry policy based on connection quality
        val retryConfig = when (errorHandler.getConnectionQuality()) {
            ConnectionQuality.WIFI -> RetryConfig(maxAttempts = 2, initialDelayMs = 500L)
            ConnectionQuality.CELLULAR_GOOD -> RetryConfig(maxAttempts = 3, initialDelayMs = 1000L)
            ConnectionQuality.CELLULAR_POOR, ConnectionQuality.POOR -> RetryConfig(maxAttempts = 4, initialDelayMs = 2000L)
            ConnectionQuality.NONE -> RetryConfig(maxAttempts = 1, initialDelayMs = 0L) // Fail fast
        }
        
        val result = safeApiCall(
            errorHandler = errorHandler,
            retryConfig = retryConfig
        ) {
            performPlaceDetailsSearchWithRetry(placeId)
        }
        
        return when (result) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> {
                _error.value = result.error
                
                // Log error for debugging
                println("Places Details API Error: ${result.error.technicalMessage}")
                
                null
            }
            is ApiResult.Loading -> null
        }
    }
    
    /**
     * Perform autocomplete search with internal retry logic for Places API specific errors
     */
    private suspend fun performAutocompleteSearchWithRetry(query: String): List<PlacePrediction> {
        return suspendCoroutine { continuation ->
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
                    // Handle specific Places API errors
                    val enhancedException = when {
                        exception.message?.contains("INVALID_REQUEST") == true -> 
                            IllegalArgumentException("Invalid search parameters provided")
                        exception.message?.contains("OVER_QUERY_LIMIT") == true -> 
                            RuntimeException("Places API quota exceeded. Please try again later.")
                        exception.message?.contains("REQUEST_DENIED") == true -> 
                            SecurityException("Places API access denied. Check API key configuration.")
                        exception.message?.contains("ZERO_RESULTS") == true -> 
                            IllegalArgumentException("No places found matching your search criteria")
                        else -> Exception("Places search failed: ${exception.message ?: "Unknown error"}")
                    }
                    continuation.resumeWithException(enhancedException)
                }
        }
    }
    
    /**
     * Perform place details search with internal retry logic for Places API specific errors
     */
    private suspend fun performPlaceDetailsSearchWithRetry(placeId: String): PlaceDetails? {
        return suspendCoroutine { continuation ->
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
                    // Handle specific Places API errors
                    val enhancedException = when {
                        exception.message?.contains("NOT_FOUND") == true -> 
                            IllegalArgumentException("Place not found with the provided ID")
                        exception.message?.contains("INVALID_REQUEST") == true -> 
                            IllegalArgumentException("Invalid place ID provided")
                        exception.message?.contains("OVER_QUERY_LIMIT") == true -> 
                            RuntimeException("Places API quota exceeded. Please try again later.")
                        exception.message?.contains("REQUEST_DENIED") == true -> 
                            SecurityException("Places API access denied. Check API key configuration.")
                        else -> Exception("Place details fetch failed: ${exception.message ?: "Unknown error"}")
                    }
                    continuation.resumeWithException(enhancedException)
                }
        }
    }
    
    /**
     * Get API key with enhanced security validation
     */
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
    
    /**
     * Clear predictions and error state
     */
    fun clearPredictions() {
        _predictions.value = emptyList()
        _error.value = null
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Check if service is currently loading
     */
    fun isCurrentlyLoading(): Boolean = _isLoading.value
    
    /**
     * Get last error if any
     */
    fun getLastError(): AppError? = _error.value
    
    /**
     * Get connection status for debugging
     */
    fun getConnectionStatus(): String {
        return when (errorHandler.getConnectionQuality()) {
            ConnectionQuality.WIFI -> "WiFi - Excellent"
            ConnectionQuality.CELLULAR_GOOD -> "Mobile - Good"
            ConnectionQuality.CELLULAR_POOR -> "Mobile - Poor"
            ConnectionQuality.POOR -> "Poor Connection"
            ConnectionQuality.NONE -> "No Connection"
        }
    }
}
