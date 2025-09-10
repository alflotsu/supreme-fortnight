package io.peng.sparrowdelivery.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*

@Composable
fun LocationSetupScreen(
    onLocationSet: (address: String, label: String) -> Unit,
    onSkip: () -> Unit
) {
    var selectedAddress by remember { mutableStateOf("") }
    var selectedLabel by remember { mutableStateOf("Home") }
    var isAddressValid by remember { mutableStateOf(false) }
    var showAddressInput by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        showAddressInput = true
    }
    
    SparrowTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SparrowSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(SparrowSpacing.xxl))
            
            // Header section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Location icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = SparrowTheme.colors.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = SparrowTheme.colors.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(SparrowSpacing.xl))
                
                ShadcnHeading(
                    text = "Set Your Default Location",
                    level = 1,
                    color = SparrowTheme.colors.foreground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(SparrowSpacing.md))
                
                ShadcnText(
                    text = "We'll use this as your preferred delivery address. You can always change it later!",
                    style = ShadcnTextStyle.Large,
                    color = SparrowTheme.colors.mutedForeground,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(SparrowSpacing.xxl))
            
            // Address input section
            AnimatedVisibility(
                visible = showAddressInput,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Address input field
                    SparrowInput(
                        value = selectedAddress,
                        onValueChange = { 
                            selectedAddress = it
                            isAddressValid = it.isNotBlank() && it.length > 5
                        },
                        label = "Delivery Address",
                        placeholder = "Enter your address",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = Icons.Default.LocationOn
                    )
                    
                    Spacer(modifier = Modifier.height(SparrowSpacing.lg))
                    
                    // Address label selection
                    if (isAddressValid) {
                        Column {
                            ShadcnText(
                                text = "Label this address as:",
                                style = ShadcnTextStyle.Small,
                                color = SparrowTheme.colors.mutedForeground
                            )
                            
                            Spacer(modifier = Modifier.height(SparrowSpacing.sm))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.sm)
                            ) {
                                AddressLabelChip(
                                    label = "Home",
                                    isSelected = selectedLabel == "Home",
                                    onClick = { selectedLabel = "Home" }
                                )
                                AddressLabelChip(
                                    label = "Work",
                                    isSelected = selectedLabel == "Work", 
                                    onClick = { selectedLabel = "Work" }
                                )
                                AddressLabelChip(
                                    label = "Other",
                                    isSelected = selectedLabel == "Other",
                                    onClick = { selectedLabel = "Other" }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(SparrowSpacing.md))
                    
                    // Current location button
                    SparrowTextButton(
                        text = "📍 Use Current Location",
                        onClick = {
                            // TODO: Implement current location detection
                            selectedAddress = "Current location (detected)"
                            isAddressValid = true
                        },
                        variant = ShadcnButtonVariant.Outline,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = Icons.Default.MyLocation
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom buttons
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Set location button
                SparrowTextButton(
                    text = "Set as Default Location",
                    onClick = { 
                        if (isAddressValid) {
                            onLocationSet(selectedAddress, selectedLabel)
                        }
                    },
                    variant = ShadcnButtonVariant.Default,
                    size = ShadcnButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isAddressValid
                )
                
                Spacer(modifier = Modifier.height(SparrowSpacing.md))
                
                // Skip button
                SparrowTextButton(
                    text = "Skip for now",
                    onClick = onSkip,
                    variant = ShadcnButtonVariant.Ghost,
                    size = ShadcnButtonSize.Large,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(SparrowSpacing.md))
        }
    }
}

@Composable
private fun AddressLabelChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    SparrowTextButton(
        text = label,
        onClick = onClick,
        variant = if (isSelected) ShadcnButtonVariant.Default else ShadcnButtonVariant.Outline,
        size = ShadcnButtonSize.Small
    )
}
