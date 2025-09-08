package io.peng.sparrowdelivery.presentation.features.tracking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.data.models.DeliveryTracking
import io.peng.sparrowdelivery.data.models.DeliveryTrackingStatus
import io.peng.sparrowdelivery.data.models.TrackingEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTrackingLookupScreen(
    onBackClick: () -> Unit = {},
    onChatClick: (DeliveryTracking) -> Unit = {},
    onCallDriverClick: (String) -> Unit = {},
    viewModel: TrackingLookupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = if (uiState.showCodeInput) "Track Delivery" else "Delivery Tracking",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (uiState.showCodeInput) {
                            onBackClick()
                        } else {
                            viewModel.goBackToCodeInput()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (!uiState.showCodeInput && uiState.deliveryTracking != null) {
                    IconButton(
                        onClick = { viewModel.refreshDelivery() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            }
        )
        
        AnimatedVisibility(
            visible = uiState.showCodeInput,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeIn(tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeOut(tween(300))
        ) {
            TrackingCodeInputSection(
                uiState = uiState,
                onTrackingCodeChanged = viewModel::onTrackingCodeChanged,
                onLookupDelivery = {
                    keyboardController?.hide()
                    viewModel.lookupDelivery()
                },
                onSelectDelivery = viewModel::selectDelivery,
                onClearError = viewModel::clearError
            )
        }
        
        AnimatedVisibility(
            visible = !uiState.showCodeInput && uiState.deliveryTracking != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeIn(tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut(tween(300))
        ) {
            uiState.deliveryTracking?.let { delivery ->
                DeliveryTrackingSection(
                    delivery = delivery,
                    onChatClick = { onChatClick(delivery) },
                    onCallDriverClick = { 
                        delivery.driverInfo?.phone?.let { onCallDriverClick(it) }
                    }
                )
            }
        }
    }
}

@Composable
private fun TrackingCodeInputSection(
    uiState: TrackingLookupUiState,
    onTrackingCodeChanged: (String) -> Unit,
    onLookupDelivery: () -> Unit,
    onSelectDelivery: (DeliveryTracking) -> Unit,
    onClearError: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tracking Code Input Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Enter Tracking Code",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Enter your 8-character tracking code to see your delivery progress",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = uiState.trackingCode,
                    onValueChange = onTrackingCodeChanged,
                    placeholder = { Text("XXXX-XXXX") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { onLookupDelivery() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Button(
                    onClick = onLookupDelivery,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && uiState.trackingCode.isNotEmpty()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (uiState.isLoading) "Looking up..." else "Track Delivery")
                }
            }
        }
        
        // Error Message
        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClearError) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        
        // Active Deliveries Section
        if (uiState.activeDeliveries.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Deliveries",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${uiState.activeDeliveries.size} active",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    LazyColumn(
                        modifier = Modifier.height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.activeDeliveries) { delivery ->
                            ActiveDeliveryItem(
                                delivery = delivery,
                                onClick = { onSelectDelivery(delivery) }
                            )
                        }
                    }
                }
            }
        } else {
            // No active deliveries message
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No deliveries",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "Try Sample Codes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "Try these sample tracking codes:\nABCD-1234 • EFGH-5678 • IJKL-9012",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveDeliveryItem(
    delivery: DeliveryTracking,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(getStatusColor(delivery.status))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = delivery.trackingCode,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = delivery.status.displayName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DeliveryTrackingSection(
    delivery: DeliveryTracking,
    onChatClick: () -> Unit,
    onCallDriverClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status and Progress Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = delivery.trackingCode,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = delivery.getFormattedCreatedAt(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    StatusBadge(status = delivery.status)
                }
                
                // Progress Bar
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = delivery.status.displayName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    LinearProgressIndicator(
                        progress = { delivery.getProgressPercentage() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = getStatusColor(delivery.status),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
                    Text(
                        text = delivery.status.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Driver Information
        delivery.driverInfo?.let { driver ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Driver Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = driver.name.first().toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = driver.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFFFA726)
                                )
                                Text(
                                    text = " ${driver.rating} • ${driver.vehicleType}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = driver.plateNumber,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Row {
                            IconButton(onClick = onChatClick) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Chat"
                                )
                            }
                            IconButton(onClick = onCallDriverClick) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call"
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Locations Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Delivery Route",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                LocationItem(
                    icon = Icons.Default.LocationOn,
                    title = "Pickup Location",
                    address = delivery.pickupLocation.address,
                    contact = delivery.pickupLocation.contactName,
                    iconColor = Color(0xFF4CAF50)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LocationItem(
                    icon = Icons.Default.Place,
                    title = "Delivery Location",
                    address = delivery.dropoffLocation.address,
                    contact = delivery.dropoffLocation.contactName,
                    iconColor = Color(0xFFF44336)
                )
            }
        }
        
        // Timeline Card
        if (delivery.timeline.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Delivery Timeline",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    delivery.timeline.forEach { event ->
                        TimelineItem(event = event)
                        if (event != delivery.timeline.last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: DeliveryTrackingStatus) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = getStatusColor(status).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status.displayName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = getStatusColor(status),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LocationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    address: String,
    contact: String?,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = address,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            contact?.let {
                Text(
                    text = "Contact: $it",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(event: TrackingEvent) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(getStatusColor(event.status))
                .padding(top = 6.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = event.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = event.getFormattedTimestamp(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getStatusColor(status: DeliveryTrackingStatus): Color {
    return when (status) {
        DeliveryTrackingStatus.CREATED -> Color(0xFFFFA726)
        DeliveryTrackingStatus.DRIVER_ASSIGNED -> Color(0xFF42A5F5)
        DeliveryTrackingStatus.DRIVER_EN_ROUTE_TO_PICKUP -> Color(0xFFAB47BC)
        DeliveryTrackingStatus.DRIVER_ARRIVED_AT_PICKUP -> Color(0xFF26C6DA)
        DeliveryTrackingStatus.ITEM_PICKED_UP -> Color(0xFF66BB6A)
        DeliveryTrackingStatus.EN_ROUTE_TO_DELIVERY -> Color(0xFFFF7043)
        DeliveryTrackingStatus.DRIVER_ARRIVED_AT_DROPOFF -> Color(0xFF9CCC65)
        DeliveryTrackingStatus.DELIVERED -> Color(0xFF4CAF50)
        DeliveryTrackingStatus.CANCELLED -> Color(0xFFEF5350)
        DeliveryTrackingStatus.FAILED -> Color(0xFFE57373)
    }
}
