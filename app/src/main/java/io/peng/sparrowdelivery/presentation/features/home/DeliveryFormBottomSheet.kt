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
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*

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
    ShadcnTheme {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(ShadcnSpacing.lg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.lg)
        ) {
            // Header
            ShadcnHeading(
                text = "Plan Your Delivery",
                level = 3,
                modifier = Modifier.padding(bottom = ShadcnSpacing.sm)
            )

            // Pickup Location
            ShadcnInput(
                value = deliveryForm.pickupLocation,
                onValueChange = onPickupLocationChange,
                label = "📍 Pickup Location",
                placeholder = "Enter pickup address",
                modifier = Modifier.fillMaxWidth()
            )

            // Destination
            ShadcnInput(
                value = deliveryForm.destination,
                onValueChange = onDestinationChange,
                label = "🎯 Destination",
                placeholder = "Enter destination address",
                modifier = Modifier.fillMaxWidth()
            )

            // Number of Intermediate Stops
            ShadcnCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                ) {
                    ShadcnText(
                        text = "Intermediate Stops",
                        style = ShadcnTextStyle.H4
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.lg)
                    ) {
                        ShadcnIconButton(
                            icon = Icons.Default.Add, // We'll use rotation for minus
                            onClick = {
                                if (deliveryForm.numberOfStops > 0) {
                                    onNumberOfStopsChange(deliveryForm.numberOfStops - 1)
                                }
                            },
                            enabled = deliveryForm.numberOfStops > 0,
                            variant = ShadcnButtonVariant.Outline,
                            contentDescription = "Remove stop"
                        )

                        ShadcnText(
                            text = "${deliveryForm.numberOfStops} stops",
                            style = ShadcnTextStyle.P,
                            modifier = Modifier.weight(1f)
                        )

                        ShadcnIconButton(
                            icon = Icons.Default.Add,
                            onClick = {
                                if (deliveryForm.numberOfStops < 5) { // Max 5 stops
                                    onNumberOfStopsChange(deliveryForm.numberOfStops + 1)
                                }
                            },
                            enabled = deliveryForm.numberOfStops < 5,
                            variant = ShadcnButtonVariant.Default,
                            contentDescription = "Add stop"
                        )
                    }

                    // Intermediate stop text fields
                    deliveryForm.intermediateStops.forEachIndexed { index, stop ->
                        ShadcnTextField(
                            value = stop,
                            onValueChange = { onIntermediateStopChange(index, it) },
                            label = "Stop ${index + 1}",
                            placeholder = "Enter stop ${index + 1} address",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Delivery Summary
            if (deliveryForm.destination.isNotBlank() || deliveryForm.pickupLocation.isNotBlank()) {
                ShadcnCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = ShadcnCardVariant.Elevated
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                    ) {
                        ShadcnText(
                            text = "Delivery Summary",
                            style = ShadcnTextStyle.H4
                        )

                        if (deliveryForm.pickupLocation.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ShadcnText(
                                    text = "From: ",
                                    style = ShadcnTextStyle.Muted
                                )
                                ShadcnText(
                                    text = deliveryForm.pickupLocation,
                                    style = ShadcnTextStyle.P
                                )
                            }
                        }

                        if (deliveryForm.destination.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ShadcnText(
                                    text = "To: ",
                                    style = ShadcnTextStyle.Muted
                                )
                                ShadcnText(
                                    text = deliveryForm.destination,
                                    style = ShadcnTextStyle.P
                                )
                            }
                        }

                        if (deliveryForm.numberOfStops > 0) {
                            ShadcnText(
                                text = "${deliveryForm.numberOfStops} intermediate stop(s)",
                                style = ShadcnTextStyle.Muted
                            )
                        }
                    }
                }
            }

            // More Options Button
            ShadcnTextButton(
                text = "⚙️ More Options",
                onClick = onMoreOptionsClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ShadcnButtonVariant.Outline
            )

            // Find Driver Button
            ShadcnTextButton(
                text = if (deliveryForm.isLoadingPricing) "Finding Drivers..." else "📍 Find Driver & Pricing",
                onClick = onFindDriverClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = deliveryForm.destination.isNotBlank() &&
                        deliveryForm.pickupLocation.isNotBlank() &&
                        !deliveryForm.isLoadingPricing,
                variant = ShadcnButtonVariant.Default
            )
        }
    }
}
