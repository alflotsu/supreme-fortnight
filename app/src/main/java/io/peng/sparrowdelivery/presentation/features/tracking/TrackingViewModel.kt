package io.peng.sparrowdelivery.presentation.features.tracking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class DeliveryStatus(val displayName: String) {
    DRIVER_EN_ROUTE("Driver en route to pickup"),
    DRIVER_ARRIVED("Driver has arrived"),
    ITEM_PICKED_UP("Item picked up"),
    EN_ROUTE_TO_DELIVERY("En route to delivery"),
    DELIVERED("Delivered")
}

data class TrackingUiState(
    val deliveryStatus: DeliveryStatus = DeliveryStatus.DRIVER_EN_ROUTE,
    val driverLatitude: Double = 5.614818, // Default Accra location
    val driverLongitude: Double = -0.186964,
    val estimatedArrivalMinutes: Int = 8,
    val isDriverOnline: Boolean = true,
    val lastLocationUpdateTime: Long = System.currentTimeMillis()
)

class TrackingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()
    
    fun updateDeliveryStatus(status: DeliveryStatus) {
        _uiState.update { currentState ->
            currentState.copy(
                deliveryStatus = status,
                lastLocationUpdateTime = System.currentTimeMillis()
            )
        }
    }
    
    fun updateDriverLocation(latitude: Double, longitude: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                driverLatitude = latitude,
                driverLongitude = longitude,
                lastLocationUpdateTime = System.currentTimeMillis()
            )
        }
    }
    
    fun updateEstimatedArrival(minutes: Int) {
        _uiState.update { currentState ->
            currentState.copy(estimatedArrivalMinutes = minutes)
        }
    }
    
    fun setDriverOnlineStatus(isOnline: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isDriverOnline = isOnline)
        }
    }
}
