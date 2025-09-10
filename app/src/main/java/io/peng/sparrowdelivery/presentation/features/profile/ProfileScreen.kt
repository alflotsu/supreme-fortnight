package io.peng.sparrowdelivery.presentation.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.*
import java.text.NumberFormat
import java.util.*

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onOrderClick: (Order) -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(stitchColors.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = stitchColors.onSurface
                        )
                    }
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        color = stitchColors.onSurface
                    )
                }
            }
            
            // Placeholder content
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = stitchColors.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Profile Screen",
                        style = MaterialTheme.typography.headlineSmall,
                        color = stitchColors.onSurface
                    )
                    Text(
                        text = "TODO: Migrate to Stitch Design System",
                        style = MaterialTheme.typography.bodyMedium,
                        color = stitchColors.textSecondary
                    )
                    
                    StitchPrimaryButton(
                        text = "Coming Soon",
                        onClick = { /* TODO */ }
                    )
                }
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
            .clip(RoundedCornerShape(SparrowBorderRadius.md))
            .clickable { onClick() }
            .background(
                if (paymentMethod.isDefault) 
                    SparrowTheme.colors.primary.copy(alpha = 0.1f) 
                else Color.Transparent
            )
            .padding(horizontal = SparrowSpacing.md, vertical = SparrowSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Payment Method Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(SparrowBorderRadius.sm))
                .background(SparrowTheme.colors.muted),
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
                color = SparrowTheme.colors.foreground
            )
            
            paymentMethod.expiryDate?.let { expiry ->
                Text(
                    text = "Expires $expiry",
                    style = MaterialTheme.typography.bodySmall,
                    color = SparrowTheme.colors.mutedForeground
                )
            }
        }
        
        // Default Badge or Selection Indicator
        if (paymentMethod.isDefault) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SparrowTheme.colors.primary
            ) {
                Text(
                    text = "Default",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = SparrowTheme.colors.primaryForeground,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Select payment method",
                tint = SparrowTheme.colors.mutedForeground.copy(alpha = 0.3f)
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
    SparrowCard(
        modifier = Modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Default
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
                    .background(SparrowTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                    color = LocalStitchColorScheme.current.onPrimary,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            // Profile Information
            if (isEditing) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShadcnTextField(
                        value = userProfile.name,
                        onValueChange = onNameChange,
                        label = "Full Name",
                        modifier = Modifier.fillMaxWidth()
                    )
                    ShadcnTextField(
                        value = userProfile.email,
                        onValueChange = onEmailChange,
                        label = "Email",
                        modifier = Modifier.fillMaxWidth()
                    )
                    ShadcnTextField(
                        value = userProfile.phone,
                        onValueChange = onPhoneChange,
                        label = "Phone",
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
                        style = MaterialTheme.typography.headlineMedium,
                        color = LocalStitchColorScheme.current.onSurface,
                        textAlign = TextAlign.Center
                    )
                    ShadcnText(
                        text = userProfile.email,
                        style = ShadcnTextStyle.P,
                        color = SparrowTheme.colors.mutedForeground,
                        textAlign = TextAlign.Center
                    )
                    ShadcnText(
                        text = userProfile.phone,
                        style = ShadcnTextStyle.Small,
                        color = SparrowTheme.colors.mutedForeground,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ShadcnText(
                text = "Member since ${userProfile.memberSince}",
                style = ShadcnTextStyle.Small,
                color = SparrowTheme.colors.mutedForeground
            )
        }
    }
}

@Composable
private fun OrderHistorySection(
    orders: List<Order>,
    onOrderClick: (Order) -> Unit
) {
    SparrowCard(
        modifier = Modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Default
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
                    style = MaterialTheme.typography.headlineSmall,
                    color = LocalStitchColorScheme.current.onSurface
                )
                if (orders.size > 3) {
                    SparrowTextButton(
                        text = "View All",
                        onClick = { /* TODO: Navigate to full order history */ }
                    )
                }
            }

            if (orders.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ShadcnText(
                        text = "📦",
                        style = ShadcnTextStyle.H1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ShadcnText(
                        text = "No orders yet",
                        style = ShadcnTextStyle.P,
                        color = SparrowTheme.colors.mutedForeground
                    )
                    ShadcnText(
                        text = "Start ordering to see your history",
                        style = ShadcnTextStyle.Small,
                        color = SparrowTheme.colors.mutedForeground
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
                        OrderStatus.PENDING -> SparrowTheme.colors.primary
                        OrderStatus.CONFIRMED -> SparrowTheme.colors.primary
                        OrderStatus.PICKED_UP -> SparrowTheme.colors.primary
                        OrderStatus.IN_TRANSIT -> SparrowTheme.colors.secondary
                        OrderStatus.DELIVERED -> Success
                        OrderStatus.CANCELLED -> SparrowTheme.colors.destructive
                        OrderStatus.REFUNDED -> SparrowTheme.colors.destructive
                    }
                )
        )

        // Order Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ShadcnText(
                text = "#${order.orderNumber}",
                style = ShadcnTextStyle.P
            )
            ShadcnText(
                text = order.items.joinToString { "${it.quantity}× ${it.name}" }.take(40) +
                       if (order.items.joinToString { "${it.quantity}× ${it.name}" }.length > 40) "..." else "",
                style = ShadcnTextStyle.Small,
                color = SparrowTheme.colors.mutedForeground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ShadcnText(
                text = order.formattedOrderDate,
                style = ShadcnTextStyle.Small,
                color = SparrowTheme.colors.mutedForeground
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
                    OrderStatus.PENDING -> SparrowTheme.colors.primary.copy(alpha = 0.2f)
                    OrderStatus.CONFIRMED -> SparrowTheme.colors.primary.copy(alpha = 0.2f)
                    OrderStatus.PICKED_UP -> SparrowTheme.colors.primary.copy(alpha = 0.2f)
                    OrderStatus.IN_TRANSIT -> SparrowTheme.colors.secondary.copy(alpha = 0.2f)
                    OrderStatus.DELIVERED -> Success.copy(alpha = 0.2f)
                    OrderStatus.CANCELLED -> SparrowTheme.colors.destructive.copy(alpha = 0.2f)
                    OrderStatus.REFUNDED -> SparrowTheme.colors.destructive.copy(alpha = 0.2f)
                }
            ) {
                ShadcnText(
                    text = order.status.displayName,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = ShadcnTextStyle.Small,
                    color = when (order.status) {
                        OrderStatus.PENDING -> SparrowTheme.colors.primary
                        OrderStatus.CONFIRMED -> SparrowTheme.colors.primary
                        OrderStatus.PICKED_UP -> SparrowTheme.colors.primary
                        OrderStatus.IN_TRANSIT -> SparrowTheme.colors.secondary
                        OrderStatus.DELIVERED -> Success
                        OrderStatus.CANCELLED -> SparrowTheme.colors.destructive
                        OrderStatus.REFUNDED -> SparrowTheme.colors.destructive
                    }
                )
            }
            ShadcnText(
                text = NumberFormat.getCurrencyInstance(Locale.US).format(order.totalWithFees),
                style = ShadcnTextStyle.P
            )
        }

        // Navigation Arrow
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = "View Details",
            tint = SparrowTheme.colors.mutedForeground
        )
    }
}

