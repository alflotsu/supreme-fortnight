package io.peng.sparrowdelivery.ui.components.stitch

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
// Removed LazyColumn imports - using regular Column instead
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme
import io.peng.sparrowdelivery.ui.theme.StitchTheme

/**
 * Option data class for Stitch dropdown
 */
data class StitchDropdownOption<T>(
    val value: T,
    val label: String,
    val description: String? = null,
    val icon: ImageVector? = null,
    val enabled: Boolean = true
)

/**
 * Stitch Dropdown component matching the design system
 * Perfect for selecting mapping API providers
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> StitchDropdown(
    options: List<StitchDropdownOption<T>>,
    selectedOption: StitchDropdownOption<T>?,
    onOptionSelected: (StitchDropdownOption<T>) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "Select an option",
    enabled: Boolean = true,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val stitchColors = LocalStitchColorScheme.current
    val haptic = LocalHapticFeedback.current
    
    Column(modifier = modifier) {
        // Label
        if (label.isNotEmpty()) {
            StitchText(
                text = label,
                style = StitchTextStyle.Small,
                color = stitchColors.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Dropdown trigger
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { 
                if (enabled) {
                    expanded = !expanded
                    if (expanded) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            }
        ) {
            // Trigger button
            StitchDropdownTrigger(
                selectedOption = selectedOption,
                placeholder = placeholder,
                expanded = expanded,
                enabled = enabled,
                error = error,
                modifier = Modifier.menuAnchor()
            )
            
            // Dropdown menu
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(
                        stitchColors.surface,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        stitchColors.outline,
                        RoundedCornerShape(12.dp)
                    )
                    .widthIn(min = 200.dp, max = 300.dp)
                    .heightIn(max = 300.dp)
            ) {
                Column(
                    modifier = Modifier.padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    options.forEach { option ->
                        StitchDropdownItem(
                            option = option,
                            selected = selectedOption?.value == option.value,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onOptionSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        
        // Error message
        error?.let {
            StitchText(
                text = it,
                style = StitchTextStyle.Small,
                color = stitchColors.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Dropdown trigger button
 */
@Composable
private fun <T> StitchDropdownTrigger(
    selectedOption: StitchDropdownOption<T>?,
    placeholder: String,
    expanded: Boolean,
    enabled: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    val borderColor = when {
        error != null -> stitchColors.error
        expanded -> stitchColors.primary
        else -> stitchColors.outline
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(12.dp)
            ),
        color = if (enabled) stitchColors.surface else stitchColors.surface.copy(alpha = 0.6f),
        contentColor = stitchColors.onSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Selected content or placeholder
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Icon
                selectedOption?.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) stitchColors.onSurface else stitchColors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Text
                StitchText(
                    text = selectedOption?.label ?: placeholder,
                    style = StitchTextStyle.P,
                    color = if (selectedOption != null) {
                        if (enabled) stitchColors.onSurface else stitchColors.onSurface.copy(alpha = 0.6f)
                    } else {
                        stitchColors.textMuted
                    }
                )
            }
            
            // Dropdown arrow
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = if (enabled) stitchColors.onSurface else stitchColors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Individual dropdown item
 */
@Composable
private fun <T> StitchDropdownItem(
    option: StitchDropdownOption<T>,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = option.enabled) { onClick() },
        color = if (selected) {
            stitchColors.primary.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        contentColor = if (option.enabled) stitchColors.onSurface else stitchColors.onSurface.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            option.icon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) stitchColors.primary else LocalContentColor.current,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                StitchText(
                    text = option.label,
                    style = StitchTextStyle.P,
                    color = if (selected) stitchColors.primary else LocalContentColor.current
                )
                
                option.description?.let { desc ->
                    StitchText(
                        text = desc,
                        style = StitchTextStyle.Small,
                        color = stitchColors.textMuted
                    )
                }
            }
            
            // Selected indicator
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = stitchColors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * API Provider Dropdown - Specific component for your use case
 */
@Composable
fun ApiProviderDropdown(
    selectedProvider: io.peng.sparrowdelivery.domain.entities.RouteProvider?,
    onProviderSelected: (io.peng.sparrowdelivery.domain.entities.RouteProvider) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val providerOptions = remember {
        listOf(
            StitchDropdownOption(
                value = io.peng.sparrowdelivery.domain.entities.RouteProvider.GOOGLE_MAPS,
                label = "Google Maps",
                description = "Comprehensive routing and geocoding API",
                icon = Icons.Default.Place
            )
        )
    }
    
    val selectedOption = providerOptions.find { it.value == selectedProvider }
    
    StitchDropdown(
        options = providerOptions,
        selectedOption = selectedOption,
        onOptionSelected = { option ->
            onProviderSelected(option.value)
        },
        label = "Routing API Provider",
        placeholder = "Choose API provider",
        enabled = enabled,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun StitchDropdownPreview() {
    StitchTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var selectedProvider by remember { 
                mutableStateOf<io.peng.sparrowdelivery.domain.entities.RouteProvider?>(null) 
            }
            
            ApiProviderDropdown(
                selectedProvider = selectedProvider,
                onProviderSelected = { provider ->
                    selectedProvider = provider
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Show selection
            selectedProvider?.let { provider ->
                StitchCard(
                    variant = io.peng.sparrowdelivery.ui.components.stitch.StitchCardVariant.Outlined,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StitchText(
                        text = "Selected: $provider",
                        style = StitchTextStyle.P,
                        color = LocalStitchColorScheme.current.primary
                    )
                }
            }
        }
    }
}
