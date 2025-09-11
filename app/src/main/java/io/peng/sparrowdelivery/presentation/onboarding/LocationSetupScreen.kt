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
import io.peng.sparrowdelivery.ui.components.stitch.*
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
    
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Location icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = stitchColors.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = stitchColors.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StitchHeading(
                    text = "Set Your Default Location",
                    level = 1,
                    color = stitchColors.onBackground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                StitchText(
                    text = "We'll use this as your preferred delivery address. You can always change it later!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = stitchColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Address input section
            AnimatedVisibility(
                visible = showAddressInput,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Address input field
                    StitchTextField(
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
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Address label selection
                    if (isAddressValid) {
                        Column {
                            StitchText(
                                text = "Label this address as:",
                                style = MaterialTheme.typography.labelSmall,
                                color = stitchColors.textSecondary
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Current location button
                    StitchOutlineButton(
                        text = "📍 Use Current Location",
                        onClick = {
                            // TODO: Implement current location detection
                            selectedAddress = "Current location (detected)"
                            isAddressValid = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.MyLocation
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom buttons
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Set location button
                StitchPrimaryButton(
                    text = "Set as Default Location",
                    onClick = { 
                        if (isAddressValid) {
                            onLocationSet(selectedAddress, selectedLabel)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isAddressValid
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Skip button
                StitchTextButton(
                    text = "Skip for now",
                    onClick = onSkip,
                    variant = StitchButtonVariant.Ghost,
                    size = StitchButtonSize.Large,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AddressLabelChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    StitchTextButton(
        text = label,
        onClick = onClick,
        variant = if (isSelected) StitchButtonVariant.Primary else StitchButtonVariant.Outline,
        size = StitchButtonSize.Small
    )
}
