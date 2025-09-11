package io.peng.sparrowdelivery.presentation.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.*

/**
 * Modern ProfileScreen using pure Stitch Design System
 * Matches the HTML reference designs with dark/light mode support
 */
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onOrderClick: (Order) -> Unit = {}
) {
    // Simple hardcoded data for the profile
    val userProfile = UserProfile(
        name = "John Doe",
        email = "john@example.com",
        phone = "+1 234 567 8900",
        totalDeliveries = 42,
        memberSince = "Jan 2024",
        address = "123 Main St, City, State",
        preferredPaymentMethod = "Visa ****1234"
    )
    
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(stitchColors.background)
        ) {
            // Header with back button and title
            ProfileHeader(
                onBackClick = onBackClick
            )
            
            // Main content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // User profile info
                item {
                    ProfileInfo(
                        userProfile = userProfile
                    )
                }
                
                // Payment method
                item {
                    PaymentSection()
                }
                
                // Settings menu
                item {
                    SettingsMenu()
                }
                
                // Logout button
                item {
                    LogoutButton(
                        onLogoutClick = { /* Handle logout */ }
                    )
                }
                
                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    onBackClick: () -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = stitchColors.background.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StitchIconButton(
                icon = Icons.Filled.ArrowBack,
                onClick = onBackClick,
                variant = StitchIconButtonVariant.Secondary
            )
            
            StitchHeading(
                text = "Profile",
                level = 2,
                textAlign = TextAlign.Center
            )
            
            // Balance the layout
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
private fun ProfileInfo(
    userProfile: UserProfile
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(stitchColors.primary),
                contentAlignment = Alignment.Center
            ) {
                StitchText(
                    text = userProfile.name
                        .split(" ")
                        .mapNotNull { it.firstOrNull() }
                        .take(2)
                        .joinToString(""),
                    style = MaterialTheme.typography.headlineLarge,
                    color = stitchColors.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Name and details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StitchHeading(
                    text = userProfile.name,
                    level = 3,
                    textAlign = TextAlign.Center
                )
                
                StitchText(
                    text = userProfile.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = stitchColors.textSecondary,
                    textAlign = TextAlign.Center
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = stitchColors.accent,
                        modifier = Modifier.size(20.dp)
                    )
                    StitchText(
                        text = "4.8 rating • ${userProfile.totalDeliveries} deliveries",
                        style = MaterialTheme.typography.bodyMedium,
                        color = stitchColors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSection() {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StitchHeading(
            text = "Payment",
            level = 4,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        StitchCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle payment click */ }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Payment icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(stitchColors.accent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = stitchColors.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Payment info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StitchText(
                        text = "Payment method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    StitchText(
                        text = "Visa •••• 4242",
                        style = MaterialTheme.typography.bodyMedium,
                        color = stitchColors.textSecondary
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = stitchColors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun SettingsMenu() {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StitchHeading(
            text = "Settings",
            level = 4,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        StitchCard {
            Column {
                SettingsMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    onClick = { /* Handle notifications */ }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(start = 80.dp),
                    color = stitchColors.outline
                )
                
                SettingsMenuItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = "English",
                    onClick = { /* Handle language */ }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(start = 80.dp),
                    color = stitchColors.outline
                )
                
                SettingsMenuItem(
                    icon = Icons.Default.Security,
                    title = "Privacy & Security",
                    onClick = { /* Handle privacy */ }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(start = 80.dp),
                    color = stitchColors.outline
                )
                
                SettingsMenuItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    onClick = { /* Handle help */ }
                )
            }
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(stitchColors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = stitchColors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Title and subtitle
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StitchText(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            subtitle?.let {
                StitchText(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stitchColors.textSecondary
                )
            }
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = stitchColors.textSecondary
        )
    }
}

@Composable
private fun LogoutButton(
    onLogoutClick: () -> Unit
) {
    StitchOutlineButton(
        text = "Log Out",
        onClick = onLogoutClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        icon = Icons.Default.ExitToApp
    )
}

// Use existing data classes from the project
