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
import io.peng.sparrowdelivery.ui.theme.*

@Composable
fun ComponentDemoScreen(
    onBackClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var textArea by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<ShadcnSelectOption<String>?>(null) }
    var selectedMultiple by remember { mutableStateOf<List<ShadcnSelectOption<String>>>(emptyList()) }
    var showToast by remember { mutableStateOf(false) }

    val locations = listOf(
        ShadcnSelectOption("accra-mall", "Accra Mall", "Tetteh Quarshie Interchange", Icons.Default.ShoppingCart),
        ShadcnSelectOption("kotoka", "Kotoka Airport", "International Airport", Icons.Default.LocationOn),
        ShadcnSelectOption("legon", "University of Ghana", "Legon Campus", Icons.Default.LocationOn),
        ShadcnSelectOption("circle", "Circle", "Commercial District", Icons.Default.LocationOn),
        ShadcnSelectOption("tema", "Tema Port", "Industrial Area", Icons.Default.LocationOn)
    )

    ShadcnTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ShadcnSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.xl)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        ShadcnHeading(
                            text = "Component Library",
                            level = 1,
                            color = ShadcnTheme.colors.primary
                        )
                        ShadcnText(
                            text = "shadcn/ui style components for Android",
                            style = ShadcnTextStyle.Muted,
                            color = ShadcnTheme.colors.mutedForeground
                        )
                    }
                    
                    ShadcnIconButton(
                        icon = Icons.Default.ArrowBack,
                        onClick = onBackClick,
                        contentDescription = "Back"
                    )
                }

                // Typography Section with Inter Font showcase
                ShadcnSection(
                    title = "Typography (Inter Font)",
                    description = "Professional typography with Inter font family"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        // App branding showcase
                        ShadcnCard(variant = ShadcnCardVariant.Default) {
                            ShadcnHeading(text = "SparrowDelivery", level = 1, color = ShadcnTheme.colors.primary)
                            ShadcnMutedText("48px ExtraBold - Perfect for hero sections and branding")
                        }
                        
                        // Delivery context examples
                        ShadcnHeading(text = "Your Package is Ready!", level = 2)
                        ShadcnMutedText("36px Bold - Page titles and major notifications")
                        
                        ShadcnHeading(text = "Track Your Delivery", level = 3)
                        ShadcnMutedText("24px SemiBold - Section headings")
                        
                        ShadcnHeading(text = "Order Details", level = 4)
                        ShadcnMutedText("20px SemiBold - Card titles and form headers")
                        
                        // Real delivery content example
                        ShadcnText(
                            text = "Experience fast and reliable delivery across Ghana. Our platform connects you with trusted drivers for seamless package transportation from pickup to destination.", 
                            style = ShadcnTextStyle.P
                        )
                        ShadcnMutedText("16px Normal - Body text optimized for mobile readability")
                        
                        ShadcnText(
                            text = "Get started with your first delivery in just a few taps!", 
                            style = ShadcnTextStyle.Lead
                        )
                        ShadcnMutedText("20px Normal - Lead paragraphs for emphasis")
                        
                        // Font features highlight
                        ShadcnCard(variant = ShadcnCardVariant.Outlined) {
                            ShadcnText("🔤 Inter Font Benefits", style = ShadcnTextStyle.H4)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                ShadcnText("• Optimized for screens and mobile devices", style = ShadcnTextStyle.Small)
                                ShadcnText("• Excellent readability at all sizes", style = ShadcnTextStyle.Small)
                                ShadcnText("• Modern, professional appearance", style = ShadcnTextStyle.Small)
                                ShadcnText("• Used by top tech companies worldwide", style = ShadcnTextStyle.Small)
                                ShadcnText("• Perfect letter spacing for UI interfaces", style = ShadcnTextStyle.Small)
                            }
                        }
                    }
                }

                // Buttons Section
                ShadcnSection(
                    title = "Buttons",
                    description = "Various button styles and states"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        // Primary buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnTextButton(
                                text = "Primary",
                                onClick = { },
                                variant = ShadcnButtonVariant.Default
                            )
                            ShadcnTextButton(
                                text = "Secondary",
                                onClick = { },
                                variant = ShadcnButtonVariant.Secondary
                            )
                            ShadcnTextButton(
                                text = "Outline",
                                onClick = { },
                                variant = ShadcnButtonVariant.Outline
                            )
                        }
                        
                        // Destructive and other variants
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnTextButton(
                                text = "Destructive",
                                onClick = { },
                                variant = ShadcnButtonVariant.Destructive
                            )
                            ShadcnTextButton(
                                text = "Ghost",
                                onClick = { },
                                variant = ShadcnButtonVariant.Ghost
                            )
                            ShadcnTextButton(
                                text = "Link",
                                onClick = { },
                                variant = ShadcnButtonVariant.Link
                            )
                        }
                        
                        // Buttons with icons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnTextButton(
                                text = "Send Message",
                                onClick = { },
                                leadingIcon = Icons.Default.Send,
                                variant = ShadcnButtonVariant.Default
                            )
                            ShadcnIconButton(
                                icon = Icons.Default.Add,
                                onClick = { },
                                contentDescription = "Add item"
                            )
                            ShadcnIconButton(
                                icon = Icons.Default.Settings,
                                onClick = { },
                                variant = ShadcnButtonVariant.Outline,
                                contentDescription = "Settings"
                            )
                        }
                        
                        // Button sizes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShadcnTextButton(
                                text = "Small",
                                onClick = { },
                                size = ShadcnButtonSize.Small
                            )
                            ShadcnTextButton(
                                text = "Default",
                                onClick = { },
                                size = ShadcnButtonSize.Default
                            )
                            ShadcnTextButton(
                                text = "Large",
                                onClick = { },
                                size = ShadcnButtonSize.Large
                            )
                        }
                    }
                }

                // Sliding Toggle Section  
                ShadcnSection(
                    title = "Sliding Toggle",
                    description = "iOS-style segmented control for delivery scheduling"
                ) {
                    var selectedScheduleType by remember { mutableStateOf("now") }
                    var selectedViewType by remember { mutableStateOf("list") }
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        // Delivery schedule toggle (matches the actual app implementation)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnText(text = "Delivery Schedule", style = ShadcnTextStyle.Small)
                            SlidingToggle(
                                options = listOf(
                                    SlidingToggleOption(
                                        value = "now",
                                        label = "Now",
                                        icon = Icons.Default.PlayArrow
                                    ),
                                    SlidingToggleOption(
                                        value = "scheduled",
                                        label = "Schedule", 
                                        icon = Icons.Default.DateRange
                                    )
                                ),
                                selectedOption = selectedScheduleType,
                                onOptionSelected = { selectedScheduleType = it },
                                modifier = Modifier.width(140.dp),
                                height = 36.dp
                            )
                            if (selectedScheduleType == "scheduled") {
                                ShadcnMutedText("📅 Schedule selected - date picker would open")
                            } else {
                                ShadcnMutedText("⚡ Immediate delivery selected")
                            }
                        }
                        
                        // View type toggle (additional example)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnText(text = "View Type", style = ShadcnTextStyle.Small)
                            SlidingToggle(
                                options = listOf(
                                    SlidingToggleOption(
                                        value = "list",
                                        label = "List",
                                        icon = Icons.Default.List
                                    ),
                                    SlidingToggleOption(
                                        value = "map",
                                        label = "Map",
                                        icon = Icons.Default.LocationOn
                                    )
                                ),
                                selectedOption = selectedViewType,
                                onOptionSelected = { selectedViewType = it },
                                modifier = Modifier.width(120.dp),
                                height = 32.dp
                            )
                        }
                        
                        // Benefits callout
                        ShadcnCard(variant = ShadcnCardVariant.Outlined) {
                            ShadcnText("✨ Sliding Toggle Benefits", style = ShadcnTextStyle.H4)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                ShadcnText("• Smooth, iOS-style animations", style = ShadcnTextStyle.Small)
                                ShadcnText("• Better UX than traditional chips", style = ShadcnTextStyle.Small)
                                ShadcnText("• Clear visual feedback", style = ShadcnTextStyle.Small)
                                ShadcnText("• Professional, modern appearance", style = ShadcnTextStyle.Small)
                            }
                        }
                        
                        // iOS Wheel Picker demo
                        var showWheelPicker by remember { mutableStateOf(false) }
                        ShadcnTextButton(
                            text = "📅 Show iOS-Style Date Picker",
                            onClick = { showWheelPicker = true },
                            variant = ShadcnButtonVariant.Outline,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        if (showWheelPicker) {
                            ShadcnWheelDateTimePickerDialog(
                                onDateTimeSelected = { dateMillis ->
                                    showWheelPicker = false
                                    // Handle selected date/time
                                },
                                onDismiss = { showWheelPicker = false }
                            )
                        }
                    }
                }

                // Input Fields Section
                ShadcnSection(
                    title = "Input Fields",
                    description = "Form inputs with validation and states"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        ShadcnEmailInput(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            placeholder = "Enter your email",
                            helper = "We'll never share your email with anyone else."
                        )
                        
                        ShadcnPasswordInput(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            helper = "Must be at least 8 characters long"
                        )
                        
                        ShadcnSearchInput(
                            value = search,
                            onValueChange = { search = it },
                            placeholder = "Search deliveries...",
                            onSearch = { /* Handle search */ }
                        )
                        
                        ShadcnNumberInput(
                            value = "25.50",
                            onValueChange = { },
                            label = "Delivery Fee",
                            placeholder = "0.00",
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                        
                        ShadcnTextArea(
                            value = textArea,
                            onValueChange = { textArea = it },
                            label = "Special Instructions",
                            placeholder = "Add any special delivery instructions...",
                            maxLines = 4
                        )
                        
                        // Smart Address Input demo
                        var addressValue by remember { mutableStateOf("") }
                        Column {
                            ShadcnText(
                                text = "Smart Address Input",
                                style = ShadcnTextStyle.Small,
                                color = ShadcnTheme.colors.foreground
                            )
                            SmartAddressInput(
                                value = addressValue,
                                onValueChange = { addressValue = it },
                                placeholder = "Search addresses...",
                                onPlaceSelected = { placeDetails ->
                                    // Handle place selection
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            ShadcnText(
                                text = "📍 Shows favorites & recents first • 💰 Reduces API costs",
                                style = ShadcnTextStyle.Small,
                                color = ShadcnTheme.colors.mutedForeground,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        // Input variants
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnInput(
                                value = "",
                                onValueChange = { },
                                placeholder = "Default",
                                variant = ShadcnInputVariant.Default,
                                modifier = Modifier.weight(1f)
                            )
                            ShadcnInput(
                                value = "",
                                onValueChange = { },
                                placeholder = "Outlined",
                                variant = ShadcnInputVariant.Outlined,
                                modifier = Modifier.weight(1f)
                            )
                            ShadcnInput(
                                value = "",
                                onValueChange = { },
                                placeholder = "Ghost",
                                variant = ShadcnInputVariant.Ghost,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        // Input states
                        ShadcnInput(
                            value = "invalid@email",
                            onValueChange = { },
                            label = "Email with Error",
                            error = "Please enter a valid email address",
                            leadingIcon = Icons.Default.Email
                        )
                        
                        ShadcnInput(
                            value = "john@example.com",
                            onValueChange = { },
                            label = "Valid Email",
                            state = ShadcnInputState.Success,
                            helper = "Email address is valid",
                            leadingIcon = Icons.Default.Email
                        )
                    }
                }

                // Badges and Chips Section
                ShadcnSection(
                    title = "Badges & Chips",
                    description = "Status indicators and labels"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        // Badge variants
                        Column(
                            verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnText(text = "Badge Variants", style = ShadcnTextStyle.Small)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ShadcnBadge(text = "Default", variant = ShadcnBadgeVariant.Default)
                                ShadcnBadge(text = "Secondary", variant = ShadcnBadgeVariant.Secondary)
                                ShadcnBadge(text = "Success", variant = ShadcnBadgeVariant.Success)
                                ShadcnBadge(text = "Warning", variant = ShadcnBadgeVariant.Warning)
                                ShadcnBadge(text = "Destructive", variant = ShadcnBadgeVariant.Destructive)
                            }
                        }
                        
                        // Status indicators
                        Column(
                            verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnText(text = "Status Indicators", style = ShadcnTextStyle.Small)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.xs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ShadcnStatusDot(variant = ShadcnBadgeVariant.Success)
                                    ShadcnText(text = "Online", style = ShadcnTextStyle.Small)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.xs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ShadcnStatusDot(variant = ShadcnBadgeVariant.Destructive)
                                    ShadcnText(text = "Offline", style = ShadcnTextStyle.Small)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.xs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ShadcnStatusDot(variant = ShadcnBadgeVariant.Warning)
                                    ShadcnText(text = "Away", style = ShadcnTextStyle.Small)
                                }
                            }
                        }
                        
                        // Removable chips
                        Column(
                            verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnText(text = "Removable Chips", style = ShadcnTextStyle.Small)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                            ) {
                                ShadcnChip(
                                    text = "Fast Delivery",
                                    variant = ShadcnBadgeVariant.Default,
                                    onRemove = { /* Handle removal */ }
                                )
                                ShadcnChip(
                                    text = "Fragile",
                                    variant = ShadcnBadgeVariant.Warning,
                                    icon = Icons.Default.Warning,
                                    onRemove = { /* Handle removal */ }
                                )
                                ShadcnChip(
                                    text = "Priority",
                                    variant = ShadcnBadgeVariant.Destructive,
                                    onRemove = { /* Handle removal */ }
                                )
                            }
                        }
                        
                        // Notification badge
                        Column(
                            verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                        ) {
                            ShadcnText(text = "Notification Badges", style = ShadcnTextStyle.Small)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.lg),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box {
                                    ShadcnIconButton(
                                        icon = Icons.Default.Notifications,
                                        onClick = { },
                                        contentDescription = "Notifications"
                                    )
                                    ShadcnNotificationBadge(
                                        count = 3,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                                
                                Box {
                                    ShadcnIconButton(
                                        icon = Icons.Default.Email,
                                        onClick = { },
                                        contentDescription = "Messages"
                                    )
                                    ShadcnNotificationBadge(
                                        count = 99,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                            }
                        }
                    }
                }

                // Dropdowns Section
                ShadcnSection(
                    title = "Dropdowns & Selects",
                    description = "Selection controls with search"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        ShadcnSelect(
                            options = locations,
                            selectedOption = selectedLocation,
                            onOptionSelected = { selectedLocation = it },
                            label = "Pickup Location",
                            placeholder = "Select pickup location...",
                            helper = "Choose from popular locations in Accra",
                            searchable = true
                        )
                        
                        ShadcnCombobox(
                            options = locations,
                            selectedOption = selectedLocation,
                            onOptionSelected = { selectedLocation = it },
                            onValueChange = { /* Handle typing */ },
                            label = "Destination (Type to search)",
                            placeholder = "Type location name...",
                            helper = "Start typing to search locations"
                        )
                    }
                }

                // Alerts Section
                ShadcnSection(
                    title = "Alerts & Notifications",
                    description = "Status messages and notifications"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        ShadcnAlert(
                            title = "Success",
                            description = "Your delivery has been scheduled successfully! The driver will contact you shortly.",
                            variant = ShadcnAlertVariant.Success,
                            dismissible = true,
                            onDismiss = { }
                        )
                        
                        ShadcnAlert(
                            title = "Warning",
                            description = "Traffic conditions may affect delivery time. Expected delay: 10-15 minutes.",
                            variant = ShadcnAlertVariant.Warning,
                            dismissible = true,
                            onDismiss = { }
                        )
                        
                        ShadcnAlert(
                            title = "Error",
                            description = "Unable to connect to driver. Please check your internet connection and try again.",
                            variant = ShadcnAlertVariant.Destructive,
                            dismissible = true,
                            onDismiss = { },
                            action = {
                                ShadcnTextButton(
                                    text = "Retry",
                                    onClick = { },
                                    variant = ShadcnButtonVariant.Outline,
                                    size = ShadcnButtonSize.Small
                                )
                            }
                        )
                        
                        ShadcnBanner(
                            message = "New delivery zones now available in Tema and Kumasi!",
                            variant = ShadcnAlertVariant.Info,
                            icon = Icons.Default.Info,
                            dismissible = true,
                            onDismiss = { },
                            action = {
                                ShadcnTextButton(
                                    text = "Learn More",
                                    onClick = { },
                                    variant = ShadcnButtonVariant.Link,
                                    size = ShadcnButtonSize.Small
                                )
                            }
                        )
                        
                        // Inline alerts
                        Column(
                            verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.xs)
                        ) {
                            ShadcnInlineAlert(
                                message = "Please select a pickup location",
                                variant = ShadcnAlertVariant.Destructive
                            )
                            ShadcnInlineAlert(
                                message = "Driver has been notified of your request",
                                variant = ShadcnAlertVariant.Success
                            )
                            ShadcnInlineAlert(
                                message = "Estimated delivery time: 30-45 minutes",
                                variant = ShadcnAlertVariant.Info
                            )
                        }
                        
                        // Toast trigger button
                        ShadcnTextButton(
                            text = "Show Toast Notification",
                            onClick = { showToast = true },
                            variant = ShadcnButtonVariant.Outline
                        )
                    }
                }

                // Cards Section
                ShadcnSection(
                    title = "Cards & Containers",
                    description = "Content containers and layouts"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                        ) {
                            ShadcnCard(
                                variant = ShadcnCardVariant.Default,
                                modifier = Modifier.weight(1f)
                            ) {
                                ShadcnText(text = "Default Card", style = ShadcnTextStyle.Small)
                                ShadcnMutedText(text = "With border and shadow")
                            }
                            
                            ShadcnCard(
                                variant = ShadcnCardVariant.Elevated,
                                modifier = Modifier.weight(1f)
                            ) {
                                ShadcnText(text = "Elevated Card", style = ShadcnTextStyle.Small)
                                ShadcnMutedText(text = "With larger shadow")
                            }
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                        ) {
                            ShadcnCard(
                                variant = ShadcnCardVariant.Outlined,
                                modifier = Modifier.weight(1f)
                            ) {
                                ShadcnText(text = "Outlined Card", style = ShadcnTextStyle.Small)
                                ShadcnMutedText(text = "With strong border")
                            }
                            
                            ShadcnCard(
                                variant = ShadcnCardVariant.Ghost,
                                modifier = Modifier.weight(1f)
                            ) {
                                ShadcnText(text = "Ghost Card", style = ShadcnTextStyle.Small)
                                ShadcnMutedText(text = "Minimal styling")
                            }
                        }
                    }
                }

                // Spacer at the bottom
                Spacer(modifier = Modifier.height(ShadcnSpacing.xl))
            }

            // Toast overlay
            if (showToast) {
                ShadcnToast(
                    message = "This is a toast notification! It will disappear automatically.",
                    variant = ShadcnAlertVariant.Success,
                    duration = 3000L,
                    onDismiss = { showToast = false },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(ShadcnSpacing.md)
                )
            }
        }
    }
}
