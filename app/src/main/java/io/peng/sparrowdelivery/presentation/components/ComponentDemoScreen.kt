package io.peng.sparrowdelivery.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.*
//
@Composable
fun ComponentDemoScreen(
    onBackClick: () -> Unit = {}
) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var search by remember { mutableStateOf("") }
//    var textArea by remember { mutableStateOf("") }
//    var selectedLocation by remember { mutableStateOf<ShadcnSelectOption<String>?>(null) }
//    var selectedMultiple by remember { mutableStateOf<List<ShadcnSelectOption<String>>>(emptyList()) }
//    var showToast by remember { mutableStateOf(false) }
//
//    val locations = listOf(
//        ShadcnSelectOption("accra-mall", "Accra Mall", "Tetteh Quarshie Interchange", Icons.Default.ShoppingCart),
//        ShadcnSelectOption("kotoka", "Kotoka Airport", "International Airport", Icons.Default.LocationOn),
//        ShadcnSelectOption("legon", "University of Ghana", "Legon Campus", Icons.Default.LocationOn),
//        ShadcnSelectOption("circle", "Circle", "Commercial District", Icons.Default.LocationOn),
//        ShadcnSelectOption("tema", "Tema Port", "Industrial Area", Icons.Default.LocationOn)
//    )
//
//    StitchTheme {
//        val stitchColors = LocalStitchColorScheme.current
//
//        Box(modifier = Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState())
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                // Header
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column {
//                        StitchHeading(
//                            text = "Component Library",
//                            level = 1,
//                            color = stitchColors.primary
//                        )
//                        StitchText(
//                            text = "Stitch Design System components for Android",
//                            style = StitchTextStyle.Muted,
//                            color = stitchColors.textSecondary
//                        )
//                    }
//
//                    StitchIconButton(
//                        icon = Icons.Default.ArrowBack,
//                        onClick = onBackClick,
//                        contentDescription = "Back"
//                    )
//                }
//
//                // Typography Section with Inter Font showcase
//                StitchSection(
//                    title = "Typography (Inter Font)",
//                    description = "Professional typography with Inter font family"
//                ) {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        // App branding showcase
//                        StitchCard(variant = CardVariant.Default) {
//                            StitchHeading(text = "SparrowDelivery", level = 1, color = stitchColors.primary)
//                            StitchText(
//                                text = "48px ExtraBold - Perfect for hero sections and branding",
//                                style = Tex.Muted,
//                                color = stitchColors.textSecondary
//                            )
//                        }
//
//                        // Delivery context examples
//                        StitchHeading(text = "Your Package is Ready!", level = 2)
//                        StitchText(
//                            text = "36px Bold - Page titles and major notifications",
//                            style = StitchTextStyle.Muted,
//                            color = stitchColors.textSecondary
//                        )
//
//                        StitchHeading(text = "Track Your Delivery", level = 3)
//                        StitchText(
//                            text = "24px SemiBold - Section headings",
//                            style = StitchTextStyle.Muted,
//                            color = stitchColors.textSecondary
//                        )
//
//                        StitchHeading(text = "Order Details", level = 4)
//                        StitchText(
//                            text = "20px SemiBold - Card titles and form headers",
//                            style = StitchTextStyle.Muted,
//                            color = stitchColors.textSecondary
//                        )
//
//                        // Real delivery content example
//                        StitchText(
//                            text = "Experience fast and reliable delivery across Ghana. Our platform connects you with trusted drivers for seamless package transportation from pickup to destination.",
//                            style = StitchTextStyle.P
//                        )
//                        StitchText(
//                            text = "16px Normal - Body text optimized for mobile readability",
//                            style = StitchTextStyle.Muted,
//                            color = stitchColors.textSecondary
//                        )
//
//                        StitchText(
//                            text = "Get started with your first delivery in just a few taps!",
//                            style = StitchTextStyle.Lead
//                        )
//                        StitchText(
//                            text = "20px Normal - Lead paragraphs for emphasis",
//                            style = StitchTextStyle.Muted,
//                            color = stitchColors.textSecondary
//                        )
//
//                        // Font features highlight
//                        StitchCard(variant = StitchCardVariant.Outlined) {
//                            StitchHeading(text = "🔤 Inter Font Benefits", level = 4)
//                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
//                                StitchText("• Optimized for screens and mobile devices", style = StitchTextStyle.Small)
//                                StitchText("• Excellent readability at all sizes", style = StitchTextStyle.Small)
//                                StitchText("• Modern, professional appearance", style = StitchTextStyle.Small)
//                                StitchText("• Used by top tech companies worldwide", style = StitchTextStyle.Small)
//                                StitchText("• Perfect letter spacing for UI interfaces", style = StitchTextStyle.Small)
//                            }
//                        }
//                    }
//                }
//
//                // Buttons Section
//                StitchSection(
//                    title = "Buttons",
//                    description = "Various button styles and states"
//                ) {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        // Primary buttons
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            StitchTextButton(
//                                text = "Primary",
//                                onClick = { },
//                                variant = StitchButtonVariant.Primary
//                            )
//                            StitchTextButton(
//                                text = "Secondary",
//                                onClick = { },
//                                variant = StitchButtonVariant.Secondary
//                            )
//                            StitchTextButton(
//                                text = "Outline",
//                                onClick = { },
//                                variant = StitchButtonVariant.Outline
//                            )
//                        }
//
//                        // Destructive and other variants
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            StitchTextButton(
//                                text = "Destructive",
//                                onClick = { },
//                                variant = StitchButtonVariant.Destructive
//                            )
//                            StitchTextButton(
//                                text = "Ghost",
//                                onClick = { },
//                                variant = StitchButtonVariant.Ghost
//                            )
//                            StitchTextButton(
//                                text = "Link",
//                                onClick = { },
//                                variant = StitchButtonVariant.Link
//                            )
//                        }
//
//                        // Buttons with icons
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            StitchTextButton(
//                                text = "Send Message",
//                                onClick = { },
//                                leadingIcon = Icons.Default.Send,
//                                variant = StitchButtonVariant.Primary
//                            )
//                            StitchIconButton(
//                                icon = Icons.Default.Add,
//                                onClick = { },
//                                contentDescription = "Add item"
//                            )
//                            StitchIconButton(
//                                icon = Icons.Default.Settings,
//                                onClick = { },
//                                variant = StitchIconButtonVariant.Outline,
//                                contentDescription = "Settings"
//                            )
//                        }
//
//                        // Button sizes
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            StitchTextButton(
//                                text = "Small",
//                                onClick = { },
//                                size = StitchButtonSize.Small
//                            )
//                            StitchTextButton(
//                                text = "Default",
//                                onClick = { },
//                                size = StitchButtonSize.Default
//                            )
//                            StitchTextButton(
//                                text = "Large",
//                                onClick = { },
//                                size = StitchButtonSize.Large
//                            )
//                        }
//                    }
//                }
//
//                // Sliding Toggle Section
//                StitchSection(
//                    title = "Sliding Toggle",
//                    description = "iOS-style segmented control for delivery scheduling"
//                ) {
//                    var selectedScheduleType by remember { mutableStateOf("now") }
//                    var selectedViewType by remember { mutableStateOf("list") }
//
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        // Delivery schedule toggle (matches the actual app implementation)
//                        Column(
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            StitchText(text = "Delivery Schedule", style = StitchTextStyle.Small)
//                            SlidingToggle(
//                                options = listOf(
//                                    SlidingToggleOption(
//                                        value = "now",
//                                        label = "Now",
//                                        icon = Icons.Default.PlayArrow
//                                    ),
//                                    SlidingToggleOption(
//                                        value = "scheduled",
//                                        label = "Schedule",
//                                        icon = Icons.Default.DateRange
//                                    )
//                                ),
//                                selectedOption = selectedScheduleType,
//                                onOptionSelected = { selectedScheduleType = it },
//                                modifier = Modifier.width(140.dp),
//                                height = 36.dp
//                            )
//                            if (selectedScheduleType == "scheduled") {
//                                StitchText(
//                                    text = "📅 Schedule selected - date picker would open",
//                                    style = StitchTextStyle.Muted,
//                                    color = stitchColors.textSecondary
//                                )
//                            } else {
//                                StitchText(
//                                    text = "⚡ Immediate delivery selected",
//                                    style = StitchTextStyle.Muted,
//                                    color = stitchColors.textSecondary
//                                )
//                            }
//                        }
//
//                        // View type toggle (additional example)
//                        Column(
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            StitchText(text = "View Type", style = StitchTextStyle.Small)
//                            SlidingToggle(
//                                options = listOf(
//                                    SlidingToggleOption(
//                                        value = "list",
//                                        label = "List",
//                                        icon = Icons.Default.List
//                                    ),
//                                    SlidingToggleOption(
//                                        value = "map",
//                                        label = "Map",
//                                        icon = Icons.Default.LocationOn
//                                    )
//                                ),
//                                selectedOption = selectedViewType,
//                                onOptionSelected = { selectedViewType = it },
//                                modifier = Modifier.width(120.dp),
//                                height = 32.dp
//                            )
//                        }
//
//                        // Benefits callout
//                        StitchCard(variant = StitchCardVariant.Outlined) {
//                            StitchHeading(text = "✨ Sliding Toggle Benefits", level = 4)
//                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
//                                StitchText("• Smooth, iOS-style animations", style = StitchTextStyle.Small)
//                                StitchText("• Better UX than traditional chips", style = StitchTextStyle.Small)
//                                StitchText("• Clear visual feedback", style = StitchTextStyle.Small)
//                                StitchText("• Professional, modern appearance", style = StitchTextStyle.Small)
//                            }
//                        }
//
//                        // iOS Wheel Picker demo
//                        var showWheelPicker by remember { mutableStateOf(false) }
//                        StitchTextButton(
//                            text = "📅 Show iOS-Style Date Picker",
//                            onClick = { showWheelPicker = true },
//                            variant = StitchButtonVariant.Outline,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                        if (showWheelPicker) {
//                            WheelDateTimePickerDialog(
//                                onDateTimeSelected = { dateMillis ->
//                                    showWheelPicker = false
//                                    // Handle selected date/time
//                                },
//                                onDismiss = { showWheelPicker = false }
//                            )
//                        }
//                    }
//                }
//
//                // Input Fields Section
//                StitchSection(
//                    title = "Input Fields",
//                    description = "Form inputs with validation and states"
//                ) {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        StitchEmailInput(
//                            value = email,
//                            onValueChange = { email = it },
//                            label = "Email Address",
//                            placeholder = "Enter your email",
//                            helper = "We'll never share your email with anyone else."
//                        )
//
//                        StitchPasswordInput(
//                            value = password,
//                            onValueChange = { password = it },
//                            label = "Password",
//                            helper = "Must be at least 8 characters long"
//                        )
//
//                        StitchSearchInput(
//                            value = search,
//                            onValueChange = { search = it },
//                            placeholder = "Search deliveries...",
//                            onSearch = { /* Handle search */ }
//                        )
//
//                        StitchNumberInput(
//                            value = "25.50",
//                            onValueChange = { },
//                            label = "Delivery Fee",
//                            placeholder = "0.00",
//                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
//                        )
//
//                        StitchTextArea(
//                            value = textArea,
//                            onValueChange = { textArea = it },
//                            label = "Special Instructions",
//                            placeholder = "Add any special delivery instructions...",
//                            maxLines = 4
//                        )
//
//                        // Smart Address Input demo
//                        var addressValue by remember { mutableStateOf("") }
//                        Column {
//                            StitchText(
//                                text = "Smart Address Input",
//                                style = StitchTextStyle.Small,
//                                color = stitchColors.onSurface
//                            )
//                            SmartAddressInput(
//                                value = addressValue,
//                                onValueChange = { addressValue = it },
//                                placeholder = "Search addresses...",
//                                onPlaceSelected = { placeDetails ->
//                                    // Handle place selection
//                                },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                            StitchText(
//                                text = "📍 Shows favorites & recents first • 💰 Reduces API costs",
//                                style = StitchTextStyle.Small,
//                                color = stitchColors.textSecondary,
//                                modifier = Modifier.padding(top = 4.dp)
//                            )
//                        }
//
//                        // Input variants
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            StitchInput(
//                                value = "",
//                                onValueChange = { },
//                                placeholder = "Default",
//                                variant = StitchInputVariant.Default,
//                                modifier = Modifier.weight(1f)
//                            )
//                            StitchInput(
//                                value = "",
//                                onValueChange = { },
//                                placeholder = "Outlined",
//                                variant = StitchInputVariant.Outlined,
//                                modifier = Modifier.weight(1f)
//                            )
//                            StitchInput(
//                                value = "",
//                                onValueChange = { },
//                                placeholder = "Ghost",
//                                variant = StitchInputVariant.Ghost,
//                                modifier = Modifier.weight(1f)
//                            )
//                        }
//
//                        // Input states
//                        StitchInput(
//                            value = "invalid@email",
//                            onValueChange = { },
//                            label = "Email with Error",
//                            error = "Please enter a valid email address",
//                            leadingIcon = Icons.Default.Email
//                        )
//
//                        StitchInput(
//                            value = "john@example.com",
//                            onValueChange = { },
//                            label = "Valid Email",
//                            state = StitchInputState.Success,
//                            helper = "Email address is valid",
//                            leadingIcon = Icons.Default.Email
//                        )
//                    }
//                }
//
//                // Badges and Chips Section
//                StitchSection(
//                    title = "Badges & Chips",
//                    description = "Status indicators and labels"
//                ) {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        // Badge variants
//                        Column(
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            StitchText(text = "Badge Variants", style = StitchTextStyle.Small)
//                            Row(
//                                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                StitchBadge(text = "Default", variant = StitchBadgeVariant.Default)
//                                StitchBadge(text = "Secondary", variant = StitchBadgeVariant.Secondary)
//                                StitchBadge(text = "Success", variant = StitchBadgeVariant.Success)
//                                StitchBadge(text = "Warning", variant = StitchBadgeVariant.Warning)
//                                StitchBadge(text = "Destructive", variant = StitchBadgeVariant.Destructive)
//                            }
//                        }
//
//                        // Status indicators
//                        Column(
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            StitchText(text = "Status Indicators", style = StitchTextStyle.Small)
//                            Row(
//                                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Row(
//                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    StitchStatusDot(variant = StitchBadgeVariant.Success)
//                                    StitchText(text = "Online", style = StitchTextStyle.Small)
//                                }
//                                Row(
//                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    StitchStatusDot(variant = StitchBadgeVariant.Destructive)
//                                    StitchText(text = "Offline", style = StitchTextStyle.Small)
//                                }
//                                Row(
//                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    StitchStatusDot(variant = StitchBadgeVariant.Warning)
//                                    StitchText(text = "Away", style = StitchTextStyle.Small)
//                                }
//                            }
//                        }
//
//                        // Removable chips
//                        Column(
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            StitchText(text = "Removable Chips", style = StitchTextStyle.Small)
//                            Row(
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                Chip(
//                                    text = "Fast Delivery",
//                                    variant = StitchBadgeVariant.Default,
//                                    onRemove = { /* Handle removal */ }
//                                )
//                                Chip(
//                                    text = "Fragile",
//                                    variant = StitchBadgeVariant.Warning,
//                                    icon = Icons.Default.Warning,
//                                    onRemove = { /* Handle removal */ }
//                                )
//                                Chip(
//                                    text = "Priority",
//                                    variant = StitchBadgeVariant.Destructive,
//                                    onRemove = { /* Handle removal */ }
//                                )
//                            }
//                        }
//
//                        // Notification badge
//                        Column(
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            StitchText(text = "Notification Badges", style = StitchTextStyle.Small)
//                            Row(
//                                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Box {
//                                    StitchIconButton(
//                                        icon = Icons.Default.Notifications,
//                                        onClick = { },
//                                        contentDescription = "Notifications"
//                                    )
//                                    StitchNotificationBadge(
//                                        count = 3,
//                                        modifier = Modifier.align(Alignment.TopEnd)
//                                    )
//                                }
//
//                                Box {
//                                    StitchIconButton(
//                                        icon = Icons.Default.Email,
//                                        onClick = { },
//                                        contentDescription = "Messages"
//                                    )
//                                    StitchNotificationBadge(
//                                        count = 99,
//                                        modifier = Modifier.align(Alignment.TopEnd)
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // Dropdowns Section
//                StitchSection(
//                    title = "Dropdowns & Selects",
//                    description = "Selection controls with search"
//                ) {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        StitchSelect(
//                            options = locations,
//                            selectedOption = selectedLocation,
//                            onOptionSelected = { selectedLocation = it },
//                            label = "Pickup Location",
//                            placeholder = "Select pickup location...",
//                            helper = "Choose from popular locations in Accra",
//                            searchable = true
//                        )
//
//                        StitchCombobox(
//                            options = locations,
//                            selectedOption = selectedLocation,
//                            onOptionSelected = { selectedLocation = it },
//                            onValueChange = { /* Handle typing */ },
//                            label = "Destination (Type to search)",
//                            placeholder = "Type location name...",
//                            helper = "Start typing to search locations"
//                        )
//                    }
//                }
//
//                // Alerts Section
//                StitchSection(
//                    title = "Alerts & Notifications",
//                    description = "Status messages and notifications"
//                ) {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        StitchAlert(
//                            title = "Success",
//                            description = "Your delivery has been scheduled successfully! The driver will contact you shortly.",
//                            variant = StitchAlertVariant.Success,
//                            dismissible = true,
//                            onDismiss = { }
//                        )
//
//                        StitchAlert(
//                            title = "Warning",
//                            description = "Traffic conditions may affect delivery time. Expected delay: 10-15 minutes.",
//                            variant = StitchAlertVariant.Warning,
//                            dismissible = true,
//                            onDismiss = { }
//                        )
//
//                        StitchAlert(
//                            title = "Error",
//                            description = "Unable to connect to driver. Please check your internet connection and try again.",
//                            variant = StitchAlertVariant.Destructive,
//                            dismissible = true,
//                            onDismiss = { },
//                            action = {
//                                StitchTextButton(
//                                    text = "Retry",
//                                    onClick = { },
//                                    variant = StitchButtonVariant.Outline,
//                                    size = StitchButtonSize.Small
//                                )
//                            }
//                        )
//
//                        StitchBanner(
//                            message = "New delivery zones now available in Tema and Kumasi!",
//                            variant = StitchAlertVariant.Info,
//                            icon = Icons.Default.Info,
//                            dismissible = true,
//                            onDismiss = { },
//                            action = {
//                                StitchTextButton(
//                                    text = "Learn More",
//                                    onClick = { },
//                                    variant = StitchButtonVariant.Link,
//                                    size = StitchButtonSize.Small
//                                )
//                            }
//                        )
//
//                        // Inline alerts
//                        Column(
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            StitchInlineAlert(
//                                message = "Please select a pickup location",
//                                variant = StitchAlertVariant.Destructive
//                            )
//                            StitchInlineAlert(
//                                message = "Driver has been notified of your request",
//                                variant = StitchAlertVariant.Success
//                            )
//                            StitchInlineAlert(
//                                message = "Estimated delivery time: 30-45 minutes",
//                                variant = StitchAlertVariant.Info
//                            )
//                        }
//
//                        // Toast trigger button
//                        StitchTextButton(
//                            text = "Show Toast Notification",
//                            onClick = { showToast = true },
//                            variant = StitchButtonVariant.Outline
//                        )
//                    }
//                }
//
//                // Cards Section
//                StitchSection(
//                    title = "Cards & Containers",
//                    description = "Content containers and layouts"
//                ) {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            StitchCard(
//                                variant = StitchCardVariant.Default,
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                StitchText(text = "Default Card", style = StitchTextStyle.Small)
//                                StitchText(
//                                    text = "With border and shadow",
//                                    style = StitchTextStyle.Muted,
//                                    color = stitchColors.textSecondary
//                                )
//                            }
//
//                            StitchCard(
//                                variant = StitchCardVariant.Elevated,
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                StitchText(text = "Elevated Card", style = StitchTextStyle.Small)
//                                StitchText(
//                                    text = "With larger shadow",
//                                    style = StitchTextStyle.Muted,
//                                    color = stitchColors.textSecondary
//                                )
//                            }
//                        }
//
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            StitchCard(
//                                variant = StitchCardVariant.Outlined,
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                StitchText(text = "Outlined Card", style = StitchTextStyle.Small)
//                                StitchText(
//                                    text = "With strong border",
//                                    style = StitchTextStyle.Muted,
//                                    color = stitchColors.textSecondary
//                                )
//                            }
//
//                            StitchCard(
//                                variant = StitchCardVariant.Ghost,
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                StitchText(text = "Ghost Card", style = StitchTextStyle.Small)
//                                StitchText(
//                                    text = "Minimal styling",
//                                    style = StitchTextStyle.Muted,
//                                    color = stitchColors.textSecondary
//                                )
//                            }
//                        }
//                    }
//                }
//
//                // Spacer at the bottom
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            // Toast overlay
//            if (showToast) {
//                StitchToast(
//                    message = "This is a toast notification! It will disappear automatically.",
//                    variant = StitchAlertVariant.Success,
//                    duration = 3000L,
//                    onDismiss = { showToast = false },
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .padding(8.dp)
//                )
//            }
//        }
//    }
}
