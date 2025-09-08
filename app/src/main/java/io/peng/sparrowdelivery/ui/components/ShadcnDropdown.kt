package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.*

data class ShadcnSelectOption<T>(
    val value: T,
    val label: String,
    val description: String? = null,
    val icon: ImageVector? = null,
    val enabled: Boolean = true
)

/**
 * shadcn/ui inspired Select component with dropdown functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ShadcnSelect(
    options: List<ShadcnSelectOption<T>>,
    selectedOption: ShadcnSelectOption<T>?,
    onOptionSelected: (ShadcnSelectOption<T>) -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnInputVariant = ShadcnInputVariant.Default,
    size: ShadcnInputSize = ShadcnInputSize.Default,
    placeholder: String = "Select an option",
    label: String = "",
    helper: String = "",
    error: String? = null,
    enabled: Boolean = true,
    searchable: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val colors = ShadcnTheme.colors
    
    val filteredOptions = remember(searchText, options) {
        if (searchable && searchText.isNotEmpty()) {
            options.filter { 
                it.label.contains(searchText, ignoreCase = true) ||
                it.description?.contains(searchText, ignoreCase = true) == true
            }
        } else {
            options
        }
    }
    
    Column(modifier = modifier) {
        // Label
        if (label.isNotEmpty()) {
            ShadcnText(
                text = label,
                style = ShadcnTextStyle.Small,
                color = colors.foreground,
                modifier = Modifier.padding(bottom = ShadcnSpacing.xs)
            )
        }
        
        // Dropdown trigger
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            ShadcnInput(
                value = selectedOption?.label ?: "",
                onValueChange = { },
                modifier = Modifier.menuAnchor(),
                variant = variant,
                size = size,
                placeholder = placeholder,
                helper = helper,
                error = error,
                enabled = enabled,
                readOnly = true,
                trailingIcon = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
            )
            
            // Dropdown menu
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = colors.popover,
                            shape = RoundedCornerShape(ShadcnBorderRadius.md)
                        )
                        .border(
                            width = 1.dp,
                            color = colors.border,
                            shape = RoundedCornerShape(ShadcnBorderRadius.md)
                        )
                        .width(200.dp)
                        .heightIn(max = 300.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(ShadcnSpacing.xs)
                    ) {
                        // Search input (if searchable)
                        if (searchable) {
                            item {
                                ShadcnSearchInput(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    placeholder = "Search options...",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = ShadcnSpacing.xs)
                                )
                            }
                        }
                        
                        // Options
                        items(filteredOptions) { option ->
                            ShadcnDropdownItem(
                                option = option,
                                selected = selectedOption?.value == option.value,
                                onClick = {
                                    onOptionSelected(option)
                                    expanded = false
                                    searchText = ""
                                }
                            )
                        }
                        
                        // No results message
                        if (filteredOptions.isEmpty() && searchable && searchText.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(ShadcnSpacing.md),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ShadcnText(
                                        text = "No options found",
                                        style = ShadcnTextStyle.Small,
                                        color = colors.mutedForeground
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual dropdown item component
 */
@Composable
private fun <T> ShadcnDropdownItem(
    option: ShadcnSelectOption<T>,
    selected: Boolean,
    onClick: () -> Unit,
    showCheckbox: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = ShadcnTheme.colors
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (selected) colors.accent.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(ShadcnBorderRadius.sm)
            )
            .clickable(enabled = option.enabled) { onClick() }
            .padding(
                horizontal = ShadcnSpacing.sm,
                vertical = ShadcnSpacing.xs
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
        ) {
            // Checkbox for multi-select
            if (showCheckbox) {
                Icon(
                    imageVector = if (selected) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (selected) "Selected" else "Not selected",
                    tint = if (selected) colors.primary else colors.mutedForeground,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            // Option icon
            option.icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (option.enabled) colors.foreground else colors.mutedForeground,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Option content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                ShadcnText(
                    text = option.label,
                    style = ShadcnTextStyle.Small,
                    color = if (option.enabled) {
                        if (selected) colors.accent else colors.foreground
                    } else {
                        colors.mutedForeground
                    }
                )
                
                option.description?.let { desc ->
                    ShadcnText(
                        text = desc,
                        style = ShadcnTextStyle.Small,
                        color = colors.mutedForeground
                    )
                }
            }
            
            // Selected indicator
            if (selected && !showCheckbox) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Combobox component that combines input and dropdown
 */
@Composable
fun <T> ShadcnCombobox(
    options: List<ShadcnSelectOption<T>>,
    selectedOption: ShadcnSelectOption<T>?,
    onOptionSelected: (ShadcnSelectOption<T>) -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnInputVariant = ShadcnInputVariant.Default,
    size: ShadcnInputSize = ShadcnInputSize.Default,
    placeholder: String = "Type or select...",
    label: String = "",
    helper: String = "",
    error: String? = null,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf(selectedOption?.label ?: "") }
    val focusManager = LocalFocusManager.current
    
    val filteredOptions = remember(inputValue, options) {
        if (inputValue.isNotEmpty()) {
            options.filter { 
                it.label.contains(inputValue, ignoreCase = true) ||
                it.description?.contains(inputValue, ignoreCase = true) == true
            }
        } else {
            options
        }
    }
    
    LaunchedEffect(selectedOption) {
        inputValue = selectedOption?.label ?: ""
    }
    
    Column(modifier = modifier) {
        // Label
        if (label.isNotEmpty()) {
            ShadcnText(
                text = label,
                style = ShadcnTextStyle.Small,
                color = ShadcnTheme.colors.foreground,
                modifier = Modifier.padding(bottom = ShadcnSpacing.xs)
            )
        }
        
        // Input field
        ShadcnInput(
            value = inputValue,
            onValueChange = { value ->
                inputValue = value
                onValueChange(value)
                expanded = value.isNotEmpty()
            },
            variant = variant,
            size = size,
            placeholder = placeholder,
            helper = helper,
            error = error,
            enabled = enabled,
            trailingIcon = Icons.Default.ArrowDropDown,
            modifier = Modifier.onFocusChanged {
                if (it.isFocused && inputValue.isNotEmpty()) {
                    expanded = true
                }
            }
        )
        
        // Dropdown suggestions
        AnimatedVisibility(
            visible = expanded && filteredOptions.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(150)) + 
                   slideInVertically(initialOffsetY = { -it / 4 }, animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150)) +
                  slideOutVertically(targetOffsetY = { -it / 4 }, animationSpec = tween(150))
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = ShadcnTheme.colors.popover,
                        shape = RoundedCornerShape(ShadcnBorderRadius.md)
                    )
                    .border(
                        width = 1.dp,
                        color = ShadcnTheme.colors.border,
                        shape = RoundedCornerShape(ShadcnBorderRadius.md)
                    )
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(ShadcnSpacing.xs)
                ) {
                    items(filteredOptions.take(8)) { option ->
                        ShadcnDropdownItem(
                            option = option,
                            selected = selectedOption?.value == option.value,
                            onClick = {
                                onOptionSelected(option)
                                inputValue = option.label
                                expanded = false
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }
        }
    }
}
