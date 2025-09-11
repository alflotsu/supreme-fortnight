package io.peng.sparrowdelivery.presentation.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.StitchTheme
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsScreen(
    onBackClick: () -> Unit,
    onApiTestingClick: () -> Unit = {},
    viewModel: MoreOptionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Scaffold(
            containerColor = stitchColors.background,
            topBar = {
                TopAppBar(
                    title = { 
                        StitchText(
                            text = "Delivery Options", 
                            color = stitchColors.onBackground
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = stitchColors.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = stitchColors.background,
                        titleContentColor = stitchColors.onBackground,
                        navigationIconContentColor = stitchColors.onBackground
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Package Details Section
                PackageDetailsSection(
                    uiState = uiState,
                    onPackageSizeChange = viewModel::updatePackageSize,
                    onPackageWeightChange = viewModel::updatePackageWeight,
                    onPackageDescriptionChange = viewModel::updatePackageDescription
                )

                // Delivery Time Section
                DeliveryTimeSection(
                    uiState = uiState,
                    onDeliveryTimeChange = viewModel::updateDeliveryTime
                )

                // Transport Mode Section
                TransportModeSection(
                    uiState = uiState,
                    onTransportModeChange = viewModel::updateTransportMode
                )

                // Special Services Section
                SpecialServicesSection(
                    uiState = uiState,
                    onRequireSignatureChange = viewModel::updateRequireSignature,
                    onFragileItemsChange = viewModel::updateFragileItems,
                    onPriorityDeliveryChange = viewModel::updatePriorityDelivery
                )

                // Recipient Information Section
                RecipientInformationSection(
                    uiState = uiState,
                    onContactRecipientChange = viewModel::updateContactRecipient,
                    onRecipientNameChange = viewModel::updateRecipientName,
                    onRecipientPhoneChange = viewModel::updateRecipientPhone
                )

                // Special Instructions Section
                SpecialInstructionsSection(
                    uiState = uiState,
                    onSpecialInstructionsChange = viewModel::updateSpecialInstructions
                )

                // Price Estimate Card
                PriceEstimateCard(estimatedPrice = uiState.estimatedPrice)

                // API Testing Button (Development Tool)
                StitchCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = StitchCardVariant.Outlined
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = stitchColors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            StitchHeading(
                                text = "🧪 Developer Tools",
                                level = 4
                            )
                        }
                        
                        StitchText(
                            text = "Test different routing API providers and their responses",
                            style = StitchTextStyle.Muted
                        )
                        
                        StitchTextButton(
                            text = "Open API Testing",
                            onClick = onApiTestingClick,
                            modifier = Modifier.fillMaxWidth(),
                            variant = StitchButtonVariant.Secondary
                        )
                    }
                }

                // Save Button
                StitchTextButton(
                    text = "Save Options",
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = StitchButtonVariant.Primary
                )
            }
        }
    }
}

@Composable
private fun PackageDetailsSection(
    uiState: MoreOptionsUiState,
    onPackageSizeChange: (PackageSize) -> Unit,
    onPackageWeightChange: (String) -> Unit,
    onPackageDescriptionChange: (String) -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StitchHeading(
                text = "📦 Package Details",
                level = 3
            )
            
            // Package Size Selection
            StitchText(
                text = "Package Size",
                style = StitchTextStyle.H4
            )
            
            Column(modifier = Modifier.selectableGroup()) {
                PackageSize.values().forEach { size ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (uiState.packageSize == size),
                                onClick = { onPackageSizeChange(size) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (uiState.packageSize == size),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            StitchText(text = size.displayName, style = StitchTextStyle.P)
                            StitchText(
                                text = size.description,
                                style = StitchTextStyle.Muted
                            )
                        }
                    }
                }
            }
            
            // Package Weight
            StitchInput(
                value = uiState.packageWeight,
                onValueChange = onPackageWeightChange,
                label = "Package Weight (kg)",
                placeholder = "e.g., 2.5",
                modifier = Modifier.fillMaxWidth()
            )
            
            // Package Description
            StitchInput(
                value = uiState.packageDescription,
                onValueChange = onPackageDescriptionChange,
                label = "Package Description",
                placeholder = "What are you sending?",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DeliveryTimeSection(
    uiState: MoreOptionsUiState,
    onDeliveryTimeChange: (MoreDeliveryTime) -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StitchHeading(
                text = "⏰ Delivery Time",
                level = 3
            )
            
            Column(modifier = Modifier.selectableGroup()) {
                MoreDeliveryTime.values().forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (uiState.deliveryTime == time),
                                onClick = { onDeliveryTimeChange(time) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (uiState.deliveryTime == time),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            StitchText(text = time.displayName, style = StitchTextStyle.P)
                            StitchText(
                                text = time.description,
                                style = StitchTextStyle.Muted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecialServicesSection(
    uiState: MoreOptionsUiState,
    onRequireSignatureChange: (Boolean) -> Unit,
    onFragileItemsChange: (Boolean) -> Unit,
    onPriorityDeliveryChange: (Boolean) -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StitchHeading(
                text = "✨ Special Services",
                level = 3
            )
            
            // Require Signature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    StitchText("Require Signature", style = StitchTextStyle.P)
                    StitchText(
                        "Recipient must sign upon delivery (+$3)",
                        style = StitchTextStyle.Muted
                    )
                }
                Switch(
                    checked = uiState.requireSignature,
                    onCheckedChange = onRequireSignatureChange
                )
            }
            
            // Fragile Items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    StitchText("Fragile Items", style = StitchTextStyle.P)
                    StitchText(
                        "Extra care handling (+$8)",
                        style = StitchTextStyle.Muted
                    )
                }
                Switch(
                    checked = uiState.fragileItems,
                    onCheckedChange = onFragileItemsChange
                )
            }
            
            // Priority Delivery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    StitchText("Priority Delivery", style = StitchTextStyle.P)
                    StitchText(
                        "Higher priority in queue (+$12)",
                        style = StitchTextStyle.Muted
                    )
                }
                Switch(
                    checked = uiState.priorityDelivery,
                    onCheckedChange = onPriorityDeliveryChange
                )
            }
        }
    }
}

