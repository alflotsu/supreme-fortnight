package io.peng.sparrowdelivery.presentation.features.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.data.services.PlaceDetails
import io.peng.sparrowdelivery.ui.components.SlidingToggle
import io.peng.sparrowdelivery.ui.components.SlidingToggleOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDeliveryFormBottomSheet(
    deliveryForm: EnhancedDeliveryFormState,
    legacyForm: DeliveryFormState, // For backward compatibility
    availableRoutes: List<io.peng.sparrowdelivery.data.services.RouteInfo> = emptyList(),
    selectedRouteIndex: Int = 0,
    onPickupLocationChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onNumberOfStopsChange: (Int) -> Unit,
    onIntermediateStopChange: (Int, String) -> Unit,
    onFindDriverClick: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    onTrackDeliveryClick: () -> Unit = {},
    onMapPinSelectPickup: () -> Unit,
    onMapPinSelectDropoff: () -> Unit,
    onPickupPlaceSelected: (PlaceDetails) -> Unit = {},
    onDropoffPlaceSelected: (PlaceDetails) -> Unit = {},
    onRoutePreviewClick: () -> Unit = {},
    onScheduleTypeChange: (DeliveryScheduleType) -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onClearFields: () -> Unit = {},
    onRouteSelected: (Int) -> Unit = {},
    onBackFromRoutePreview: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Determine sheet mode based on state
    val hasValidLocations = deliveryForm.pickupLocation.isNotBlank() && deliveryForm.dropoffLocation.isNotBlank()
    val hasRouteData = availableRoutes.isNotEmpty()
    val shouldShowRoutePreview = hasValidLocations && hasRouteData
    
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        // Fast concurrent transition between different sheet content modes
        AnimatedContent(
            targetState = shouldShowRoutePreview,
            transitionSpec = {
                // Concurrent animations with faster timing
                (fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                slideInVertically(
                    animationSpec = tween(200, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 4 }
                )) togetherWith
                (fadeOut(animationSpec = tween(150, easing = LinearOutSlowInEasing)) +
                slideOutVertically(
                    animationSpec = tween(150, easing = LinearOutSlowInEasing),
                    targetOffsetY = { -it / 4 }
                ))
            },
            label = "sheet_content_transition"
        ) { showRoutePreview ->
            if (showRoutePreview) {
                // Route Preview Mode
                RoutePreviewBottomSheetContent(
                    routes = availableRoutes,
                    selectedRouteIndex = selectedRouteIndex,
                    pickupLocation = deliveryForm.pickupLocation,
                    dropoffLocation = deliveryForm.dropoffLocation,
                    onRouteSelected = onRouteSelected,
                    onBackClick = onBackFromRoutePreview,
                    onConfirmRoute = onFindDriverClick,
                    modifier = modifier
                )
            } else {
                // Standard Form Mode
                StandardFormBottomSheetContent(
                    deliveryForm = deliveryForm,
                    legacyForm = legacyForm,
                    onPickupLocationChange = onPickupLocationChange,
                    onDestinationChange = onDestinationChange,
                    onNumberOfStopsChange = onNumberOfStopsChange,
                    onIntermediateStopChange = onIntermediateStopChange,
                    onFindDriverClick = onFindDriverClick,
                    onMoreOptionsClick = onMoreOptionsClick,
                    onTrackDeliveryClick = onTrackDeliveryClick,
                    onMapPinSelectPickup = onMapPinSelectPickup,
                    onMapPinSelectDropoff = onMapPinSelectDropoff,
                    onPickupPlaceSelected = onPickupPlaceSelected,
                    onDropoffPlaceSelected = onDropoffPlaceSelected,
                    onRoutePreviewClick = onRoutePreviewClick,
                    onScheduleTypeChange = onScheduleTypeChange,
                    onScheduleClick = onScheduleClick,
                    onClearFields = onClearFields,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun StandardFormBottomSheetContent(
    deliveryForm: EnhancedDeliveryFormState,
    legacyForm: DeliveryFormState,
    onPickupLocationChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onNumberOfStopsChange: (Int) -> Unit,
    onIntermediateStopChange: (Int, String) -> Unit,
    onFindDriverClick: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    onTrackDeliveryClick: () -> Unit,
    onMapPinSelectPickup: () -> Unit,
    onMapPinSelectDropoff: () -> Unit,
    onPickupPlaceSelected: (PlaceDetails) -> Unit,
    onDropoffPlaceSelected: (PlaceDetails) -> Unit,
    onRoutePreviewClick: () -> Unit,
    onScheduleTypeChange: (DeliveryScheduleType) -> Unit,
    onScheduleClick: () -> Unit,
    onClearFields: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
            .verticalScroll(rememberScrollState())
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
                )
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            // Header with delivery time toggle (similar to SwiftTouches)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StitchHeading(
                    text = "Where to?",
                    level = 4
                )
                
                // Delivery time sliding toggle (Now/Schedule)
                SlidingToggle(
                    options = DeliveryScheduleType.entries.map { scheduleType ->
                        SlidingToggleOption(
                            value = scheduleType,
                            label = scheduleType.displayName,
                            icon = when (scheduleType.icon) {
                                "flash_on" -> Icons.Default.PlayArrow
                                "schedule" -> Icons.Default.DateRange
                                else -> Icons.Default.DateRange
                            }
                        )
                    },
                    selectedOption = deliveryForm.deliveryScheduleType,
                    onOptionSelected = { scheduleType ->
                        onScheduleTypeChange(scheduleType)
                        // If user selects "Schedule", show the date picker
                        if (scheduleType == DeliveryScheduleType.SCHEDULED) {
                            onScheduleClick()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.85f),
                    height = 36.dp
                )
            }
            
            // Show scheduled date/time with fast concurrent animation
            AnimatedVisibility(
                visible = deliveryForm.deliveryScheduleType == DeliveryScheduleType.SCHEDULED && deliveryForm.scheduledDateTime != null,
                enter = fadeIn(animationSpec = tween(150)) + expandVertically(
                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                ),
                exit = fadeOut(animationSpec = tween(100)) + shrinkVertically(
                    animationSpec = tween(150, easing = LinearOutSlowInEasing)
                )
            ) {
                StitchCard(
                    variant = StitchCardVariant.Outlined,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = stitchColors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            StitchText(
                                text = "Scheduled Delivery",
                                style = StitchTextStyle.Muted
                            )
                            val formatter = java.text.SimpleDateFormat("EEEE, MMM dd, yyyy 'at' h:mm a", java.util.Locale.getDefault())
                            StitchText(
                                text = deliveryForm.scheduledDateTime?.let { timestamp ->
                                    formatter.format(java.util.Date(timestamp))
                                } ?: "No date selected",
                                style = StitchTextStyle.P
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { onScheduleClick() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change time",
                                tint = stitchColors.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            
            // Location inputs with map pin buttons (similar to SwiftTouches)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pickup location with GPS icon
                LocationInputRow(
                    icon = io.peng.sparrowdelivery.ui.icons.SparrowIcons.Delivery.GPS, // GPS icon for pickup location
                    iconColor = Color(0xFF2563eb), // Blue-600
                    value = deliveryForm.pickupLocation,
                    onValueChange = onPickupLocationChange,
                    placeholder = "Pickup location",
                    onMapPinClick = onMapPinSelectPickup,
                    onPlaceSelected = onPickupPlaceSelected,
                    isPickupField = true
                )
                
                // Connecting line dots (visual connection)
//                Row(
//                    modifier = Modifier.padding(start = 26.dp)
//                ) {
//                    repeat(30) {
//                        Box(
//                            modifier = Modifier
//                                .size(3.dp)
//                                .padding(1.dp)
//                        ) {
//                            Surface(
//                                modifier = Modifier.fillMaxSize(),
//                                shape = androidx.compose.foundation.shape.CircleShape,
//                                color = stitchColors.outline
//                            ) {}
//                        }
//                    }
//                }
                
                // Dropoff location with navigation arrow
                LocationInputRow(
                    icon = io.peng.sparrowdelivery.ui.icons.SparrowIcons.Delivery.Navigation, // Navigation arrow for destination
                    iconColor = Color(0xFF059669), // Emerald-600
                    value = deliveryForm.dropoffLocation,
                    onValueChange = onDestinationChange,
                    placeholder = "Destination",
                    onMapPinClick = onMapPinSelectDropoff,
                    onPlaceSelected = onDropoffPlaceSelected,
                    isPickupField = false
                )
            }
            
            // Route preview section with fast concurrent animation
            AnimatedVisibility(
                visible = deliveryForm.showingRoutePreview && deliveryForm.canShowRoutePreview,
                enter = fadeIn(animationSpec = tween(200)) + expandVertically(
                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                ),
                exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(
                    animationSpec = tween(200, easing = LinearOutSlowInEasing)
                )
            ) {
                RoutePreviewCard(
                    isLoading = deliveryForm.isLoadingRoute,
                    error = deliveryForm.routePreviewError,
                    estimatedPrice = deliveryForm.estimatedPrice,
                    onClick = onRoutePreviewClick
                )
            }
            
            // Conditional: Quick actions when fields are empty, Clear button when route is found
            val hasValidLocations = deliveryForm.pickupLocation.isNotBlank() && deliveryForm.dropoffLocation.isNotBlank()
            val hasRoutePreview = deliveryForm.showingRoutePreview && deliveryForm.canShowRoutePreview
            
            // Fast concurrent transition between clear button and quick actions
            AnimatedContent(
                targetState = hasValidLocations && hasRoutePreview,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(150, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        animationSpec = tween(150, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 6 }
                    )) togetherWith
                    (fadeOut(animationSpec = tween(100, easing = LinearOutSlowInEasing)) +
                    slideOutVertically(
                        animationSpec = tween(100, easing = LinearOutSlowInEasing),
                        targetOffsetY = { -it / 6 }
                    ))
                },
                label = "clear_vs_actions_transition"
            ) { showClearButton ->
                if (showClearButton) {
                    // Clear Fields Button when both locations are set and route is found
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = onClearFields,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = io.peng.sparrowdelivery.ui.icons.SparrowIcons.UI.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                            Text(
                                text = "Clear Route",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    // Quick actions when user needs help getting started
                    QuickActionsRow(
                        onTrackDeliveryClick = onTrackDeliveryClick
                    )
                }
            }
            
            // Package type selector with fast animation
            AnimatedVisibility(
                visible = deliveryForm.packageType != null,
                enter = fadeIn(animationSpec = tween(150)) + slideInVertically(
                    animationSpec = tween(200, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 3 }
                ),
                exit = fadeOut(animationSpec = tween(100)) + slideOutVertically(
                    animationSpec = tween(150, easing = LinearOutSlowInEasing),
                    targetOffsetY = { -it / 3 }
                )
            ) {
                deliveryForm.packageType?.let { packageType ->
                    PackageTypeChip(packageType = packageType)
                }
            }
            
            // Intermediate Stops Section with fast concurrent animation
            AnimatedVisibility(
                visible = legacyForm.numberOfStops > 0,
                enter = fadeIn(animationSpec = tween(150)) + expandVertically(
                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                ),
                exit = fadeOut(animationSpec = tween(100)) + shrinkVertically(
                    animationSpec = tween(150, easing = LinearOutSlowInEasing)
                )
            ) {
                IntermediateStopsSection(
                    numberOfStops = legacyForm.numberOfStops,
                    stops = legacyForm.intermediateStops,
                    onNumberOfStopsChange = onNumberOfStopsChange,
                    onStopChange = onIntermediateStopChange
                )
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // More Options Button
                StitchSecondaryButton(
                    text = "More Options",
                    onClick = onMoreOptionsClick,
                    modifier = Modifier.weight(1f)
                )
                
                // Find Driver Button
                StitchPrimaryButton(
                    text = if (deliveryForm.isLoadingPricing) "Finding..." else "Find Driver",
                    onClick = onFindDriverClick,
                    modifier = Modifier.weight(1f),
                    enabled = deliveryForm.canProceed && !deliveryForm.isLoadingPricing,
                    loading = deliveryForm.isLoadingPricing
                )
            }
        }
}

@Composable
private fun LocationInputRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onMapPinClick: () -> Unit,
    onPlaceSelected: ((PlaceDetails) -> Unit)? = null,
    isPickupField: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // HTML-matching location input field with icon inside and Places Autocomplete
        StitchHtmlLocationField(
            value = value,
            onValueChange = onValueChange,
            onMapPinClick = onMapPinClick,
            onPlaceSelected = onPlaceSelected,
            placeholder = placeholder,
            leadingIcon = icon,
            iconColor = iconColor,
            isPickupField = isPickupField,
            modifier = Modifier.weight(1f)
        )
        

    }
}

@Composable
private fun RoutePreviewCard(
    isLoading: Boolean,
    error: String?,
    estimatedPrice: Double,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = modifier.fillMaxWidth(),
        variant = StitchCardVariant.Elevated,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    StitchHeading(
                        text = "Route Preview",
                        level = 4
                    )
                    
                    if (isLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            StitchText(
                                text = "Loading route...",
                                style = StitchTextStyle.Muted
                            )
                        }
                    } else if (error != null) {
                        StitchText(
                            text = error,
                            style = StitchTextStyle.Small,
                            color = stitchColors.destructive
                        )
                    } else {
                        // TODO: Show actual distance and duration when available
                        StitchText(
                            text = "~15 min • Route calculated",
                            style = StitchTextStyle.Muted
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    StitchText(
                        text = "Estimated fare",
                        style = StitchTextStyle.Small
                    )
                    StitchText(
                        text = "₵ ${String.format("%.2f", estimatedPrice)}",
                        style = StitchTextStyle.H4,
                        color = stitchColors.success
                    )
                    
                    // Tap indicator
                    if (!isLoading && error == null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            StitchText(
                                text = "View on map",
                                style = StitchTextStyle.Small,
                                color = stitchColors.primary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = stitchColors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    modifier: Modifier = Modifier,
    onTrackDeliveryClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionType.entries.forEach { actionType ->
            QuickActionButton(
                icon = when (actionType.icon) {
                    "history" -> Icons.Default.DateRange
                    "star" -> Icons.Default.Star
//                    "home" -> Icons.Default.Home
                    "track_changes" -> Icons.Default.Search
                    else -> Icons.Default.Place
                },
                title = actionType.displayName,
                onClick = { 
                    when (actionType) {
                        QuickActionType.TRACK -> onTrackDeliveryClick()
                        else -> { /* TODO: Handle other quick actions */ }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    CompactCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        variant = StitchCardVariant.Outlined,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                tint = stitchColors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            StitchText(
                text = title,
                style = StitchTextStyle.Small,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PackageTypeChip(
    packageType: PackageType,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = { /* TODO: Handle package type change */ },
        label = { Text(packageType.displayName) },
        selected = true,
        leadingIcon = {
            Icon(
                imageVector = when (packageType) {
                    PackageType.MEDICINE -> Icons.Default.Build
                    PackageType.RAW_FOOD -> Icons.Default.Settings
                    PackageType.COOKED_FOOD -> Icons.Default.ShoppingCart
                    PackageType.SENSITIVE -> Icons.Default.Warning
                    PackageType.ELECTRONIC -> Icons.Default.Phone
                    PackageType.EXTREME_CARE -> Icons.Default.Favorite
                },
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun IntermediateStopsSection(
    numberOfStops: Int,
    stops: List<String>,
    onNumberOfStopsChange: (Int) -> Unit,
    onStopChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StitchHeading(
                    text = "Intermediate Stops",
                    level = 4
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { 
                            if (numberOfStops > 0) {
                                onNumberOfStopsChange(numberOfStops - 1)
                            }
                        },
                        enabled = numberOfStops > 0
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove stop") // Using available alternative
                    }
                    
                    StitchText(
                        text = "$numberOfStops stops",
                        style = StitchTextStyle.P
                    )
                    
                    IconButton(
                        onClick = { 
                            if (numberOfStops < 5) {
                                onNumberOfStopsChange(numberOfStops + 1)
                            }
                        },
                        enabled = numberOfStops < 5
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add stop")
                    }
                }
            }
            
            // Stop input fields
            stops.forEachIndexed { index, stop ->
                StitchInput(
                    value = stop,
                    onValueChange = { onStopChange(index, it) },
                    label = "Stop ${index + 1}",
                    placeholder = "Enter stop ${index + 1} address",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RoutePreviewBottomSheetContent(
    routes: List<io.peng.sparrowdelivery.data.services.RouteInfo>,
    selectedRouteIndex: Int,
    pickupLocation: String,
    dropoffLocation: String,
    onRouteSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    onConfirmRoute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Header with back button and title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to form",
                    tint = stitchColors.onBackground
                )
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                StitchHeading(
                    text = "Choose Route",
                    level = 4
                )
                StitchText(
                    text = "${routes.size} route${if (routes.size != 1) "s" else ""} found",
                    style = StitchTextStyle.Muted
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp)) // Reduced from 16dp
        
        // Trip summary
//        TODO: please revisit
        StitchCard(
            variant = StitchCardVariant.Outlined,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = io.peng.sparrowdelivery.ui.icons.SparrowIcons.Delivery.GPS,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row {
                        Icon(
                            imageVector = io.peng.sparrowdelivery.ui.icons.SparrowIcons.Delivery.Navigation,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(18.dp)
                        )
                        StitchText(
                            text = pickupLocation,
                            style = StitchTextStyle.P,
                            maxLines = 1
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = stitchColors.outline
                                ) {}
                            }
                        }
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = io.peng.sparrowdelivery.ui.icons.SparrowIcons.Delivery.Navigation,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(18.dp)
                        )
                        StitchText(
                            text = dropoffLocation,
                            style = StitchTextStyle.P,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp)) // Reduced from 16dp
        
        // Route options
        StitchText(
            text = "Select your preferred route",
            style = StitchTextStyle.Muted
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp) // Reduced from 8dp
        ) {
            routes.forEachIndexed { index, route ->
                RouteOptionCard(
                    route = route,
                    isSelected = index == selectedRouteIndex,
                    routeNumber = index + 1,
                    onClick = { onRouteSelected(index) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp)) // Reduced from 16dp
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StitchTextButton(
                text = "Back",
                onClick = onBackClick,
                modifier = Modifier.weight(1f),
                variant = StitchButtonVariant.Outline
            )
            
            StitchTextButton(
                text = "Find Driver",
                onClick = onConfirmRoute,
                modifier = Modifier.weight(1f),
                variant = StitchButtonVariant.Primary
            )
        }
    }
}

@Composable
private fun RouteOptionCard(
    route: io.peng.sparrowdelivery.data.services.RouteInfo,
    isSelected: Boolean,
    routeNumber: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = modifier.fillMaxWidth(),
        variant = if (isSelected) StitchCardVariant.Elevated else StitchCardVariant.Default,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp), // Reduced from 16dp
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Reduced from 16dp
        ) {
            // Route number badge - smaller
            Surface(
                modifier = Modifier.size(24.dp), // Reduced from 32dp
                shape = androidx.compose.foundation.shape.CircleShape,
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
                        style = StitchTextStyle.Small, // Smaller text
                        color = if (isSelected) {
                            stitchColors.onPrimary
                        } else {
                            stitchColors.textSecondary
                        }
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Main route info - more compact
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp), // Reduced from 16dp
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Duration
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp) // Reduced from 4dp
                    ) {
                        StitchText(
                            text = "⏱",
                            style = StitchTextStyle.Small
                        )
                        StitchText(
                            text = formatDuration(route.duration),
                            style = StitchTextStyle.Small // Changed from P to Small
                        )
                    }
                    
                    // Distance
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp) // Reduced from 4dp
                    ) {
                        StitchText(
                            text = "📏",
                            style = StitchTextStyle.Small
                        )
                        StitchText(
                            text = formatDistance(route.distance),
                            style = StitchTextStyle.Small // Changed from P to Small
                        )
                    }
                    
                    // Route type inline
                    val routeType = when {
                        routeNumber == 1 -> "• Fastest"
                        else -> "• Alternative"
                    }
                    
                    StitchText(
                        text = routeType,
                        style = StitchTextStyle.Small,
                        color = if (isSelected) {
                            stitchColors.primary
                        } else {
                            stitchColors.textSecondary
                        }
                    )
                }
                
                // Traffic indicator - only if present, more compact
                if (route.hasTraffic) {
                    Spacer(modifier = Modifier.height(2.dp)) // Reduced from 4dp
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        StitchText(
                            text = "🚗",
                            style = StitchTextStyle.Small
                        )
                        StitchText(
                            text = "Traffic included",
                            style = StitchTextStyle.Small,
                            color = stitchColors.textSecondary
                        )
                    }
                }
            }
            
            // Selection indicator
            if (isSelected) {
                Surface(
                    modifier = Modifier.size(6.dp), // Reduced from 8dp
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = stitchColors.primary
                ) {}
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
