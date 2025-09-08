package io.peng.sparrowdelivery.presentation.features.profile

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Order(
    val id: String,
    val orderNumber: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val orderDate: Date,
    val deliveryDate: Date?,
    val totalAmount: Double,
    val deliveryAddress: DeliveryAddress,
    val pickupAddress: DeliveryAddress,
    val driverName: String?,
    val driverPhone: String?,
    val trackingDetails: TrackingDetails?,
    val paymentMethod: String,
    val deliveryFee: Double,
    val specialInstructions: String?
) {
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
    
    val formattedOrderDate: String
        get() = dateFormatter.format(orderDate)
    
    val formattedDeliveryDate: String
        get() = deliveryDate?.let { dateFormatter.format(it) } ?: "Not delivered"
    
    val totalWithFees: Double
        get() = totalAmount + deliveryFee
}

data class OrderItem(
    val id: String,
    val name: String,
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val imageUrl: String?
) {
    val totalPrice: Double
        get() = quantity * unitPrice
}

enum class OrderStatus(val displayName: String, val colorCode: String) {
    PENDING("Pending", "#FFA726"),
    CONFIRMED("Confirmed", "#42A5F5"),
    PICKED_UP("Picked Up", "#AB47BC"),
    IN_TRANSIT("In Transit", "#FF7043"),
    DELIVERED("Delivered", "#66BB6A"),
    CANCELLED("Cancelled", "#EF5350"),
    REFUNDED("Refunded", "#8D6E63")
}

data class DeliveryAddress(
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
    val label: String? = null
)

data class TrackingDetails(
    val currentLocation: DeliveryAddress?,
    val estimatedDeliveryTime: Date?,
    val statusHistory: List<StatusUpdate>
)

data class StatusUpdate(
    val status: OrderStatus,
    val timestamp: Date,
    val description: String,
    val location: DeliveryAddress?
) {
    private val timeFormatter = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    
    val formattedTimestamp: String
        get() = timeFormatter.format(timestamp)
}
