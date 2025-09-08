package io.peng.sparrowdelivery.presentation.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*
import java.text.NumberFormat
import java.util.*

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onOrderClick: (Order) -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    ShadcnTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShadcnTheme.colors.background)
        ) {
            // Custom Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ShadcnSpacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                ) {
                    ShadcnIconButton(
                        icon = Icons.Default.ArrowBack,
                        onClick = onBackClick,
                        contentDescription = "Back"
                    )
                    ShadcnHeading(
                        text = "Profile",
                        level = 3
                    )
                }
                
                ShadcnIconButton(
                    icon = if (uiState.isEditing) Icons.Default.Check else Icons.Default.Edit,
                    onClick = { 
                        if (uiState.isEditing) {
                            viewModel.saveProfile()
                        } else {
                            viewModel.toggleEditMode()
                        }
                    },
                    contentDescription = if (uiState.isEditing) "Save" else "Edit",
                    variant = ShadcnButtonVariant.Outline
                )
            }
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ShadcnSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.lg)
            ) {
            // Profile Header
            ProfileHeaderSection(
                userProfile = uiState.userProfile,
                isEditing = uiState.isEditing,
                onNameChange = viewModel::updateName,
                onEmailChange = viewModel::updateEmail,
                onPhoneChange = viewModel::updatePhone
            )
            
            // Personal Information
            PersonalInformationSection(
                userProfile = uiState.userProfile,
                isEditing = uiState.isEditing,
                onAddressChange = viewModel::updateAddress
            )
            
            // Delivery Stats
            DeliveryStatsSection(
                userProfile = uiState.userProfile,
                onPaymentMethodClick = viewModel::showPaymentMethodsDialog
            )
            
            // Order History
            OrderHistorySection(
                orders = uiState.userProfile.orderHistory,
                onOrderClick = { order ->
                    selectedOrder = order
                }
            )
            
            // Saved Addresses
            SavedAddressesSection(addresses = uiState.userProfile.savedAddresses)
            
            // Notification Preferences
            NotificationPreferencesSection(
                notifications = uiState.userProfile.deliveryPreferences.notifications,
                onNotificationChange = viewModel::updateNotificationPreference
            )
            
                // Account Actions
                AccountActionsSection(
                    onLogoutClick = viewModel::showLogoutDialog
                )
            }
        }
    
    // Logout Confirmation Dialog
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = viewModel::hideLogoutDialog,
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        onBackClick() // Navigate back after logout
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideLogoutDialog) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Payment Methods Dialog
    if (uiState.showPaymentMethodsDialog) {
        PaymentMethodsDialog(
            paymentMethods = uiState.userProfile.paymentMethods,
            onDismiss = viewModel::hidePaymentMethodsDialog,
            onPaymentMethodSelect = viewModel::selectPaymentMethod,
            onAddNewPaymentMethod = {
                // TODO: Navigate to add payment method screen
                viewModel.hidePaymentMethodsDialog()
            }
        )
    }
    
    // Order Detail Modal
    selectedOrder?.let { order ->
        OrderDetailScreen(
            order = order,
            onBackClick = { selectedOrder = null }
        )
    }
    }
}

@Composable
private fun PaymentMethodsDialog(
    paymentMethods: List<PaymentMethod>,
    onDismiss: () -> Unit,
    onPaymentMethodSelect: (PaymentMethod) -> Unit,
    onAddNewPaymentMethod: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        ShadcnCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(ShadcnSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.xs)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShadcnText(
                        text = "💳 Payment Methods",
                        style = ShadcnTextStyle.H4
                    )
                    ShadcnIconButton(
                        icon = Icons.Default.Close,
                        onClick = onDismiss,
                        variant = ShadcnButtonVariant.Ghost,
                        contentDescription = "Close"
                    )
                }
                
                // Payment Methods List
                Column(
                    verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                ) {
                    paymentMethods.forEach { paymentMethod ->
                        PaymentMethodItem(
                            paymentMethod = paymentMethod,
                            onClick = { onPaymentMethodSelect(paymentMethod) }
                        )
                    }
                }
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = ShadcnSpacing.sm)
                )
                
                // Add New Payment Method Button
                ShadcnTextButton(
                    text = "+ Add New Payment Method",
                    onClick = onAddNewPaymentMethod,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ShadcnButtonVariant.Outline
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodItem(
    paymentMethod: PaymentMethod,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ShadcnBorderRadius.md))
            .clickable { onClick() }
            .background(
                if (paymentMethod.isDefault) 
                    ShadcnTheme.colors.primary.copy(alpha = 0.1f) 
                else Color.Transparent
            )
            .padding(horizontal = ShadcnSpacing.md, vertical = ShadcnSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Payment Method Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(ShadcnBorderRadius.sm))
                .background(ShadcnTheme.colors.muted),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (paymentMethod.type) {
                    PaymentMethodType.CREDIT_CARD -> "💳"
                    PaymentMethodType.DEBIT_CARD -> "💳"
                    PaymentMethodType.PAYPAL -> "📱"
                    PaymentMethodType.APPLE_PAY -> "🍎"
                    PaymentMethodType.GOOGLE_PAY -> "🎯"
                    PaymentMethodType.BANK_ACCOUNT -> "🏦"
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        // Payment Method Details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = paymentMethod.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = ShadcnTheme.colors.foreground
            )
            
            paymentMethod.expiryDate?.let { expiry ->
                Text(
                    text = "Expires $expiry",
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadcnTheme.colors.mutedForeground
                )
            }
        }
        
        // Default Badge or Selection Indicator
        if (paymentMethod.isDefault) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ShadcnTheme.colors.primary
            ) {
                Text(
                    text = "Default",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = ShadcnTheme.colors.primaryForeground,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Select payment method",
                tint = ShadcnTheme.colors.mutedForeground.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun ProfileHeaderSection(
    userProfile: UserProfile,
    isEditing: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadcnTheme.colors.card
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Profile Information
            if (isEditing) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = userProfile.name,
                        onValueChange = onNameChange,
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = userProfile.email,
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = userProfile.phone,
                        onValueChange = onPhoneChange,
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ShadcnTheme.colors.foreground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = userProfile.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = ShadcnTheme.colors.mutedForeground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = userProfile.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShadcnTheme.colors.mutedForeground,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = "Member since ${userProfile.memberSince}",
                style = MaterialTheme.typography.bodySmall,
                color = ShadcnTheme.colors.mutedForeground
            )
        }
    }
}

