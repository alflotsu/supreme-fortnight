package io.peng.sparrowdelivery.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.StitchTheme
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.components.TranslucentCard

/**
 * Showcase screen for the new Stitch design system
 * This demonstrates all the beautiful Stitch components and colors
 */
@Composable
fun StitchShowcaseScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(stitchColors.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = stitchColors.onBackground
                    )
                }
                Text(
                    text = "Stitch Design System",
                    style = MaterialTheme.typography.headlineLarge,
                    color = stitchColors.onBackground
                )
            }
            
            // Color Palette Section
            Text(
                text = "Color Palette",
                style = MaterialTheme.typography.titleLarge,
                color = stitchColors.onBackground
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ColorPaletteCard("Primary Red", stitchColors.primary, Modifier.weight(1f))
                ColorPaletteCard("Success Green", stitchColors.accent, Modifier.weight(1f))
                ColorPaletteCard("Cream", stitchColors.primaryContainer, Modifier.weight(1f))
            }
            
            // Typography Section  
            Text(
                text = "Typography (Spline Sans)",
                style = MaterialTheme.typography.titleLarge,
                color = stitchColors.onBackground
            )
            
            TranslucentCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Reliable delivery at your fingertips",
                        style = MaterialTheme.typography.headlineLarge,
                        color = stitchColors.onSurface
                    )
                    Text(
                        text = "From small parcels to large packages, get it delivered swiftly and securely.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = stitchColors.textSecondary
                    )
                    Text(
                        text = "Beautiful Spline Sans typography",
                        style = MaterialTheme.typography.bodyMedium,
                        color = stitchColors.textMuted
                    )
                }
            }
            
            // Button Components
            Text(
                text = "Button Components",
                style = MaterialTheme.typography.titleLarge,
                color = stitchColors.onBackground
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StitchPrimaryButton(
                    onClick = { },
                    text = "Request Delivery",
                    icon = Icons.Outlined.LocalShipping,
                    modifier = Modifier.fillMaxWidth()
                )
                
                StitchSecondaryButton(
                    onClick = { },
                    text = "Log in",
                    modifier = Modifier.fillMaxWidth()
                )
                
                StitchSuccessButton(
                    onClick = { },
                    text = "Confirm Booking",
                    icon = Icons.Outlined.Check,
                    modifier = Modifier.fillMaxWidth()
                )
                
                StitchOutlineButton(
                    onClick = { },
                    text = "Cancel",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Icon Buttons
            Text(
                text = "Icon Buttons",
                style = MaterialTheme.typography.titleLarge,
                color = stitchColors.onBackground
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StitchIconButton(
                    onClick = { },
                    icon = Icons.Outlined.Call,
                    variant = StitchIconButtonVariant.Primary,
                    contentDescription = "Call driver"
                )
                StitchIconButton(
                    onClick = { },
                    icon = Icons.Outlined.Message,
                    variant = StitchIconButtonVariant.Secondary,
                    contentDescription = "Chat with driver"
                )
                StitchIconButton(
                    onClick = { },
                    icon = Icons.Outlined.Navigation,
                    variant = StitchIconButtonVariant.Success,
                    contentDescription = "Navigate"
                )
                
                Text(
                    text = "Call, Chat, Navigate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = stitchColors.textSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Input Fields
            Text(
                text = "Input Components",
                style = MaterialTheme.typography.titleLarge,
                color = stitchColors.onBackground
            )
            
            var searchValue by remember { mutableStateOf("") }
            var pickupValue by remember { mutableStateOf("") }
            var dropoffValue by remember { mutableStateOf("") }
            var packageSize by remember { mutableStateOf(PackageSize.Medium) }
            var schedule by remember { mutableStateOf(DeliverySchedule.Now) }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StitchSearchField(
                    value = searchValue,
                    onValueChange = { searchValue = it },
                    placeholder = "Search for location"
                )
                
                StitchLocationField(
                    value = pickupValue,
                    onValueChange = { pickupValue = it },
                    placeholder = "Pickup location",
                    label = "From",
                    isPickup = true
                )
                
                StitchLocationField(
                    value = dropoffValue,
                    onValueChange = { dropoffValue = it },
                    placeholder = "Delivery location",
                    label = "To",
                    isPickup = false
                )
                
                StitchPackageSizeSelector(
                    selectedSize = packageSize,
                    onSizeSelected = { packageSize = it }
                )
                
                StitchScheduleSelector(
                    selectedSchedule = schedule,
                    onScheduleSelected = { schedule = it }
                )
            }
            
            // Loading States
            Text(
                text = "Loading States",
                style = MaterialTheme.typography.titleLarge,
                color = stitchColors.onBackground
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StitchPrimaryButton(
                    onClick = { },
                    text = "Finding Driver...",
                    loading = true,
                    modifier = Modifier.weight(1f)
                )
                
                StitchSecondaryButton(
                    onClick = { },
                    text = "Loading...",
                    loading = true,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Reference Note
            TranslucentCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "✨ Stitch Design System",
                        style = MaterialTheme.typography.titleMedium,
                        color = stitchColors.onSurface
                    )
                    Text(
                        text = "Based on the beautiful HTML designs in styles/reference/. Features Spline Sans typography, distinctive red/green color palette, and ultra-thin translucent overlays perfect for map-based UIs.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = stitchColors.textSecondary
                    )
                    Text(
                        text = "Perfect for African delivery markets! 🚚",
                        style = MaterialTheme.typography.bodySmall,
                        color = stitchColors.textMuted
                    )
                }
            }
            
            // Bottom spacing
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ColorPaletteCard(
    name: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = if (color == stitchColors.primaryContainer) stitchColors.onPrimaryContainer else androidx.compose.ui.graphics.Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StitchShowcaseScreenPreview() {
    StitchShowcaseScreen()
}
