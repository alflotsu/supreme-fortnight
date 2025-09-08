package io.peng.sparrowdelivery.presentation.features.external

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.peng.sparrowdelivery.integration.LocationData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

data class ExternalBookingReviewUiState(
    val isLoading: Boolean = false,
    val estimatedPrice: Double? = null,
    val estimatedDistance: Double? = null,
    val error: String? = null
)

class ExternalBookingReviewViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExternalBookingReviewUiState())
    val uiState: StateFlow<ExternalBookingReviewUiState> = _uiState.asStateFlow()
    
    fun calculateEstimate(pickup: LocationData, dropoff: LocationData) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )
        
        viewModelScope.launch {
            try {
                // Simulate API call delay
                delay(1500)
                
                // Calculate distance using Haversine formula
                val distance = calculateDistance(pickup, dropoff)
                val price = calculatePrice(distance)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    estimatedPrice = price,
                    estimatedDistance = distance,
                    error = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to calculate estimate: ${e.message}"
                )
            }
        }
    }
    
    private fun calculateDistance(pickup: LocationData, dropoff: LocationData): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(dropoff.latitude - pickup.latitude)
        val dLon = Math.toRadians(dropoff.longitude - pickup.longitude)
        val a = sin(dLat/2) * sin(dLat/2) +
                cos(Math.toRadians(pickup.latitude)) * cos(Math.toRadians(dropoff.latitude)) *
                sin(dLon/2) * sin(dLon/2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        return earthRadius * c
    }
    
    private fun calculatePrice(distance: Double): Double {
        // Base price + distance-based pricing
        val basePrice = 3.0
        val pricePerKm = 2.5
        return basePrice + (distance * pricePerKm)
    }
}
