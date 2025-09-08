package io.peng.sparrowdelivery.presentation.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    order: Order,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order #${order.orderNumber}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (order.status == OrderStatus.IN_TRANSIT) {
                        IconButton(onClick = { /* TODO: Real-time tracking */ }) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Track",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Order Status Header
            item {
                OrderStatusCard(order = order)
            }
            
            // Map Section
            item {
                OrderMapCard(order = order)
            }
            
            // Order Items
            item {
                OrderItemsCard(items = order.items)
            }
            
            // Delivery Information
            item {
                DeliveryInfoCard(order = order)
            }
            
            // Payment Information
            item {
                PaymentInfoCard(order = order)
            }
            
            // Tracking History (if available)
            if (order.trackingDetails != null) {
                item {
                    TrackingHistoryCard(trackingDetails = order.trackingDetails)
                }
            }
            
            // Driver Information (if available)
            if (order.driverName != null) {
                item {
                    DriverInfoCard(order = order)
                }
            }
        }
    }
}

@Composable
private fun OrderStatusCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when (order.status) {
                    OrderStatus.PENDING -> MaterialTheme.colorScheme.primary
                    OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
                    OrderStatus.PICKED_UP -> MaterialTheme.colorScheme.tertiary
                    OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.secondary
                    OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primary
                    OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                    OrderStatus.REFUNDED -> MaterialTheme.colorScheme.error
                }
            ) {
                Text(
                    text = order.status.displayName,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = order.formattedOrderDate,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            if (order.deliveryDate != null) {
                Text(
                    text = "Delivered: ${order.formattedDeliveryDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else if (order.trackingDetails?.estimatedDeliveryTime != null) {
                Text(
                    text = "ETA: ${order.trackingDetails.estimatedDeliveryTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun OrderMapCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📍 Route",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Map View
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                factory = { context ->
                    MapView(context).apply {
                        onCreate(null)
                        onResume()
                        getMapAsync { googleMap ->
                            val pickupLatLng = LatLng(order.pickupAddress.latitude, order.pickupAddress.longitude)
                            val deliveryLatLng = LatLng(order.deliveryAddress.latitude, order.deliveryAddress.longitude)
                            
                            // Add markers
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(pickupLatLng)
                                    .title("Pickup")
                                    .snippet(order.pickupAddress.fullAddress)
                            )
                            
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(deliveryLatLng)
                                    .title("Delivery")
                                    .snippet(order.deliveryAddress.fullAddress)
                            )
                            
                            // Add current location if available
                            order.trackingDetails?.currentLocation?.let { currentLoc ->
                                val currentLatLng = LatLng(currentLoc.latitude, currentLoc.longitude)
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(currentLatLng)
                                        .title("Current Location")
                                        .snippet(currentLoc.fullAddress)
                                )
                            }
                            
                            // Draw route line
                            googleMap.addPolyline(
                                PolylineOptions()
                                    .add(pickupLatLng)
                                    .add(deliveryLatLng)
                                    .width(5f)
                                    .color(0xFF2563EB.toInt()) // Blue primary color
                            )
                            
                            // Center camera
                            val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
                                .include(pickupLatLng)
                                .include(deliveryLatLng)
                                .build()
                            
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                        }
                    }
                }
            )
            
            // Address Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "From",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = order.pickupAddress.fullAddress,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "To",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = order.deliveryAddress.fullAddress,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemsCard(items: List<OrderItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📦 Items (${items.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            items.forEach { item ->
                OrderItemRow(item = item)
                if (item != items.last()) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Qty: ${item.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = NumberFormat.getCurrencyInstance(Locale.US).format(item.totalPrice),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DeliveryInfoCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🚚 Delivery Info",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            InfoRow(
                label = "Delivery Address",
                value = "${order.deliveryAddress.fullAddress}${
                    order.deliveryAddress.label?.let { " ($it)" } ?: ""
                }"
            )
            
            if (!order.specialInstructions.isNullOrBlank()) {
                InfoRow(
                    label = "Special Instructions",
                    value = order.specialInstructions
                )
            }
        }
    }
}

@Composable
private fun PaymentInfoCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "💳 Payment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            InfoRow(
                label = "Subtotal",
                value = NumberFormat.getCurrencyInstance(Locale.US).format(order.totalAmount)
            )
            
            InfoRow(
                label = "Delivery Fee",
                value = NumberFormat.getCurrencyInstance(Locale.US).format(order.deliveryFee)
            )
            
            HorizontalDivider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.US).format(order.totalWithFees),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            InfoRow(
                label = "Payment Method",
                value = order.paymentMethod
            )
        }
    }
}

@Composable
private fun TrackingHistoryCard(trackingDetails: TrackingDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📋 Tracking History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            trackingDetails.statusHistory.forEach { update ->
                StatusUpdateRow(statusUpdate = update)
                if (update != trackingDetails.statusHistory.last()) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun StatusUpdateRow(statusUpdate: StatusUpdate) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(
                    when (statusUpdate.status) {
                        OrderStatus.PENDING -> MaterialTheme.colorScheme.primary
                        OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
                        OrderStatus.PICKED_UP -> MaterialTheme.colorScheme.tertiary
                        OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.secondary
                        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primary
                        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                        OrderStatus.REFUNDED -> MaterialTheme.colorScheme.error
                    }
                )
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = statusUpdate.status.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = statusUpdate.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (statusUpdate.location != null) {
                Text(
                    text = statusUpdate.location.fullAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Text(
            text = statusUpdate.formattedTimestamp,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DriverInfoCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "👨‍💼 Driver",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.driverName ?: "Assigned",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (order.driverPhone != null) {
                        Text(
                            text = order.driverPhone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (order.driverPhone != null && order.status == OrderStatus.IN_TRANSIT) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { /* TODO: Call driver */ }
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call")
                        }
                        IconButton(
                            onClick = { /* TODO: Message driver */ }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Message")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}