@Composable
private fun RecipientInformationSection(
    uiState: MoreOptionsUiState,
    onContactRecipientChange: (Boolean) -> Unit,
    onRecipientNameChange: (String) -> Unit,
    onRecipientPhoneChange: (String) -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StitchHeading(
                text = "👤 Recipient Information",
                level = 3
            )
            
            // Contact Recipient Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    StitchText("Contact Recipient", style = StitchTextStyle.P)
                    StitchText(
                        "Notify recipient about delivery",
                        style = StitchTextStyle.Muted
                    )
                }
                Switch(
                    checked = uiState.contactRecipient,
                    onCheckedChange = onContactRecipientChange
                )
            }
            
            // Recipient details (only show if contact is enabled)
            if (uiState.contactRecipient) {
                StitchInput(
                    value = uiState.recipientName,
                    onValueChange = onRecipientNameChange,
                    label = "Recipient Name",
                    placeholder = "John Doe",
                    modifier = Modifier.fillMaxWidth()
                )
                
                StitchInput(
                    value = uiState.recipientPhone,
                    onValueChange = onRecipientPhoneChange,
                    label = "Recipient Phone",
                    placeholder = "+1 234 567 8900",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SpecialInstructionsSection(
    uiState: MoreOptionsUiState,
    onSpecialInstructionsChange: (String) -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StitchHeading(
                text = "📝 Special Instructions",
                level = 3
            )
            
            StitchTextArea(
                value = uiState.specialInstructions,
                onValueChange = onSpecialInstructionsChange,
                label = "Special Instructions",
                placeholder = "e.g., Leave at door, Ring doorbell, etc.",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TransportModeSection(
    uiState: MoreOptionsUiState,
    onTransportModeChange: (TransportMode) -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StitchHeading(
                text = "🚗 Transport Mode",
                level = 3
            )
            
            Column(modifier = Modifier.selectableGroup()) {
                TransportMode.values().forEach { mode ->
                    val packageWeight = uiState.packageWeight.toDoubleOrNull() ?: 0.0
                    val isCompatible = packageWeight <= mode.maxWeight || packageWeight == 0.0
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (uiState.transportMode == mode),
                                onClick = { 
                                    if (isCompatible) {
                                        onTransportModeChange(mode)
                                    }
                                },
                                role = Role.RadioButton,
                                enabled = isCompatible
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (uiState.transportMode == mode),
                            onClick = null,
                            enabled = isCompatible
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        StitchText(
                            text = mode.emoji,
                            style = StitchTextStyle.H2,
                            modifier = Modifier.width(40.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StitchText(
                                    text = mode.displayName,
                                    style = StitchTextStyle.P,
                                    color = if (isCompatible) 
                                        stitchColors.onSurface 
                                    else 
                                        stitchColors.textSecondary
                                )
                                
                                if (!isCompatible) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    StitchText(
                                        text = "⚠️ Too heavy",
                                        style = StitchTextStyle.Small,
                                        color = stitchColors.destructive
                                    )
                                }
                            }
                            
                            StitchText(
                                text = "${mode.description} • Max ${mode.maxWeight}kg",
                                style = StitchTextStyle.Small,
                                color = if (isCompatible) 
                                    stitchColors.textSecondary
                                else 
                                    stitchColors.textSecondary
                            )
                            
                            StitchText(
                                text = when {
                                    mode.priceMultiplier < 1.0 -> "${(100 - mode.priceMultiplier * 100).toInt()}% cheaper"
                                    mode.priceMultiplier > 1.0 -> "+${((mode.priceMultiplier - 1) * 100).toInt()}% cost"
                                    else -> "Standard pricing"
                                },
                                style = StitchTextStyle.Small,
                                color = when {
                                    mode.priceMultiplier < 1.0 -> stitchColors.success
                                    mode.priceMultiplier > 1.0 -> stitchColors.warning
                                    else -> stitchColors.textSecondary
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceEstimateCard(estimatedPrice: Double) {
    val stitchColors = LocalStitchColorScheme.current
    
    StitchCard(
        modifier = Modifier.fillMaxWidth(),
        variant = StitchCardVariant.Elevated
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                StitchHeading(
                    text = "Estimated Price",
                    level = 4
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = stitchColors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    StitchText(
                        text = "Final price may vary",
                        style = StitchTextStyle.Small
                    )
                }
            }
            StitchText(
                text = "GH₵ %.2f".format(estimatedPrice),
                style = StitchTextStyle.H4,
                color = stitchColors.success
            )
        }
    }
}
