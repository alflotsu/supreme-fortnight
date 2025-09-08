package io.peng.sparrowdelivery.integration

import kotlinx.serialization.Serializable

@Serializable
data class ExternalBookingRequest(
    val pickup: LocationRequest,
    val dropoff: LocationRequest,
    val externalRef: String? = null,
    val customerInfo: CustomerInfo? = null,
    val notes: String? = null,
    val priority: BookingPriority = BookingPriority.NORMAL
)

@Serializable
data class LocationRequest(
    val address: String,
    val lat: Double? = null,
    val lng: Double? = null,
    val notes: String? = null
)

@Serializable
data class CustomerInfo(
    val name: String,
    val phone: String? = null,
    val email: String? = null
)

@Serializable
enum class BookingPriority {
    LOW, NORMAL, HIGH, URGENT
}

@Serializable
data class ExternalBookingResponse(
    val bookingId: String,
    val status: BookingStatus,
    val estimatedPickupTime: String? = null,
    val deepLinks: DeepLinks,
    val externalRef: String? = null
)

@Serializable
data class DeepLinks(
    val chat: String,
    val track: String,
    val booking: String
)

@Serializable
enum class BookingStatus {
    CREATED, 
    DRIVER_ASSIGNED, 
    PICKUP_IN_PROGRESS, 
    IN_TRANSIT, 
    DELIVERED, 
    CANCELLED
}

// Helper to generate deep links for external responses
object DeepLinkGenerator {
    fun generateLinks(bookingId: String): DeepLinks {
        return DeepLinks(
            chat = "sparrowdelivery://chat?booking=$bookingId",
            track = "sparrowdelivery://track?booking=$bookingId", 
            booking = "sparrowdelivery://booking?booking=$bookingId"
        )
    }
    
    fun generateWebLinks(bookingId: String): DeepLinks {
        return DeepLinks(
            chat = "https://sparrowdelivery.app/chat?booking=$bookingId",
            track = "https://sparrowdelivery.app/track?booking=$bookingId",
            booking = "https://sparrowdelivery.app/booking?booking=$bookingId"
        )
    }
}
