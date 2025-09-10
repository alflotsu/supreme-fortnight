package io.peng.sparrowdelivery.presentation.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.theme.SparrowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsScreen(
    onBackClick: () -> Unit,
    viewModel: MoreOptionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SparrowTheme {
        Scaffold(
            containerColor = SparrowTheme.colors.background,
            topBar = {
                TopAppBar(
                    title = { Text("Delivery Options", color = SparrowTheme.colors.foreground) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = SparrowTheme.colors.foreground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SparrowTheme.colors.background,
                        titleContentColor = SparrowTheme.colors.foreground,
                        navigationIconContentColor = SparrowTheme.colors.foreground
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

                // Save Button
                ShadcnTextButton(
                    text = "Save Options",
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ShadcnButtonVariant.Default
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
    ShadcnCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShadcnText(
                text = "📦 Package Details",
                style = ShadcnTextStyle.H3
            )
            
            // Package Size Selection
            ShadcnText(
                text = "Package Size",
                style = ShadcnTextStyle.H4
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
                            ShadcnText(text = size.displayName, style = ShadcnTextStyle.P)
                            ShadcnText(
                                text = size.description,
                                style = ShadcnTextStyle.Muted
                            )
                        }
                    }
                }
            }
            
            // Package Weight
            ShadcnTextField(
                value = uiState.packageWeight,
                onValueChange = onPackageWeightChange,
                label = "Package Weight (kg)",
                placeholder = "e.g., 2.5",
                modifier = Modifier.fillMaxWidth()
            )
            
            // Package Description
            ShadcnTextField(
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
    ShadcnCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShadcnText(
                text = "⏰ Delivery Time",
                style = ShadcnTextStyle.H3
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
                            ShadcnText(text = time.displayName, style = ShadcnTextStyle.P)
                            ShadcnText(
                                text = time.description,
                                style = ShadcnTextStyle.Muted
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
    ShadcnCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShadcnText(
                text = "✨ Special Services",
                style = ShadcnTextStyle.H3
            )
            
            // Require Signature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ShadcnText("Require Signature", style = ShadcnTextStyle.P)
                    ShadcnText(
                        "Recipient must sign upon delivery (+$3)",
                        style = ShadcnTextStyle.Muted
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
                    ShadcnText("Fragile Items", style = ShadcnTextStyle.P)
                    ShadcnText(
                        "Extra care handling (+$8)",
                        style = ShadcnTextStyle.Muted
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
                    ShadcnText("Priority Delivery", style = ShadcnTextStyle.P)
                    ShadcnText(
                        "Higher priority in queue (+$12)",
                        style = ShadcnTextStyle.Muted
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
    ShadcnCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShadcnText(
                text = "👤 Recipient Information",
                style = ShadcnTextStyle.H3
            )
            
            // Contact Recipient Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ShadcnText("Contact Recipient", style = ShadcnTextStyle.P)
                    ShadcnText(
                        "Notify recipient about delivery",
                        style = ShadcnTextStyle.Muted
                    )
                }
                Switch(
                    checked = uiState.contactRecipient,
                    onCheckedChange = onContactRecipientChange
                )
            }
            
            // Recipient details (only show if contact is enabled)
            if (uiState.contactRecipient) {
                ShadcnTextField(
                    value = uiState.recipientName,
                    onValueChange = onRecipientNameChange,
                    label = "Recipient Name",
                    placeholder = "John Doe",
                    modifier = Modifier.fillMaxWidth()
                )
                
                ShadcnTextField(
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
    ShadcnCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShadcnText(
                text = "📝 Special Instructions",
                style = ShadcnTextStyle.H3
            )
            
            ShadcnTextField(
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
    ShadcnCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical =12.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShadcnText(
                text = "🚗 Transport Mode",
                style = ShadcnTextStyle.H3
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
                        
                        ShadcnText(
                            text = mode.emoji,
                            style = ShadcnTextStyle.H2,
                            modifier = Modifier.width(40.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ShadcnText(
                                    text = mode.displayName,
                                    style = ShadcnTextStyle.P,
                                    color = if (isCompatible) 
                                        SparrowTheme.colors.foreground 
                                    else 
                                        SparrowTheme.colors.mutedForeground
                                )
                                
                                if (!isCompatible) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    ShadcnText(
                                        text = "⚠️ Too heavy",
                                        style = ShadcnTextStyle.Small,
                                        color = SparrowTheme.colors.destructive
                                    )
                                }
                            }
                            
                            ShadcnText(
                                text = "${mode.description} • Max ${mode.maxWeight}kg",
                                style = ShadcnTextStyle.Small,
                                color = if (isCompatible) 
                                    SparrowTheme.colors.mutedForeground 
                                else 
                                    SparrowTheme.colors.mutedForeground
                            )
                            
                            ShadcnText(
                                text = when {
                                    mode.priceMultiplier < 1.0 -> "${(100 - mode.priceMultiplier * 100).toInt()}% cheaper"
                                    mode.priceMultiplier > 1.0 -> "+${((mode.priceMultiplier - 1) * 100).toInt()}% cost"
                                    else -> "Standard pricing"
                                },
                                style = ShadcnTextStyle.Small,
                                color = when {
                                    mode.priceMultiplier < 1.0 -> SparrowTheme.colors.success
                                    mode.priceMultiplier > 1.0 -> SparrowTheme.colors.warning
                                    else -> SparrowTheme.colors.mutedForeground
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
    ShadcnCard(
        modifier = Modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Elevated
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                ShadcnText(
                    text = "Estimated Price",
                    style = ShadcnTextStyle.H4
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SparrowTheme.colors.mutedForeground
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    ShadcnText(
                        text = "Final price may vary",
                        style = ShadcnTextStyle.Small
                    )
                }
            }
            ShadcnText(
                text = "GH₵ %.2f".format(estimatedPrice),
                style = ShadcnTextStyle.H4,
                color = SparrowTheme.colors.success
            )
        }
    }
}
