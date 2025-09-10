package io.peng.sparrowdelivery.presentation.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryFormBottomSheet(
    deliveryForm: DeliveryFormState,
    onPickupLocationChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onNumberOfStopsChange: (Int) -> Unit,
    onIntermediateStopChange: (Int, String) -> Unit,
    onFindDriverClick: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp) // Using direct dp values for consistency with Stitch design
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp) // Using direct dp values
    ) {
        // Header
        Text(
            text = "Plan Your Delivery",
            style = MaterialTheme.typography.headlineMedium,
            color = stitchColors.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Pickup Location
        StitchLocationField(
            value = deliveryForm.pickupLocation,
            onValueChange = onPickupLocationChange,
            label = "📍 Pickup Location",
            placeholder = "Enter pickup address",
            modifier = Modifier.fillMaxWidth(),
            isPickup = true
        )

        // Destination
        StitchLocationField(
            value = deliveryForm.destination,
            onValueChange = onDestinationChange,
            label = "🎯 Destination",
            placeholder = "Enter destination address",
            modifier = Modifier.fillMaxWidth(),
            isPickup = false
        )

        // Package Size Selector
        // Note: This would need to be added to the DeliveryFormState
        // StitchPackageSizeSelector(
        //     selectedSize = deliveryForm.packageSize,
        //     onSizeSelected = { /* update state */ },
        //     modifier = Modifier.fillMaxWidth()
        // )

        // Delivery Summary Card
        if (deliveryForm.destination.isNotBlank() || deliveryForm.pickupLocation.isNotBlank()) {
            // Using a simple card implementation for now
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = stitchColors.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Delivery Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = stitchColors.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    if (deliveryForm.pickupLocation.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "From: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = stitchColors.textSecondary
                            )
                            Text(
                                text = deliveryForm.pickupLocation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = stitchColors.onSurface
                            )
                        }
                    }

                    if (deliveryForm.destination.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "To: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = stitchColors.textSecondary
                            )
                            Text(
                                text = deliveryForm.destination,
                                style = MaterialTheme.typography.bodyMedium,
                                color = stitchColors.onSurface
                            )
                        }
                    }

                    // Note: numberOfStops would need to be added to the DeliveryFormState
                    // if (deliveryForm.numberOfStops > 0) {
                    //     Text(
                    //         text = "${deliveryForm.numberOfStops} intermediate stop(s)",
                    //         style = MaterialTheme.typography.bodySmall,
                    //         color = stitchColors.textSecondary
                    //     )
                    // }
                }
            }
        }

        // More Options Button
        StitchOutlineButton(
            onClick = onMoreOptionsClick,
            text = "⚙️ More Options",
            modifier = Modifier.fillMaxWidth()
        )

        // Find Driver Button
        StitchPrimaryButton(
            onClick = onFindDriverClick,
            text = if (deliveryForm.isLoadingPricing) "Finding Drivers..." else "📍 Find Driver & Pricing",
            modifier = Modifier.fillMaxWidth(),
            enabled = deliveryForm.destination.isNotBlank() &&
                    deliveryForm.pickupLocation.isNotBlank() &&
                    !deliveryForm.isLoadingPricing
        )
    }
}
