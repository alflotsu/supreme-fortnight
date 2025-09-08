package io.peng.sparrowdelivery.data.models

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class DeliveryTracking(
    val id: String,
    val trackingCode: String,
    val externalBookingId: String? = null,
    val status: DeliveryTrackingStatus,
    val currentLocation: LocationUpdate? = null,
    val pickupLocation: TrackingLocation,
    val dropoffLocation: TrackingLocation,
    val estimatedArrivalTime: String? = null,
    val driverInfo: DriverTrackingInfo? = null,
    val customerInfo: CustomerTrackingInfo,
    val timeline: List<TrackingEvent> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val notes: String? = null,
    val priority: String = "NORMAL"
) {
    fun getFormattedCreatedAt(): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val displayFormat = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
            val date = isoFormat.parse(createdAt) ?: Date()
            displayFormat.format(date)
        } catch (e: Exception) {
            createdAt
        }
    }

    fun getProgressPercentage(): Float {
        return when (status) {
            DeliveryTrackingStatus.CREATED -> 0.1f
            DeliveryTrackingStatus.DRIVER_ASSIGNED -> 0.2f
            DeliveryTrackingStatus.DRIVER_EN_ROUTE_TO_PICKUP -> 0.3f
            DeliveryTrackingStatus.DRIVER_ARRIVED_AT_PICKUP -> 0.4f
            DeliveryTrackingStatus.ITEM_PICKED_UP -> 0.5f
            DeliveryTrackingStatus.EN_ROUTE_TO_DELIVERY -> 0.7f
            DeliveryTrackingStatus.DRIVER_ARRIVED_AT_DROPOFF -> 0.9f
            DeliveryTrackingStatus.DELIVERED -> 1.0f
            DeliveryTrackingStatus.CANCELLED -> 0f
            DeliveryTrackingStatus.FAILED -> 0f
        }
    }
}

@Serializable
enum class DeliveryTrackingStatus(val displayName: String, val description: String) {
    CREATED("Order Created", "Your delivery request has been created"),
    DRIVER_ASSIGNED("Driver Assigned", "A driver has been assigned to your delivery"),
    DRIVER_EN_ROUTE_TO_PICKUP("En Route to Pickup", "Driver is heading to pickup location"),
    DRIVER_ARRIVED_AT_PICKUP("Arrived at Pickup", "Driver has arrived at pickup location"),
    ITEM_PICKED_UP("Item Picked Up", "Your item has been picked up"),
    EN_ROUTE_TO_DELIVERY("En Route to Delivery", "Driver is heading to delivery location"),
    DRIVER_ARRIVED_AT_DROPOFF("Arrived at Dropoff", "Driver has arrived at delivery location"),
    DELIVERED("Delivered", "Your item has been delivered successfully"),
    CANCELLED("Cancelled", "Delivery has been cancelled"),
    FAILED("Failed", "Delivery could not be completed")
}

@Serializable
data class TrackingLocation(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val notes: String? = null,
    val contactName: String? = null,
    val contactPhone: String? = null
)

@Serializable
data class LocationUpdate(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val timestamp: String,
    val accuracy: Float? = null
)

@Serializable
data class DriverTrackingInfo(
    val id: String,
    val name: String,
    val phone: String,
    val vehicleType: String,
    val plateNumber: String,
    val rating: Float,
    val profileImageUrl: String? = null,
    val isOnline: Boolean = true
)

@Serializable
data class CustomerTrackingInfo(
    val name: String,
    val phone: String? = null,
    val email: String? = null
)

@Serializable
data class TrackingEvent(
    val id: String,
    val status: DeliveryTrackingStatus,
    val timestamp: String,
    val description: String,
    val location: TrackingLocation? = null,
    val notes: String? = null
) {
    fun getFormattedTimestamp(): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val displayFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
            val date = isoFormat.parse(timestamp) ?: Date()
            displayFormat.format(date)
        } catch (e: Exception) {
            timestamp
        }
    }
}

// Helper function to generate tracking codes
object TrackingCodeGenerator {
    fun generateTrackingCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
            .chunked(4)
            .joinToString("-")
    }
}
