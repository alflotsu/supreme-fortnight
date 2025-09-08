package io.peng.sparrowdelivery.presentation.features.home

import android.content.Context
import android.location.Geocoder
import android.location.Address
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import io.peng.sparrowdelivery.data.services.RouteInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMapView(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    interactionMode: MapInteractionMode = MapInteractionMode.NONE,
    pickupCoordinate: LocationCoordinate? = null,
    dropoffCoordinate: LocationCoordinate? = null,
    currentRoute: List<LocationCoordinate> = emptyList(),
    availableRoutes: List<RouteInfo> = emptyList(),
    selectedRouteIndex: Int = 0,
    hasLocationPermission: Boolean = false,
    onLocationSelected: ((LocationCoordinate) -> Unit)? = null,
    onPOISelected: ((PointOfInterest) -> Unit)? = null,
    onCancelSelection: (() -> Unit)? = null,
    onRouteSelected: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedMarkerState by remember { mutableStateOf<MarkerState?>(null) }
    
    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasLocationPermission,
                zoomControlsEnabled = false,
                compassEnabled = true,
                rotationGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = true,
                zoomGesturesEnabled = true
            ),
            onMapClick = { latLng ->
                if (interactionMode != MapInteractionMode.NONE) {
                    handleMapClick(latLng, context, onLocationSelected)
                }
            },
            onPOIClick = { pointOfInterest ->
                if (interactionMode != MapInteractionMode.NONE) {
                    handlePOIClick(pointOfInterest, context, onPOISelected)
                }
            }
        ) {
            // Selection marker (temporary marker during selection)
            selectedMarkerState?.let { markerState ->
                Marker(
                    state = markerState,
                    icon = when (interactionMode) {
                        MapInteractionMode.SELECTING_PICKUP -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        MapInteractionMode.SELECTING_DROPOFF -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        else -> BitmapDescriptorFactory.defaultMarker()
                    }
                )
            }
            
            // Pickup marker
            pickupCoordinate?.let { coordinate ->
                Marker(
                    state = MarkerState(position = coordinate.toLatLng()),
                    title = "Pickup Location",
                    snippet = "Your package will be collected from here",
                    icon = createCustomMarkerIcon(MapMarkerType.PICKUP)
                )
            }
            
            // Dropoff marker
            dropoffCoordinate?.let { coordinate ->
                Marker(
                    state = MarkerState(position = coordinate.toLatLng()),
                    title = "Dropoff Location",
                    snippet = "Your package will be delivered here",
                    icon = createCustomMarkerIcon(MapMarkerType.DROPOFF)
                )
            }
            
            // Multiple route polylines (if available from HERE routing)
            if (availableRoutes.isNotEmpty() && interactionMode == MapInteractionMode.NONE) {
                availableRoutes.forEachIndexed { index, route ->
                    val points = route.polylinePoints.map { it.toLatLng() }
                    val isSelected = index == selectedRouteIndex
                    
                    Polyline(
                        points = points,
                        color = if (isSelected) {
                            Color(0xFF2196F3) // Blue for selected route
                        } else {
                            Color(0xFF9E9E9E) // Gray for alternative routes
                        },
                        width = if (isSelected) 8f else 5f,
                        pattern = if (isSelected) null else listOf(
                            Dash(20f), Gap(10f)
                        ), // Dashed for alternatives
                        clickable = true,
                        onClick = {
                            onRouteSelected?.invoke(index)
                        }
                    )
                }
                
                // Fit camera to show selected route
                LaunchedEffect(selectedRouteIndex, availableRoutes) {
                    if (availableRoutes.isNotEmpty() && selectedRouteIndex < availableRoutes.size) {
                        val selectedRoute = availableRoutes[selectedRouteIndex]
                        val points = selectedRoute.polylinePoints.map { it.toLatLng() }
                        
                        if (points.size >= 2) {
                            val boundsBuilder = LatLngBounds.builder()
                            points.forEach { boundsBuilder.include(it) }
                            
                            // Add pickup and dropoff coordinates to bounds for better framing
                            pickupCoordinate?.let { boundsBuilder.include(it.toLatLng()) }
                            dropoffCoordinate?.let { boundsBuilder.include(it.toLatLng()) }
                            
                            val bounds = boundsBuilder.build()
                            val padding = 120 // pixels - extra padding for better view
                            
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
                                durationMs = 1000
                            )
                        }
                    }
                }
            }
            // Fallback: Legacy route polyline (for backward compatibility)
            else if (currentRoute.isNotEmpty() && interactionMode == MapInteractionMode.NONE) {
                val points = currentRoute.map { it.toLatLng() }
                Polyline(
                    points = points,
                    color = Color(0xFF2196F3), // Blue color
                    width = 8f,
                    pattern = null
                )
                
                // Fit camera to show entire route
                LaunchedEffect(currentRoute) {
                    if (points.size >= 2) {
                        val boundsBuilder = LatLngBounds.builder()
                        points.forEach { boundsBuilder.include(it) }
                        val bounds = boundsBuilder.build()
                        val padding = 100 // pixels
                        
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
                            durationMs = 1000
                        )
                    }
                }
            }
        }
        
        // Map interaction overlay
        if (interactionMode != MapInteractionMode.NONE) {
            MapInteractionOverlay(
                mode = interactionMode,
                onCancel = {
                    selectedMarkerState = null
                    onCancelSelection?.invoke()
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun MapInteractionOverlay(
    mode: MapInteractionMode,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .zIndex(1000f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3).copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = when (mode) {
                        MapInteractionMode.SELECTING_PICKUP -> "Select Pickup Location"
                        MapInteractionMode.SELECTING_DROPOFF -> "Select Dropoff Location"
                        else -> "Select Location"
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cancel selection",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Text(
                text = "Tap anywhere on the map or a point of interest",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

private fun handleMapClick(
    latLng: LatLng,
    context: Context,
    onLocationSelected: ((LocationCoordinate) -> Unit)?
) {
    val coordinate = LocationCoordinate.fromLatLng(latLng)
    
    // Perform reverse geocoding in background
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            ) { addresses ->
                // Handle the result on main thread
                onLocationSelected?.invoke(coordinate)
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            onLocationSelected?.invoke(coordinate)
        }
    } catch (e: Exception) {
        // Fallback without geocoding
        onLocationSelected?.invoke(coordinate)
    }
}

private fun handlePOIClick(
    pointOfInterest: com.google.android.gms.maps.model.PointOfInterest,
    context: Context,
    onPOISelected: ((PointOfInterest) -> Unit)?
) {
    val coordinate = LocationCoordinate.fromLatLng(pointOfInterest.latLng)
    val poi = PointOfInterest(
        placeId = pointOfInterest.placeId,
        name = pointOfInterest.name,
        address = "", // Will be filled by Places API
        coordinate = coordinate,
        types = emptyList() // Will be filled by Places API
    )
    
    onPOISelected?.invoke(poi)
}

private fun createCustomMarkerIcon(type: MapMarkerType): BitmapDescriptor {
    return when (type) {
        MapMarkerType.PICKUP -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        MapMarkerType.DROPOFF -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        MapMarkerType.INTERMEDIATE_STOP -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        MapMarkerType.CURRENT_LOCATION -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        MapMarkerType.SELECTED_LOCATION -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
    }
}

// Helper function to format address from geocoding results
fun formatAddress(addresses: List<Address>, coordinate: LocationCoordinate): String {
    if (addresses.isEmpty()) {
        return "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
    }
    
    val address = addresses[0]
    val addressComponents = mutableListOf<String>()
    
    // Check if we have a landmark or POI name
    address.featureName?.let { featureName ->
        val locality = address.locality ?: ""
        val subLocality = address.subLocality ?: ""
        val thoroughfare = address.thoroughfare ?: ""
        
        // Check if feature name is a landmark (not just a street address)
        val isLandmark = !featureName.contains(thoroughfare) &&
                featureName != locality &&
                featureName != subLocality &&
                !featureName[0].isDigit() &&
                featureName.length > 3
        
        if (isLandmark) {
            addressComponents.add(featureName)
            // Add context location
            if (subLocality.isNotEmpty() && subLocality != featureName) {
                addressComponents.add(subLocality)
            } else if (locality.isNotEmpty() && locality != featureName) {
                addressComponents.add(locality)
            }
            return addressComponents.joinToString(", ")
        }
    }
    
    // Regular address formatting
    address.subThoroughfare?.let { number ->
        address.thoroughfare?.let { street ->
            addressComponents.add("$number $street")
        }
    } ?: address.thoroughfare?.let { street ->
        addressComponents.add(street)
    }
    
    address.subLocality?.let { addressComponents.add(it) }
    address.locality?.let { addressComponents.add(it) }
    
    if (addressComponents.isEmpty()) {
        address.featureName?.let { addressComponents.add(it) }
    }
    
    return if (addressComponents.isEmpty()) {
        "Pinned Location (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
    } else {
        // Limit to first 2 components for readability
        val finalComponents = addressComponents.take(2)
        val baseAddress = finalComponents.joinToString(", ")
        
        // Add coordinates for less precise addresses
        if (address.subThoroughfare != null && address.thoroughfare != null) {
            baseAddress
        } else {
            "$baseAddress (${String.format("%.4f, %.4f", coordinate.latitude, coordinate.longitude)})"
        }
    }
}