@Composable
private fun PersonalInformationSection(
    userProfile: UserProfile,
    isEditing: Boolean,
    onAddressChange: (String) -> Unit
) {
    SparrowCard(
        modifier = Modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Default
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShadcnText(
                text = "📍 Address",
                style = ShadcnTextStyle.H4
            )

            if (isEditing) {
                ShadcnTextField(
                    value = userProfile.address,
                    onValueChange = onAddressChange,
                    label = "Address",
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            } else {
                ShadcnText(
                    text = userProfile.address,
                    style = ShadcnTextStyle.P
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
    SparrowCard(
        modifier = Modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Default
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShadcnText(
                text = "📊 Delivery Stats",
                style = ShadcnTextStyle.H4
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
        ShadcnText(
            text = icon,
            style = ShadcnTextStyle.H3
        )
        ShadcnText(
            text = value,
            style = ShadcnTextStyle.P
        )
        ShadcnText(
            text = label,
            style = ShadcnTextStyle.Small,
            color = SparrowTheme.colors.mutedForeground
        )
    }
}

@Composable
private fun SavedAddressesSection(addresses: List<SavedAddress>) {
    SparrowCard(
        modifier = Modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Default
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
                ShadcnText(
                    text = "🏠 Saved Addresses",
                    style = ShadcnTextStyle.H4
                )
                SparrowTextButton(
                    text = "Manage",
                    onClick = { /* TODO: Navigate to manage addresses */ }
                )
            }

            addresses.take(3).forEach { address ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        ShadcnText(
                            text = address.label + if (address.isDefault) " (Default)" else "",
                            style = ShadcnTextStyle.P
                        )
                        ShadcnText(
                            text = address.address,
                            style = ShadcnTextStyle.Small,
                            color = SparrowTheme.colors.mutedForeground
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
    SparrowCard(
        modifier = Modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Default
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShadcnText(
                text = "🔔 Notifications",
                style = ShadcnTextStyle.H4
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
            ShadcnText(
                text = title,
                style = ShadcnTextStyle.P
            )
            ShadcnText(
                text = description,
                style = ShadcnTextStyle.Small,
                color = SparrowTheme.colors.mutedForeground
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
    SparrowCard(
        modifier = Modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Default
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShadcnText(
                text = "⚙️ Account",
                style = ShadcnTextStyle.H4
            )
            
            ShadcnButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ShadcnButtonVariant.Destructive
                ) {
                    Text(text = "Logout")
                }
        }
    }
}
