package io.peng.sparrowdelivery.presentation.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import io.peng.sparrowdelivery.data.services.RouteInfo
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePreviewDialog(
    routes: List<RouteInfo>,
    selectedRouteIndex: Int,
    onRouteSelected: (Int) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (routes.isEmpty()) return
    
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        StitchTheme {
            val stitchColors = LocalStitchColorScheme.current
            
            Surface(
                modifier = modifier.fillMaxSize(),
                color = stitchColors.background
            ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with close button
                TopAppBar(
                    title = {
                        StitchText(
                            text = "Route Preview",
                            color = stitchColors.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close route preview",
                                tint = stitchColors.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = stitchColors.background
                    )
                )
                
                // Map view
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    RoutePreviewMap(
                        routes = routes,
                        selectedRouteIndex = selectedRouteIndex,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Route alternatives list
                if (routes.size > 1) {
                    StitchText(
                        text = "Choose your route",
                        color = stitchColors.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp,horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(routes) { index, route ->
                        RouteCard(
                            route = route,
                            isSelected = index == selectedRouteIndex,
                            routeNumber = index + 1,
                            onClick = { onRouteSelected(index) }
                        )
                    }
                }
                
//                Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp)
                        .padding(16.dp).padding(bottom = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)

                ) {
                    StitchTextButton(
                        text = "Cancel",
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        variant = StitchButtonVariant.Outline
                    )
                    
                    StitchTextButton(
                        text = "Confirm Route",
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        variant = StitchButtonVariant.Primary
                    )
                }
            }
        }
        }
    }
}

@Composable
fun RouteCard(
    route: RouteInfo,
    isSelected: Boolean,
    routeNumber: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = modifier.fillMaxWidth(),
        variant = if (isSelected) StitchCardVariant.Elevated else StitchCardVariant.Default
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Route number badge
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) {
                    stitchColors.primary
                } else {
                    stitchColors.outline
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    StitchText(
                        text = routeNumber.toString(),
                        color = if (isSelected) {
                            stitchColors.onPrimary
                        } else {
                            stitchColors.textSecondary
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Main route info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Duration
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StitchText(
                            text = "⏱",
                            color = stitchColors.textSecondary
                        )
                        StitchText(
                            text = formatDuration(route.duration),
                            color = stitchColors.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Distance
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StitchText(
                            text = "📏",
                            color = stitchColors.textSecondary
                        )
                        StitchText(
                            text = formatDistance(route.distance),
                            color = stitchColors.onSurface
                        )
                    }
                }
                
                // Route description/conditions
                if (route.hasTraffic) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StitchText(
                            text = "🚗",
                            color = stitchColors.warning // Orange for traffic
                        )
                        StitchText(
                            text = "Current traffic conditions included",
                            color = stitchColors.textSecondary
                        )
                    }
                }
                
                // Route type indicator
                val routeType = when {
                    routeNumber == 1 -> "Fastest route"
                    else -> "Alternative route"
                }
                
                if (routeNumber <= 3) { // Only show route type for first few routes
                    Spacer(modifier = Modifier.height(2.dp))
                    StitchText(
                        text = routeType,
                        color = if (isSelected) {
                            stitchColors.primary
                        } else {
                            stitchColors.textSecondary
                        },
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
            
            // Selection indicator
            if (isSelected) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = stitchColors.primary
                ) {}
            }
        }
    }
}

@Composable
fun RoutePreviewMap(
    routes: List<RouteInfo>,
    selectedRouteIndex: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Calculate bounds for all route points
    val allPoints = routes.flatMap { it.polylinePoints.map { coord -> 
        LatLng(coord.latitude, coord.longitude) 
    }}
    
    val cameraPositionState = rememberCameraPositionState()
    
    // Update camera to show all routes when routes change
    LaunchedEffect(routes) {
        if (allPoints.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            allPoints.forEach { point ->
                boundsBuilder.include(point)
            }
            val bounds = boundsBuilder.build()
            
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(bounds, 100),
                durationMs = 1000
            )
        }
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            myLocationButtonEnabled = false
        )
    ) {
        routes.forEachIndexed { index, route ->
            val isSelected = index == selectedRouteIndex
            val polylinePoints = route.polylinePoints.map { 
                LatLng(it.latitude, it.longitude) 
            }
            
            if (polylinePoints.isNotEmpty()) {
                Polyline(
                    points = polylinePoints,
                    color = if (isSelected) {
                        Color(0xFF1976D2) // Blue for selected route
                    } else {
                        Color(0xFF757575).copy(alpha = 0.6f) // Gray for alternative routes
                    },
                    width = if (isSelected) 8f else 5f
                )
                
                // Start marker
                Marker(
                    state = MarkerState(position = polylinePoints.first()),
                    title = "Pickup Location",
                    alpha = if (isSelected) 1f else 0.7f
                )
                
                // End marker
                Marker(
                    state = MarkerState(position = polylinePoints.last()),
                    title = "Drop-off Location",
                    alpha = if (isSelected) 1f else 0.7f
                )
            }
        }
    }
}

// Helper functions
private fun formatDuration(durationSeconds: Int): String {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "$minutes min"
        else -> "< 1 min"
    }
}

private fun formatDistance(distanceMeters: Int): String {
    val kilometers = distanceMeters / 1000.0
    return when {
        kilometers >= 1.0 -> "${"%.1f".format(kilometers)} km"
        else -> "$distanceMeters m"
    }
}
