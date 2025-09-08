package io.peng.sparrowdelivery.integration

import android.net.Uri

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

sealed class DeepLinkDestination {
    data class Chat(val bookingId: String) : DeepLinkDestination()
    data class Track(val bookingId: String) : DeepLinkDestination()
    data class Booking(val bookingId: String) : DeepLinkDestination()
    data class BookingRequest(
        val pickup: LocationData,
        val dropoff: LocationData,
        val callbackScheme: String? = null,
        val referenceId: String? = null
    ) : DeepLinkDestination()
    object Unknown : DeepLinkDestination()
}

object DeepLinkHandler {
    // Supported examples:
    // sparrowdelivery://chat?booking=123
    // sparrowdelivery://track?booking=123
    // sparrowdelivery://book?pickup_lat=5.6037&pickup_lng=-0.1870&pickup_address=Accra%20Mall&dropoff_lat=5.6492&dropoff_lng=-0.1815&dropoff_address=Legon&callback=external_app://callback&reference=order123
    // https://sparrowdelivery.app/chat?booking=123
    fun parse(uri: Uri?): DeepLinkDestination {
        if (uri == null) return DeepLinkDestination.Unknown
        val pathOrHost = uri.host ?: uri.pathSegments.firstOrNull()
        val firstSegment = when (uri.scheme) {
            "http", "https" -> uri.pathSegments.firstOrNull()
            else -> pathOrHost
        } ?: return DeepLinkDestination.Unknown

        val bookingId = uri.getQueryParameter("booking") ?: uri.getQueryParameter("booking_id")
        
        return when (firstSegment.lowercase()) {
            "chat" -> if (bookingId != null) DeepLinkDestination.Chat(bookingId) else DeepLinkDestination.Unknown
            "track" -> if (bookingId != null) DeepLinkDestination.Track(bookingId) else DeepLinkDestination.Unknown
            "booking" -> if (bookingId != null) DeepLinkDestination.Booking(bookingId) else DeepLinkDestination.Unknown
            "book" -> parseBookingRequest(uri)
            else -> DeepLinkDestination.Unknown
        }
    }
    
    private fun parseBookingRequest(uri: Uri): DeepLinkDestination {
        val pickupLat = uri.getQueryParameter("pickup_lat")?.toDoubleOrNull()
        val pickupLng = uri.getQueryParameter("pickup_lng")?.toDoubleOrNull()
        val pickupAddress = uri.getQueryParameter("pickup_address")
        
        val dropoffLat = uri.getQueryParameter("dropoff_lat")?.toDoubleOrNull()
        val dropoffLng = uri.getQueryParameter("dropoff_lng")?.toDoubleOrNull()
        val dropoffAddress = uri.getQueryParameter("dropoff_address")
        
        val callbackScheme = uri.getQueryParameter("callback")
        val referenceId = uri.getQueryParameter("reference")
        
        // Validate required parameters
        if (pickupLat == null || pickupLng == null || pickupAddress == null ||
            dropoffLat == null || dropoffLng == null || dropoffAddress == null) {
            return DeepLinkDestination.Unknown
        }
        
        return DeepLinkDestination.BookingRequest(
            pickup = LocationData(pickupLat, pickupLng, pickupAddress),
            dropoff = LocationData(dropoffLat, dropoffLng, dropoffAddress),
            callbackScheme = callbackScheme,
            referenceId = referenceId
        )
    }
}

