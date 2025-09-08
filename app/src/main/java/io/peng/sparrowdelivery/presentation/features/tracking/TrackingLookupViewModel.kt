package io.peng.sparrowdelivery.presentation.features.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.peng.sparrowdelivery.data.models.DeliveryTracking
import io.peng.sparrowdelivery.data.repository.MockDeliveryTrackingRepository
import io.peng.sparrowdelivery.domain.repository.DeliveryTrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackingLookupUiState(
    val trackingCode: String = "",
    val deliveryTracking: DeliveryTracking? = null,
    val activeDeliveries: List<DeliveryTracking> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showCodeInput: Boolean = true,
    val isSubscribedToUpdates: Boolean = false
)

class TrackingLookupViewModel(
    private val repository: DeliveryTrackingRepository = MockDeliveryTrackingRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TrackingLookupUiState())
    val uiState: StateFlow<TrackingLookupUiState> = _uiState.asStateFlow()
    
    init {
        loadActiveDeliveries()
    }
    
    fun onTrackingCodeChanged(code: String) {
        // Format the code with dashes every 4 characters and convert to uppercase
        val formattedCode = code.uppercase()
            .replace("-", "")
            .take(8)
            .chunked(4)
            .joinToString("-")
        
        _uiState.update { currentState ->
            currentState.copy(
                trackingCode = formattedCode,
                errorMessage = null
            )
        }
    }
    
    fun lookupDelivery() {
        val code = _uiState.value.trackingCode.trim()
        if (code.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter a tracking code") }
            return
        }
        
        if (!isValidTrackingCode(code)) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid tracking code (format: XXXX-XXXX)") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            repository.getDeliveryByTrackingCode(code)
                .onSuccess { delivery ->
                    if (delivery != null) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                deliveryTracking = delivery,
                                isLoading = false,
                                showCodeInput = false,
                                errorMessage = null
                            )
                        }
                        // Subscribe to real-time updates
                        subscribeToDeliveryUpdates(code)
                    } else {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                errorMessage = "No delivery found with tracking code: $code"
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = "Error looking up delivery: ${exception.message}"
                        )
                    }
                }
        }
    }
    
    fun loadActiveDeliveries() {
        viewModelScope.launch {
            repository.getAllActiveDeliveries()
                .onSuccess { deliveries ->
                    _uiState.update { currentState ->
                        currentState.copy(activeDeliveries = deliveries)
                    }
                }
                .onFailure { exception ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            errorMessage = "Error loading active deliveries: ${exception.message}"
                        )
                    }
                }
        }
    }
    
    fun selectDelivery(delivery: DeliveryTracking) {
        _uiState.update { currentState ->
            currentState.copy(
                deliveryTracking = delivery,
                trackingCode = delivery.trackingCode,
                showCodeInput = false,
                errorMessage = null
            )
        }
        // Subscribe to real-time updates
        subscribeToDeliveryUpdates(delivery.trackingCode)
    }
    
    fun goBackToCodeInput() {
        _uiState.update { currentState ->
            currentState.copy(
                deliveryTracking = null,
                showCodeInput = true,
                isSubscribedToUpdates = false,
                errorMessage = null
            )
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun refreshDelivery() {
        val trackingCode = _uiState.value.deliveryTracking?.trackingCode
        if (trackingCode != null) {
            _uiState.update { it.copy(trackingCode = trackingCode) }
            lookupDelivery()
        }
    }
    
    private fun subscribeToDeliveryUpdates(trackingCode: String) {
        if (_uiState.value.isSubscribedToUpdates) return
        
        _uiState.update { it.copy(isSubscribedToUpdates = true) }
        
        viewModelScope.launch {
            repository.subscribeToDeliveryUpdates(trackingCode)
                .catch { exception ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            errorMessage = "Error subscribing to delivery updates: ${exception.message}"
                        )
                    }
                }
                .onEach { updatedDelivery ->
                    updatedDelivery?.let { delivery ->
                        _uiState.update { currentState ->
                            currentState.copy(deliveryTracking = delivery)
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }
    
    private fun isValidTrackingCode(code: String): Boolean {
        // Valid format: XXXX-XXXX where X is alphanumeric
        val pattern = Regex("^[A-Z0-9]{4}-[A-Z0-9]{4}$")
        return pattern.matches(code)
    }
    
    // For testing purposes - samples are already loaded from MockDeliveryTrackingRepository
    fun createSampleDeliveries() {
        // Sample deliveries are already available from MockDeliveryTrackingRepository
        loadActiveDeliveries()
    }
}
