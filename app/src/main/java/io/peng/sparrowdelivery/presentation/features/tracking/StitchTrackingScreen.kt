package io.peng.sparrowdelivery.presentation.features.tracking

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.components.stitch.*
import java.util.*

/**
 * Stitch-styled tracking screen following the design system
 * Features: Map with driver location, status tracking, driver details, action buttons
 */
@Composable
fun StitchTrackingScreen(
    driverInfo: DriverInfo,
    pickupLocation: String,
    dropoffLocation: String,
    onBackClick: () -> Unit,
    onCallDriverClick: () -> Unit = {},
    onMessageDriverClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Demo coordinates (Accra, Ghana)
    val driverLocation = LatLng(5.614818, -0.186964)
    val pickupLatLng = LatLng(5.614818, -0.196964)
    val dropoffLatLng = LatLng(5.624818, -0.176964)
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverLocation, 14f)
    }
    
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(stitchColors.background)
        ) {
            // Full screen map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Driver marker (pulsing green dot)
                Marker(
                    state = rememberMarkerState(position = driverLocation),
                    title = "Driver: ${driverInfo.name}",
                    snippet = "ETA: ${driverInfo.estimatedArrival}"
                )
                
                // Pickup marker
                Marker(
                    state = rememberMarkerState(position = pickupLatLng),
                    title = "Pickup",
                    snippet = pickupLocation
                )
                
                // Dropoff marker 
                Marker(
                    state = rememberMarkerState(position = dropoffLatLng),
                    title = "Delivery",
                    snippet = dropoffLocation
                )
            }
            
            // Stitch header with blur background
            StitchTrackingHeader(
                onBackClick = onBackClick,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            
            // Stitch-styled bottom overlay with driver info
            StitchTrackingBottomSheet(
                driverInfo = driverInfo,
                pickupLocation = pickupLocation,
                dropoffLocation = dropoffLocation,
                deliveryStatus = uiState.deliveryStatus,
                estimatedTime = "10:45 AM",
                onCallClick = onCallDriverClick,
                onMessageClick = onMessageDriverClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun StitchTrackingHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    // Translucent header with blur effect
    TranslucentCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        backgroundColor = stitchColors.background.copy(alpha = 0.85f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            StitchIconButton(
                onClick = onBackClick,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                variant = StitchIconButtonVariant.Secondary,
                contentDescription = "Back"
            )
            
            // Title
            StitchText(
                text = "Package Tracking",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = stitchColors.onBackground
            )
            
            // Spacer for balance
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
private fun StitchTrackingBottomSheet(
    driverInfo: DriverInfo,
    pickupLocation: String,
    dropoffLocation: String,
    deliveryStatus: DeliveryStatus,
    estimatedTime: String,
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    // Main bottom sheet card
    StitchCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        backgroundColor = stitchColors.surface,
        elevation = 16.dp,
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status and time header
            StitchDeliveryStatus(
                status = deliveryStatus,
                estimatedTime = estimatedTime
            )
            
            // Driver info section
            StitchDriverInfo(
                driverInfo = driverInfo,
                onCallClick = onCallClick,
                onMessageClick = onMessageClick
            )
            
            // Trip details
            StitchTripDetails(
                pickupLocation = pickupLocation,
                dropoffLocation = dropoffLocation
            )
            
            // Fare summary
            StitchFareSummary(
                totalFare = driverInfo.totalPrice
            )
        }
    }
}

@Composable
private fun StitchDeliveryStatus(
    status: DeliveryStatus,
    estimatedTime: String,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status with pulsing indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StitchPulsingDot()
            StitchHeading(
                text = "Delivery in progress", // status.displayName,
                level = 3,
                color = stitchColors.onSurface
            )
        }
        
        // Time indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = stitchColors.primary,
                modifier = Modifier.size(20.dp)
            )
            StitchText(
                text = estimatedTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = stitchColors.textSecondary
            )
        }
    }
}

@Composable
private fun StitchDriverInfo(
    driverInfo: DriverInfo,
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    // Border separator
    HorizontalDivider(
        color = stitchColors.outline.copy(alpha = 0.2f),
        thickness = 1.dp
    )
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Driver avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(stitchColors.primary),
            contentAlignment = Alignment.Center
        ) {
            StitchText(
                text = driverInfo.name.first().toString().uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = stitchColors.onPrimary
            )
        }
        
        // Driver details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StitchText(
                text = driverInfo.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = stitchColors.onSurface
            )
            StitchText(
                text = "Your delivery driver",
                style = MaterialTheme.typography.bodySmall,
                color = stitchColors.textSecondary
            )
        }
        
        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Call button (primary)
            StitchIconButton(
                onClick = onCallClick,
                icon = Icons.Default.Call,
                variant = StitchIconButtonVariant.Primary,
                contentDescription = "Call driver"
            )
            
            // Message button (secondary)
            StitchIconButton(
                onClick = onMessageClick,
                icon = Icons.Default.ChatBubble,
                variant = StitchIconButtonVariant.Secondary,
                contentDescription = "Message driver"
            )
        }
    }
    
    // Bottom border
    HorizontalDivider(
        color = stitchColors.outline.copy(alpha = 0.2f),
        thickness = 1.dp
    )
}

@Composable
private fun StitchTripDetails(
    pickupLocation: String,
    dropoffLocation: String,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // From location
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Green dot for pickup
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .background(stitchColors.accent, CircleShape)
            )
            Column {
                StitchText(
                    text = "From",
                    style = MaterialTheme.typography.labelSmall,
                    color = stitchColors.textSecondary
                )
                StitchText(
                    text = pickupLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stitchColors.onSurface
                )
            }
        }
        
        // To location
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Red dot for dropoff
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .background(stitchColors.primary, CircleShape)
            )
            Column {
                StitchText(
                    text = "To",
                    style = MaterialTheme.typography.labelSmall,
                    color = stitchColors.textSecondary
                )
                StitchText(
                    text = dropoffLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stitchColors.onSurface
                )
            }
        }
    }
    
    // Bottom divider
    HorizontalDivider(
        color = stitchColors.outline.copy(alpha = 0.2f),
        thickness = 1.dp
    )
}

@Composable
private fun StitchFareSummary(
    totalFare: Double,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StitchText(
            text = "Total fare",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = stitchColors.onSurface
        )
        
        StitchText(
            text = "₵ ${String.format(Locale.US, "%.2f", totalFare)}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = stitchColors.accent // Green for money
        )
    }
}

@Composable
private fun StitchPulsingDot(
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
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
            .background(stitchColors.accent, CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
private fun StitchTrackingScreenPreview() {
    val mockDriverInfo = DriverInfo(
        name = "Kwame Asante",
        rating = 4.8f,
        vehicleType = "Toyota Camry", 
        plateNumber = "GR-4587-20",
        phone = "+233-24-123-4567",
        totalPrice = 25.50,
        estimatedArrival = "5-8 mins"
    )
    
    StitchTrackingScreen(
        driverInfo = mockDriverInfo,
        pickupLocation = "Accra Mall, Tetteh Quarshie",
        dropoffLocation = "University of Ghana, Legon",
        onBackClick = {}
    )
}
