package io.peng.sparrowdelivery.presentation.features.tracking

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import io.peng.sparrowdelivery.presentation.features.home.DriverInfo
import io.peng.sparrowdelivery.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    driverInfo: DriverInfo,
    pickupLocation: String,
    dropoffLocation: String,
    onBackClick: () -> Unit,
    onMessageClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Default location (Accra, Ghana) for demo purposes
    val driverLocation = LatLng(5.614818, -0.186964)
    val pickupLatLng = LatLng(5.614818, -0.196964)
    val dropoffLatLng = LatLng(5.624818, -0.176964)
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverLocation, 14f)
    }
    
    ShadcnTheme {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // Top app bar
            TopAppBar(
                title = {
                    Text(
                        text = "Track Your Delivery",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
            
            Box(modifier = Modifier.fillMaxSize()) {
                // Map
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    // Driver marker
                    Marker(
                        state = rememberMarkerState(position = driverLocation),
                        title = "Driver: ${driverInfo.name}",
                        snippet = "ETA: ${driverInfo.estimatedArrival}"
                    )
                    
                    // Pickup marker
                    Marker(
                        state = rememberMarkerState(position = pickupLatLng),
                        title = "Pickup Location",
                        snippet = pickupLocation
                    )
                    
                    // Dropoff marker
                    Marker(
                        state = rememberMarkerState(position = dropoffLatLng),
                        title = "Delivery Location", 
                        snippet = dropoffLocation
                    )
                }
                
                // Driver info card at the bottom - full screen width
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(top = 16.dp), // Only top padding to separate from map
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp, 
                            top = 20.dp,
                            bottom = 32.dp // Extra bottom padding
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Status indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PulsingDot()
                            Text(
                                text = uiState.deliveryStatus.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        HorizontalDivider(
                            color = DividerDefaults.color.copy(alpha = 0.3f),
                            thickness = DividerDefaults.Thickness
                        )
                        
                        // Driver details
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Driver avatar
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = driverInfo.name.first().toString().uppercase(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = driverInfo.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "⭐",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = String.format(Locale.US, "%.1f", driverInfo.rating),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "• ${driverInfo.vehicleType}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                
                                Text(
                                    text = driverInfo.plateNumber,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // ETA
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "ETA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = driverInfo.estimatedArrival,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                
                                // Action buttons row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Message button
                                    IconButton(
                                        onClick = onMessageClick,
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    ) {
                                        Text(
                                            text = "💬",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    
                                    // Call button
                                    IconButton(
                                        onClick = { /* TODO: Call driver */ },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Call,
                                            contentDescription = "Call driver",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        HorizontalDivider(
                            color = DividerDefaults.color.copy(alpha = 0.3f),
                            thickness = DividerDefaults.Thickness
                        )
                        
                        // Trip details
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // From location
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            Color(0xFF4CAF50),
                                            CircleShape
                                        )
                                        .padding(top = 4.dp)
                                )
                                Column {
                                    Text(
                                        text = "From",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = pickupLocation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            
                            // To location
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            Color(0xFFF44336),
                                            CircleShape
                                        )
                                        .padding(top = 4.dp)
                                )
                                Column {
                                    Text(
                                        text = "To",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = dropoffLocation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        
                        HorizontalDivider(
                            color = DividerDefaults.color.copy(alpha = 0.3f),
                            thickness = DividerDefaults.Thickness
                        )
                        
                        // Total fare
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total fare",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "₵ ${String.format(Locale.US, "%.2f", driverInfo.totalPrice)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PulsingDot(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .size(12.dp)
            .scale(scale)
            .background(
                Color(0xFF4CAF50),
                CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
fun TrackingScreenPreview() {
    val mockDriverInfo = DriverInfo(
        name = "Kwame Asante",
        rating = 4.8f,
        vehicleType = "Toyota Camry",
        plateNumber = "GR-4587-20",
        phone = "+233-24-123-4567",
        totalPrice = 25.50,
        estimatedArrival = "5-8 mins"
    )
    
    TrackingScreen(
        driverInfo = mockDriverInfo,
        pickupLocation = "Accra Mall, Tetteh Quarshie",
        dropoffLocation = "University of Ghana, Legon",
        onBackClick = {}
    )
}
