package io.peng.sparrowdelivery.ui.components.stitch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme
import io.peng.sparrowdelivery.ui.theme.StitchTheme

/**
 * Stitch-themed text field based on the reference designs
 * Features rounded corners, proper focus states, and Stitch color scheme
 */
@Composable
fun StitchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxLines: Int = 1,
    singleLine: Boolean = true
) {
    val stitchColors = LocalStitchColorScheme.current
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    
    Column(modifier = modifier) {
        // Label
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = stitchColors.textSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Input field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stitchColors.textMuted
                )
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isFocused) stitchColors.inputFocused else stitchColors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon?.let { icon ->
                {
                    IconButton(
                        onClick = onTrailingIconClick ?: { },
                        enabled = onTrailingIconClick != null
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = stitchColors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            maxLines = maxLines,
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = stitchColors.onSurface,
                unfocusedTextColor = stitchColors.onSurface,
                disabledTextColor = stitchColors.textMuted,
                errorTextColor = stitchColors.onSurface,
                focusedContainerColor = stitchColors.inputBackground,
                unfocusedContainerColor = stitchColors.inputBackground,
                disabledContainerColor = stitchColors.inputBackground.copy(alpha = 0.5f),
                errorContainerColor = stitchColors.inputBackground,
                focusedBorderColor = stitchColors.inputFocused,
                unfocusedBorderColor = stitchColors.inputBorder,
                disabledBorderColor = stitchColors.inputBorder.copy(alpha = 0.5f),
                errorBorderColor = stitchColors.error,
                cursorColor = stitchColors.inputFocused
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
        )
        
        // Error message
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = stitchColors.error,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }
    }
}

/**
 * Stylized search input with search icon
 */
@Composable
fun StitchSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    enabled: Boolean = true,
    onSearch: (() -> Unit)? = null
) {
    StitchTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = Icons.Outlined.Search,
        trailingIcon = if (value.isNotEmpty()) Icons.Outlined.Clear else null,
        onTrailingIconClick = if (value.isNotEmpty()) { { onValueChange("") } } else null,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch?.invoke() }
        ),
        singleLine = true
    )
}

/**
 * Location input field with location icon
 */
@Composable
fun StitchLocationField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter location",
    label: String? = null,
    enabled: Boolean = true,
    isPickup: Boolean = true
) {
    val icon = if (isPickup) Icons.Outlined.MyLocation else Icons.Outlined.LocationOn
    
    StitchTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        label = label,
        leadingIcon = icon,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        singleLine = true
    )
}

/**
 * HTML-matching location input field with proper height and styling
 * Matches the design from updated_sample.html:
 * - height: 3.5rem (56dp)
 * - padding: 1rem (16dp)
 * - left padding: 3rem (48dp) for icon space
 * - background: #254632 (medium green)
 * - placeholder: #95c6a9 (secondary light)
 */
@Composable
fun StitchHtmlLocationField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter location",
    leadingIcon: ImageVector = Icons.Outlined.LocationOn,
    iconColor: Color = LocalStitchColorScheme.current.textSecondary,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    val stitchColors = LocalStitchColorScheme.current
    var isFocused by remember { mutableStateOf(false) }
    
    // Match HTML design: height: 3.5rem (56dp), padding: 1rem (16dp), left padding: 3rem (48dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // 3.5rem from HTML
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled) stitchColors.onSurface else stitchColors.onSurface.copy(alpha = 0.38f),
                fontSize = 16.sp // 1rem from HTML
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (enabled) stitchColors.inputBackground else stitchColors.inputBackground.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp) // 0.5rem from HTML
                )
                .border(
                    width = if (isFocused) 2.dp else if (isError) 2.dp else 0.dp,
                    color = when {
                        isError -> stitchColors.error
                        isFocused -> stitchColors.inputFocused
                        else -> Color.Transparent
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(
                    start = 48.dp, // 3rem from HTML - space for icon
                    end = 16.dp,    // 1rem from HTML
                    top = 16.dp,    // 1rem from HTML
                    bottom = 16.dp  // 1rem from HTML
                )
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            singleLine = true,
            cursorBrush = SolidColor(stitchColors.inputFocused)
        ) { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = stitchColors.textSecondary,
                        fontSize = 16.sp
                    )
                )
            }
            innerTextField()
        }
        
        // Icon positioned at left: 1rem (16dp), centered vertically
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp) // 1rem from HTML
                .size(20.dp)
        )
    }
}

/**
 * Package size selector component matching the reference design
 */
@Composable
fun StitchPackageSizeSelector(
    selectedSize: PackageSize,
    onSizeSelected: (PackageSize) -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(modifier = modifier) {
        Text(
            text = "Package Size",
            style = MaterialTheme.typography.titleMedium,
            color = stitchColors.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PackageSize.values().forEach { size ->
                val isSelected = selectedSize == size
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 2.dp,
                            color = if (isSelected) stitchColors.inputFocused else stitchColors.inputBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            if (isSelected) stitchColors.inputFocused.copy(alpha = 0.1f) 
                            else stitchColors.inputBackground
                        )
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = size.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) stitchColors.inputFocused else stitchColors.textSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

enum class PackageSize(val displayName: String) {
    Small("Small"),
    Medium("Medium"),
    Large("Large")
}

/**
 * Stitch-themed radio button group for delivery schedule
 */
@Composable
fun StitchScheduleSelector(
    selectedSchedule: DeliverySchedule,
    onScheduleSelected: (DeliverySchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(modifier = modifier) {
        Text(
            text = "When?",
            style = MaterialTheme.typography.titleMedium,
            color = stitchColors.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DeliverySchedule.values().forEach { schedule ->
                val isSelected = selectedSchedule == schedule
                
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 2.dp,
                            color = if (isSelected) stitchColors.inputFocused else stitchColors.inputBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            if (isSelected) stitchColors.inputFocused.copy(alpha = 0.1f) 
                            else stitchColors.inputBackground
                        )
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onScheduleSelected(schedule) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = stitchColors.inputFocused,
                            unselectedColor = stitchColors.textMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = schedule.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) stitchColors.inputFocused else stitchColors.textSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

enum class DeliverySchedule(val displayName: String) {
    Now("Now"),
    Later("Schedule")
}

@Preview(showBackground = true)
@Composable
private fun StitchInputFieldsPreview() {
    StitchTheme {
        var searchValue by remember { mutableStateOf("") }
        var pickupValue by remember { mutableStateOf("") }
        var dropoffValue by remember { mutableStateOf("") }
        var packageSize by remember { mutableStateOf(PackageSize.Medium) }
        var schedule by remember { mutableStateOf(DeliverySchedule.Now) }
        
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Stitch Input Components",
                style = MaterialTheme.typography.headlineMedium
            )
            
            StitchSearchField(
                value = searchValue,
                onValueChange = { searchValue = it },
                placeholder = "Search locations"
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
            
            Text(
                text = "HTML-Matching Input (56dp height)",
                style = MaterialTheme.typography.labelMedium
            )
            
            StitchHtmlLocationField(
                value = pickupValue,
                onValueChange = { pickupValue = it },
                placeholder = "Pickup location",
                leadingIcon = Icons.Outlined.MyLocation,
                iconColor = Color(0xFF2563eb)
            )
            
            StitchHtmlLocationField(
                value = dropoffValue,
                onValueChange = { dropoffValue = it },
                placeholder = "Delivery location",
                leadingIcon = Icons.Outlined.LocationOn,
                iconColor = Color(0xFF059669)
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
    }
}