@Composable
private fun OrderHistorySection(
    orders: List<Order>,
    onOrderClick: (Order) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadcnTheme.colors.muted
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 Order History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (orders.size > 3) {
                    TextButton(onClick = { /* TODO: Navigate to full order history */ }) {
                        Text("View All")
                    }
                }
            }

            if (orders.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📦",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No orders yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ShadcnTheme.colors.mutedForeground
                    )
                    Text(
                        text = "Start ordering to see your history",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShadcnTheme.colors.mutedForeground
                    )
                }
            } else {
                orders.take(3).forEach { order ->
                    OrderHistoryItem(
                        order = order,
                        onClick = { onOrderClick(order) }
                    )
                    if (order != orders.take(3).last()) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryItem(
    order: Order,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(
                    when (order.status) {
                        OrderStatus.PENDING -> MaterialTheme.colorScheme.primary
                        OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
                        OrderStatus.PICKED_UP -> MaterialTheme.colorScheme.tertiary
                        OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.secondary
                        OrderStatus.DELIVERED -> Success
                        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                        OrderStatus.REFUNDED -> MaterialTheme.colorScheme.error
                    }
                )
        )

        // Order Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "#${order.orderNumber}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = order.items.joinToString { "${it.quantity}× ${it.name}" }.take(40) +
                       if (order.items.joinToString { "${it.quantity}× ${it.name}" }.length > 40) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = ShadcnTheme.colors.mutedForeground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = order.formattedOrderDate,
                style = MaterialTheme.typography.bodySmall,
                color = ShadcnTheme.colors.mutedForeground
            )
        }

        // Status and Price
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (order.status) {
                    OrderStatus.PENDING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    OrderStatus.PICKED_UP -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    OrderStatus.DELIVERED -> Success.copy(alpha = 0.2f)
                    OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    OrderStatus.REFUNDED -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                }
            ) {
                Text(
                    text = order.status.displayName,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (order.status) {
                        OrderStatus.PENDING -> MaterialTheme.colorScheme.primary
                        OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
                        OrderStatus.PICKED_UP -> MaterialTheme.colorScheme.tertiary
                        OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.secondary
                        OrderStatus.DELIVERED -> Success
                        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                        OrderStatus.REFUNDED -> MaterialTheme.colorScheme.error
                    },
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.US).format(order.totalWithFees),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Navigation Arrow
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = "View Details",
            tint = ShadcnTheme.colors.mutedForeground
        )
    }
}

@Composable
private fun PersonalInformationSection(
    userProfile: UserProfile,
    isEditing: Boolean,
    onAddressChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadcnTheme.colors.muted
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📍 Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (isEditing) {
                OutlinedTextField(
                    value = userProfile.address,
                    onValueChange = onAddressChange,
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            } else {
                Text(
                    text = userProfile.address,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun DeliveryStatsSection(
    userProfile: UserProfile,
    onPaymentMethodClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadcnTheme.colors.muted
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📊 Delivery Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = "📦",
                    value = userProfile.totalDeliveries.toString(),
                    label = "Total Deliveries"
                )
                StatItem(
                    icon = "💳",
                    value = userProfile.preferredPaymentMethod,
                    label = "Payment Method",
                    onClick = onPaymentMethodClick
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    value: String,
    label: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onClick() }
                        .padding(8.dp)
                } else Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = ShadcnTheme.colors.mutedForeground
        )
    }
}

@Composable
private fun SavedAddressesSection(addresses: List<SavedAddress>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadcnTheme.colors.muted
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏠 Saved Addresses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { /* TODO: Navigate to manage addresses */ }) {
                    Text("Manage")
                }
            }

            addresses.take(3).forEach { address ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = address.label + if (address.isDefault) " (Default)" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (address.isDefault) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = address.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = ShadcnTheme.colors.mutedForeground
                        )
                    }
                }
                if (address != addresses.last()) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun NotificationPreferencesSection(
    notifications: NotificationPreferences,
    onNotificationChange: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadcnTheme.colors.muted
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🔔 Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            NotificationToggle(
                title = "Order Updates",
                description = "Get notified about order status changes",
                checked = notifications.orderUpdates,
                onCheckedChange = { onNotificationChange("orderUpdates", it) }
            )

            NotificationToggle(
                title = "Driver Location",
                description = "Track your driver's real-time location",
                checked = notifications.driverLocation,
                onCheckedChange = { onNotificationChange("driverLocation", it) }
            )

            NotificationToggle(
                title = "Promotions",
                description = "Receive offers and promotional content",
                checked = notifications.promotions,
                onCheckedChange = { onNotificationChange("promotions", it) }
            )

            NotificationToggle(
                title = "Delivery Confirmation",
                description = "Get notified when delivery is completed",
                checked = notifications.deliveryConfirmation,
                onCheckedChange = { onNotificationChange("deliveryConfirmation", it) }
            )
        }
    }
}

@Composable
private fun NotificationToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = ShadcnTheme.colors.mutedForeground
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun AccountActionsSection(
    onLogoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadcnTheme.colors.muted
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚙️ Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}
