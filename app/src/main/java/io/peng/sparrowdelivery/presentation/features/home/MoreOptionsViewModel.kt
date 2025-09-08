package io.peng.sparrowdelivery.presentation.features.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class MoreOptionsUiState(
    val packageSize: PackageSize = PackageSize.MEDIUM,
    val packageWeight: String = "",
    val packageDescription: String = "",
    val deliveryTime: MoreDeliveryTime = MoreDeliveryTime.STANDARD,
    val scheduledDateTime: LocalDateTime? = null,
    val transportMode: TransportMode = TransportMode.COMPACT_CAR,
    val specialInstructions: String = "",
    val requireSignature: Boolean = false,
    val fragileItems: Boolean = false,
    val priorityDelivery: Boolean = false,
    val contactRecipient: Boolean = true,
    val recipientName: String = "",
    val recipientPhone: String = "",
    val estimatedPrice: Double = 0.0,
    val isUpdating: Boolean = false
)

enum class PackageSize(val displayName: String, val description: String) {
    SMALL("Small", "Up to 1 kg, fits in mailbox"),
    MEDIUM("Medium", "Up to 5 kg, standard box"),
    LARGE("Large", "Up to 15 kg, large box"),
    EXTRA_LARGE("Extra Large", "Up to 30 kg, requires special handling")
}

enum class MoreDeliveryTime(val displayName: String, val description: String) {
    EXPRESS("Express", "Within 1-2 hours"),
    STANDARD("Standard", "Same day delivery"),
    SCHEDULED("Scheduled", "Choose specific date and time"),
    ECONOMY("Economy", "Next day delivery")
}

enum class TransportMode(
    val displayName: String, 
    val description: String, 
    val emoji: String,
    val priceMultiplier: Double,
    val maxWeight: Double // in kg
) {
    MOTORCYCLE("Motorcycle", "Fast & agile, small packages", "🏍️", 0.7, 5.0),
    COMPACT_CAR("Compact Car", "Standard delivery, most packages", "🚗", 1.0, 25.0),
    SEDAN("Sedan", "Comfortable, medium packages", "🚙", 1.2, 40.0),
    VAN("Van", "Large packages & bulk items", "🚐", 1.8, 100.0)
}

class MoreOptionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MoreOptionsUiState())
    val uiState: StateFlow<MoreOptionsUiState> = _uiState.asStateFlow()

    fun updatePackageSize(size: PackageSize) {
        _uiState.value = _uiState.value.copy(packageSize = size)
        calculateEstimatedPrice()
    }

    fun updatePackageWeight(weight: String) {
        _uiState.value = _uiState.value.copy(packageWeight = weight)
        calculateEstimatedPrice()
    }

    fun updatePackageDescription(description: String) {
        _uiState.value = _uiState.value.copy(packageDescription = description)
    }

    fun updateDeliveryTime(time: MoreDeliveryTime) {
        _uiState.value = _uiState.value.copy(
            deliveryTime = time,
            scheduledDateTime = if (time != MoreDeliveryTime.SCHEDULED) null else _uiState.value.scheduledDateTime
        )
        calculateEstimatedPrice()
    }

    fun updateScheduledDateTime(dateTime: LocalDateTime?) {
        _uiState.value = _uiState.value.copy(scheduledDateTime = dateTime)
    }

    fun updateSpecialInstructions(instructions: String) {
        _uiState.value = _uiState.value.copy(specialInstructions = instructions)
    }

    fun updateRequireSignature(require: Boolean) {
        _uiState.value = _uiState.value.copy(requireSignature = require)
        calculateEstimatedPrice()
    }

    fun updateFragileItems(fragile: Boolean) {
        _uiState.value = _uiState.value.copy(fragileItems = fragile)
        calculateEstimatedPrice()
    }

    fun updatePriorityDelivery(priority: Boolean) {
        _uiState.value = _uiState.value.copy(priorityDelivery = priority)
        calculateEstimatedPrice()
    }

    fun updateContactRecipient(contact: Boolean) {
        _uiState.value = _uiState.value.copy(contactRecipient = contact)
    }

    fun updateRecipientName(name: String) {
        _uiState.value = _uiState.value.copy(recipientName = name)
    }

    fun updateRecipientPhone(phone: String) {
        _uiState.value = _uiState.value.copy(recipientPhone = phone)
    }

    fun updateTransportMode(mode: TransportMode) {
        _uiState.value = _uiState.value.copy(transportMode = mode)
        calculateEstimatedPrice()
    }

    private fun calculateEstimatedPrice() {
        val state = _uiState.value
        var basePrice = 10.0 // Base delivery price

        // Package size pricing
        basePrice += when (state.packageSize) {
            PackageSize.SMALL -> 0.0
            PackageSize.MEDIUM -> 5.0
            PackageSize.LARGE -> 15.0
            PackageSize.EXTRA_LARGE -> 30.0
        }

        // Delivery time pricing
        basePrice *= when (state.deliveryTime) {
            MoreDeliveryTime.EXPRESS -> 2.5
            MoreDeliveryTime.STANDARD -> 1.0
            MoreDeliveryTime.SCHEDULED -> 1.2
            MoreDeliveryTime.ECONOMY -> 0.8
        }

        // Additional services
        if (state.requireSignature) basePrice += 3.0
        if (state.fragileItems) basePrice += 8.0
        if (state.priorityDelivery) basePrice += 12.0

        // Weight-based pricing
        state.packageWeight.toDoubleOrNull()?.let { weight ->
            if (weight > 10) {
                basePrice += (weight - 10) * 2.0 // $2 per kg over 10kg
            }
        }

        // Transport mode pricing
        basePrice *= state.transportMode.priceMultiplier

        _uiState.value = _uiState.value.copy(estimatedPrice = basePrice)
    }

    fun resetOptions() {
        _uiState.value = MoreOptionsUiState()
    }
}
