package io.peng.sparrowdelivery.presentation.features.external

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.peng.sparrowdelivery.integration.LocationData
import io.peng.sparrowdelivery.presentation.features.home.DriverInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ExternalBookingStatus {
    FINDING_DRIVERS,
    DRIVER_FOUND,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    ERROR
}

data class ExternalBookingUiState(
    val status: ExternalBookingStatus = ExternalBookingStatus.FINDING_DRIVERS,
    val pickup: LocationData? = null,
    val dropoff: LocationData? = null,
    val referenceId: String? = null,
    val estimatedPrice: Double? = null,
    val totalPrice: Double = 0.0,
    val estimatedArrival: String = "",
    val bookingId: String? = null,
    val driver: DriverInfo? = null,
    val error: String? = null
)

class ExternalBookingViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExternalBookingUiState())
    val uiState: StateFlow<ExternalBookingUiState> = _uiState.asStateFlow()
    
    fun startBooking(pickup: LocationData, dropoff: LocationData, referenceId: String?) {
        _uiState.value = _uiState.value.copy(
            pickup = pickup,
            dropoff = dropoff,
            referenceId = referenceId,
            status = ExternalBookingStatus.FINDING_DRIVERS
        )
        
        viewModelScope.launch {
            try {
                // Calculate estimated price
                val distance = calculateDistance(pickup, dropoff)
                val estimatedPrice = calculatePrice(distance)
                
                _uiState.value = _uiState.value.copy(
                    estimatedPrice = estimatedPrice
                )
                
                // Simulate finding drivers (2-5 seconds)
                delay(Random.nextLong(2000, 5000))
                
                // Simulate driver found
                val driver = generateRandomDriver()
                val totalPrice = estimatedPrice + Random.nextDouble(0.0, 2.0) // Small variation
                val arrival = generateArrivalTime()
                val bookingId = generateBookingId()
                
                _uiState.value = _uiState.value.copy(
                    status = ExternalBookingStatus.DRIVER_FOUND,
                    driver = driver,
                    totalPrice = totalPrice,
                    estimatedArrival = arrival,
                    bookingId = bookingId
                )
                
                // Auto-progress to in progress after 3 seconds
                delay(3000)
                _uiState.value = _uiState.value.copy(
                    status = ExternalBookingStatus.IN_PROGRESS
                )
                
                // Simulate delivery progress (10-20 seconds)
                delay(Random.nextLong(10000, 20000))
                
                // Complete delivery
                _uiState.value = _uiState.value.copy(
                    status = ExternalBookingStatus.COMPLETED
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    status = ExternalBookingStatus.ERROR,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
    
    fun retryBooking() {
        _uiState.value.pickup?.let { pickup ->
            _uiState.value.dropoff?.let { dropoff ->
                startBooking(pickup, dropoff, _uiState.value.referenceId)
            }
        }
    }
    
    fun cancelBooking() {
        _uiState.value = _uiState.value.copy(
            status = ExternalBookingStatus.CANCELLED
        )
    }
    
    private fun calculateDistance(pickup: LocationData, dropoff: LocationData): Double {
        // Simple haversine distance calculation
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(dropoff.latitude - pickup.latitude)
        val dLon = Math.toRadians(dropoff.longitude - pickup.longitude)
        val a = kotlin.math.sin(dLat/2) * kotlin.math.sin(dLat/2) +
                kotlin.math.cos(Math.toRadians(pickup.latitude)) * kotlin.math.cos(Math.toRadians(dropoff.latitude)) *
                kotlin.math.sin(dLon/2) * kotlin.math.sin(dLon/2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1-a))
        return earthRadius * c
    }
    
    private fun calculatePrice(distance: Double): Double {
        // Base price + distance-based pricing
        val basePrice = 3.0
        val pricePerKm = 2.5
        return basePrice + (distance * pricePerKm)
    }
    
    private fun generateRandomDriver(): DriverInfo {
        val drivers = listOf(
            DriverInfo("Kwame Asante", 4.8f, "Toyota Camry", "GR-4587-20", null, "+233-24-123-4567", 0.0, ""),
            DriverInfo("Akosua Mensah", 4.9f, "Honda Accord", "GR-2134-21", null, "+233-24-987-6543", 0.0, ""),
            DriverInfo("Kofi Owusu", 4.7f, "Nissan Sentra", "GR-8765-19", null, "+233-24-456-7890", 0.0, ""),
            DriverInfo("Ama Gyasi", 4.6f, "Hyundai Elantra", "GR-3456-22", null, "+233-24-789-0123", 0.0, ""),
            DriverInfo("Yaw Boateng", 4.8f, "Toyota Corolla", "GR-9876-20", null, "+233-24-234-5678", 0.0, "")
        )
        return drivers.random()
    }
    
    private fun generateArrivalTime(): String {
        val minutes = Random.nextInt(3, 12)
        return "$minutes mins"
    }
    
    private fun generateBookingId(): String {
        return "EXT${System.currentTimeMillis().toString().takeLast(6)}"
    }
}
