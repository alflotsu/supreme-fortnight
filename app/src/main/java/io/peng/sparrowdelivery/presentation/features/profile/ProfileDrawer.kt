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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileDrawer(
    onDismiss: () -> Unit,
    onViewProfileClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Header
                ProfileHeader(
                    userProfile = uiState.userProfile,
                    onViewProfileClick = {
                        onDismiss()
                        onViewProfileClick()
                    }
                )

                HorizontalDivider()

                // Quick Actions
                QuickActions(
                    onOrderHistoryClick = { /* TODO: Navigate to order history */ },
                    onSavedAddressesClick = { /* TODO: Navigate to addresses */ },
                    onPaymentMethodsClick = { /* TODO: Navigate to payment */ },
                    onNotificationsClick = { /* TODO: Navigate to notifications */ }
                )
                
                HorizontalDivider()
                
                // App Actions
                AppActions(
                    onHelpClick = { /* TODO: Navigate to help */ },
                    onSettingsClick = { /* TODO: Navigate to settings */ },
                    onLogoutClick = viewModel::showLogoutDialog
                )
            }
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
                        onDismiss()
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
}

@Composable
private fun ProfileHeader(
    userProfile: UserProfile,
    onViewProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewProfileClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userProfile.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Profile Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userProfile.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = userProfile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${userProfile.totalDeliveries} deliveries",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
        
        Icon(
            Icons.Default.ArrowForward,
            contentDescription = "View Profile",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickActions(
    onOrderHistoryClick: () -> Unit,
    onSavedAddressesClick: () -> Unit,
    onPaymentMethodsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        DrawerMenuItem(
            icon = Icons.Default.DateRange,
            title = "Order History",
            onClick = onOrderHistoryClick
        )
        
        DrawerMenuItem(
            icon = Icons.Default.LocationOn,
            title = "Saved Addresses",
            onClick = onSavedAddressesClick
        )
        
        DrawerMenuItem(
            icon = Icons.Default.AccountBox,
            title = "Payment Methods",
            onClick = onPaymentMethodsClick
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            onClick = onNotificationsClick
        )
    }
}

@Composable
private fun AppActions(
    onHelpClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "App",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Info,
            title = "Help & Support",
            onClick = onHelpClick
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            onClick = onSettingsClick
        )
        
        DrawerMenuItem(
            icon = Icons.Default.ExitToApp,
            title = "Logout",
            onClick = onLogoutClick,
            textColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}
