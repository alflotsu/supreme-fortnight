package io.peng.sparrowdelivery.presentation.features.home

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.presentation.features.profile.ProfileDrawer
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.components.ContextualHapticFeedback
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onMoreOptionsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigateToTracking: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToComponentDemo: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Profile drawer state
    var showProfileDrawer by remember { mutableStateOf(false) }
    
    // Location permission handling
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    
    // Bottom Sheet State with proper constraints
    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = true // Prevent completely hiding sheet
        )
    )
    
    // Handle bottom sheet state based on booking state AND side effects
    LaunchedEffect(uiState.bookingState) {
        when (uiState.bookingState) {
            is BookingState.FindingDriver -> {
                // Collapse to peek during driver search
                bottomSheetState.bottomSheetState.partialExpand()
            }
            is BookingState.DriverFound -> {
                // Collapse to peek when driver is found
                bottomSheetState.bottomSheetState.partialExpand()
            }
            is BookingState.BookingConfirmed -> {
                // Collapse to peek when booking is confirmed
                bottomSheetState.bottomSheetState.partialExpand()
            }
            else -> {
                // Expand bottom sheet for location entry and route preview
                bottomSheetState.bottomSheetState.expand()
            }
        }
    }
    
    // Handle side effect triggers for bottom sheet
    LaunchedEffect(uiState.shouldExpandBottomSheet) {
        if (uiState.shouldExpandBottomSheet) {
            println("🔄 Side effect: Expanding bottom sheet")
            bottomSheetState.bottomSheetState.expand()
            viewModel.onBottomSheetExpanded() // Reset trigger
        }
    }
    
    LaunchedEffect(uiState.shouldCollapseBottomSheet) {
        if (uiState.shouldCollapseBottomSheet) {
            println("🔄 Side effect: Collapsing bottom sheet")
            bottomSheetState.bottomSheetState.partialExpand() // Collapse to peek instead of hiding
            viewModel.onBottomSheetCollapsed() // Reset trigger
        }
    }
    
    // Initialize location client when composable is first created
    LaunchedEffect(context) {
        viewModel.initializeLocationClient(context)
    }
    
    // Handle permission state changes
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.onLocationPermissionGranted()
        }
    }
    
    // Default location (Accra, Ghana) if no location is available - matching SwiftTouches
    val defaultLocation = LatLng(5.61, -0.14)
    val currentLocation = uiState.currentLocation ?: defaultLocation
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }
    
    // Update camera when location changes
    LaunchedEffect(uiState.currentLocation) {
        uiState.currentLocation?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 15f),
                durationMs = 1000
            )
        }
    }
    
    ShadcnTheme {
        // Add contextual haptic feedback for map and delivery states
        ContextualHapticFeedback(
            isSuccess = uiState.isDriverFound,
            isError = uiState.errorMessage != null,
            isLoading = uiState.isFindingDrivers,
            intensity = 0.7f
        )
        
        BottomSheetScaffold(
            scaffoldState = bottomSheetState,
            sheetContainerColor = Color.Transparent,
            containerColor = Color.Transparent,
            sheetDragHandle = null, // Disable default handle - we have custom one
            sheetContent = {
                // Ultra-thin gradient sheet wrapper
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    Color.White.copy(alpha = 0.85f),
                                    Color.White.copy(alpha = 0.75f)
                                )
                            )
                        )
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        // Elegant gradient handle
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Gray.copy(alpha = 0.3f),
                                            Color.Gray.copy(alpha = 0.5f),
                                            Color.Gray.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Original sheet content
                        EnhancedDeliveryFormBottomSheet(
                    deliveryForm = uiState.enhancedDeliveryForm,
                    legacyForm = uiState.deliveryForm, // For backward compatibility
                    availableRoutes = uiState.availableRoutes,
                    selectedRouteIndex = uiState.selectedRouteIndex,
                    onPickupLocationChange = viewModel::updatePickupLocation,
                    onDestinationChange = viewModel::updateDestination,
                    onNumberOfStopsChange = viewModel::updateNumberOfStops,
                    onIntermediateStopChange = viewModel::updateIntermediateStop,
                    onFindDriverClick = viewModel::findDriverAndPricing,
                    onMoreOptionsClick = onMoreOptionsClick,
                    onTrackDeliveryClick = onNavigateToTracking,
                    onMapPinSelectPickup = { viewModel.setMapInteractionMode(MapInteractionMode.SELECTING_PICKUP) },
                    onMapPinSelectDropoff = { viewModel.setMapInteractionMode(MapInteractionMode.SELECTING_DROPOFF) },
                    onPickupPlaceSelected = { placeDetails ->
                        viewModel.updatePickupLocationFromPlace(placeDetails)
                    },
                    onDropoffPlaceSelected = { placeDetails ->
                        viewModel.updateDropoffLocationFromPlace(placeDetails)
                    },
                    onRoutePreviewClick = viewModel::showRoutePreviewDialog,
                    onScheduleTypeChange = viewModel::updateDeliveryScheduleType,
                    onScheduleClick = viewModel::showDatePickerDialog,
                    onClearFields = viewModel::clearDeliveryFields,
                    onRouteSelected = viewModel::selectRoute,
                    onBackFromRoutePreview = viewModel::clearRoutePreview
                )
                    }
                }
            },
            sheetPeekHeight = 84.dp, // Minimum visible height - prevents dragging out of view
            modifier = modifier
        ) { _ ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Don't apply innerPadding - let map fill entire screen
            ) {
                // Enhanced Interactive Map with HERE routing
                InteractiveMapView(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    interactionMode = uiState.mapInteractionMode,
                    pickupCoordinate = uiState.enhancedDeliveryForm.pickupCoordinate,
                    dropoffCoordinate = uiState.enhancedDeliveryForm.dropoffCoordinate,
                    currentRoute = uiState.currentRoute,
                    availableRoutes = uiState.availableRoutes,
                    selectedRouteIndex = uiState.selectedRouteIndex,
                    hasLocationPermission = uiState.hasLocationPermission,
                    onLocationSelected = { coordinate ->
                        viewModel.handleLocationSelection(coordinate)
                    },
                    onPOISelected = { poi ->
                        viewModel.handlePOISelection(poi)
                    },
                    onCancelSelection = {
                        viewModel.cancelMapSelection()
                    },
                    onRouteSelected = { routeIndex ->
                        viewModel.selectRoute(routeIndex)
                    }
                )

                // Permission request UI
                if (!locationPermissionState.status.isGranted) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (locationPermissionState.status.shouldShowRationale) {
                                    "Location permission is needed for delivery tracking"
                                } else {
                                    "SparrowDelivery needs location access to provide delivery services"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    locationPermissionState.launchPermissionRequest()
                                }
                            ) {
                                Text("Grant Permission")
                            }
                        }
                    }
                }

                // Loading indicator
                if (uiState.isLoadingLocation) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Getting your location...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Error message
                uiState.errorMessage?.let { errorMessage ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(
                                onClick = { viewModel.clearErrorMessage() }
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                }

                // Profile floating action button (top-left)
                SmallFloatingActionButton(
                    onClick = { showProfileDrawer = true },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .padding(top = 32.dp),
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Floating action button for refresh location
                if (uiState.hasLocationPermission) {
                    FloatingActionButton(
                        onClick = { viewModel.refreshLocation() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Text("📍")
                    }
                }
                
                // Component Demo FAB (top-right corner for easy access)
                SmallFloatingActionButton(
                    onClick = onNavigateToComponentDemo,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .padding(top = 32.dp),
                    containerColor = ShadcnColors.Primary,
                    contentColor = ShadcnColors.PrimaryForeground
                ) {
                    Text(
                        "UI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Profile Drawer
            if (showProfileDrawer) {
                ProfileDrawer(
                    onDismiss = { showProfileDrawer = false },
                    onViewProfileClick = {
                        showProfileDrawer = false
                        onProfileClick()
                    }
                )
            }
            
            // Route preview is now integrated into the bottom sheet
            
            // iOS-style Wheel Date Picker Dialog
            if (uiState.showDatePickerDialog) {
                ShadcnWheelDateTimePickerDialog(
                    onDateTimeSelected = { dateMillis ->
                        viewModel.updateScheduledDateTime(dateMillis)
                    },
                    onDismiss = { viewModel.hideDatePickerDialog() }
                )
            }
            
            // Finding Drivers Overlay - React directly to BookingState
            when (val bookingState = uiState.bookingState) {
                is BookingState.FindingDriver -> {
                    FindingDriversOverlay(
                        pickupLocation = bookingState.pickupLocation,
                        dropoffLocation = bookingState.dropoffLocation,
                        estimatedPrice = bookingState.estimatedPrice,
                        onCancel = viewModel::cancelDriverBooking
                    )
                }
                is BookingState.DriverFound -> {
                    DriverFoundDialog(
                        driverInfo = bookingState.driver,
                        pickupLocation = bookingState.pickupLocation,
                        dropoffLocation = bookingState.dropoffLocation,
                        onContinue = {
                            viewModel.continueToTracking()
                            onNavigateToTracking()
                        },
                        onCancel = viewModel::cancelDriverBooking
                    )
                }
                else -> { /* No overlays */ }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(true) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    
    val datePickerState = rememberDatePickerState(
        // Set initial date to tomorrow to prevent past dates
        initialSelectedDateMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
    )
    
    val timePickerState = rememberTimePickerState(
        initialHour = 9, // Default to 9 AM
        initialMinute = 0,
        is24Hour = false // Use 12-hour format
    )

    when {
        showDatePicker -> {
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedDateMillis = datePickerState.selectedDateMillis
                            showDatePicker = false
                            showTimePicker = true
                        },
                        enabled = datePickerState.selectedDateMillis != null
                    ) {
                        Text("Next")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                },
                modifier = modifier
            ) {
                DatePicker(
                    state = datePickerState,
                    title = {
                        Text(
                            text = "Select delivery date",
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                    headline = {
                        Text(
                            text = datePickerState.selectedDateMillis?.let { millis ->
                                SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
                            } ?: "Select a date",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                )
            }
        }
        
        showTimePicker -> {
            TimePickerDialog(
                onDismiss = {
                    showTimePicker = false
                    showDatePicker = true
                },
                onConfirm = {
                    selectedDateMillis?.let { dateMillis ->
                        // Combine date and time
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = dateMillis
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onDateSelected(calendar.timeInMillis)
                    }
                },
                selectedDate = selectedDateMillis,
                timePickerState = timePickerState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    selectedDate: Long?,
    timePickerState: TimePickerState,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Back")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Column {
                Text("Select delivery time", color = ShadcnTheme.colors.foreground)
                selectedDate?.let { millis ->
                    Text(
                        text = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date(millis)),
                        color = ShadcnTheme.colors.mutedForeground
                    )
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(16.dp)
                )
                
                // Show selected time preview
                val timeFormat = if (timePickerState.is24hour) {
                    SimpleDateFormat("HH:mm", Locale.getDefault())
                } else {
                    SimpleDateFormat("h:mm a", Locale.getDefault())
                }
                
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    set(Calendar.MINUTE, timePickerState.minute)
                }
                
                ShadcnCard(
                    variant = ShadcnCardVariant.Outlined,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    ShadcnText(
                        text = "Delivery at ${timeFormat.format(calendar.time)}",
                        modifier = Modifier.padding(12.dp),
                        style = ShadcnTextStyle.P
                    )
                }
            }
        },
        modifier = modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun FindingDriversOverlay(
    pickupLocation: String,
    dropoffLocation: String,
    estimatedPrice: Double,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Ping animation
    val infiniteTransition = rememberInfiniteTransition(label = "ping")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        ShadcnCard(
            variant = ShadcnCardVariant.Elevated,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Animated ping circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    // Outer ping circle
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(scale)
                            .background(
                                ShadcnTheme.colors.primary.copy(alpha = alpha * 0.3f),
                                CircleShape
                            )
                    )
                    
                    // Inner ping circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(scale * 0.8f)
                            .background(
                                ShadcnTheme.colors.primary.copy(alpha = alpha * 0.6f),
                                CircleShape
                            )
                    )
                    
                    // Core circle with car emoji
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                ShadcnTheme.colors.primary,
                                CircleShape
                            )
                    ) {
                        Text(
                            text = "🚗",
                            color = ShadcnTheme.colors.primaryForeground
                        )
                    }
                }
                
                // Text content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShadcnText(
                        text = "Finding a driver",
                        style = ShadcnTextStyle.H3
                    )
                    
                    ShadcnText(
                        text = "Please standby...",
                        style = ShadcnTextStyle.Muted
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Trip details
                    ShadcnCompactCard(
                        variant = ShadcnCardVariant.Outlined
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // From location
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            ShadcnTheme.colors.success,
                                            CircleShape
                                        )
                                        .padding(top = 4.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ShadcnText(
                                        text = "From",
                                        style = ShadcnTextStyle.Small,

                                    )
                                    ShadcnText(
                                        text = pickupLocation.ifEmpty { "Pickup location" },
                                        style = ShadcnTextStyle.P
                                    )
                                }
                            }
                            
                            // To location
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            ShadcnTheme.colors.destructive,
                                            CircleShape
                                        )
                                        .padding(top = 4.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ShadcnText(
                                        text = "To",
                                        style = ShadcnTextStyle.Small,

                                    )
                                    ShadcnText(
                                        text = dropoffLocation.ifEmpty { "Dropoff location" },
                                        style = ShadcnTextStyle.P,
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = ShadcnTheme.colors.border
                            )

                            // Estimated price
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ShadcnText(
                                    text = "Estimated fare",
                                    style = ShadcnTextStyle.Muted
                                )
                                ShadcnText(
                                    text = "₵ ${String.format(Locale.US, "%.2f", estimatedPrice)}",
                                    style = ShadcnTextStyle.H4,
                                    color = ShadcnTheme.colors.success
                                )
                            }
                        }
                    }
                    
                    // Loading indicator with dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        repeat(3) { index ->
                            val dotScale by infiniteTransition.animateFloat(
                                initialValue = 0.5f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = 600,
                                        delayMillis = index * 200
                                    ),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "dot$index"
                            )
                            
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .scale(dotScale)
                                    .background(
                                        ShadcnTheme.colors.primary,
                                        CircleShape
                                    )
                            )
                        }
                    }
                    
                    // Cancel button
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ShadcnTextButton(
                        text = "Cancel",
                        onClick = onCancel,
                        variant = ShadcnButtonVariant.Outline,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DriverFoundDialog(
    driverInfo: DriverInfo,
    pickupLocation: String,
    dropoffLocation: String,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 16.dp
            ),
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Success icon and title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "✅",
                        style = MaterialTheme.typography.displayMedium
                    )
                    
                    Text(
                        text = "Driver Found!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "Your driver is on the way",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Driver info card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Driver details
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Driver avatar placeholder
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
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = driverInfo.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = "• ${driverInfo.vehicleType}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                                
                                Text(
                                    text = driverInfo.plateNumber,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Arrives in",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = driverInfo.estimatedArrival,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        
                        HorizontalDivider(
                            color = DividerDefaults.color.copy(alpha = 0.3f),
                            thickness = DividerDefaults.Thickness
                        )
                        
                        // Trip summary
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // From location
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = pickupLocation.ifEmpty { "Pickup location" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            // To location
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = dropoffLocation.ifEmpty { "Dropoff location" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        HorizontalDivider(
                            color = DividerDefaults.color.copy(alpha = 0.3f),
                            thickness = DividerDefaults.Thickness
                        )
                        
                        // Total price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total fare",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                
                // Action buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text(
                            text = "Continue to Tracking",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cancel Booking",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
