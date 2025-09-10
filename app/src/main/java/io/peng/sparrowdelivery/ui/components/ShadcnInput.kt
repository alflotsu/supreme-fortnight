package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.components.stitch.*

enum class ShadcnInputVariant {
    Default,
    Ghost,
    Outlined
}

enum class ShadcnInputSize {
    Small,
    Default,
    Large
}

enum class ShadcnInputState {
    Default,
    Error,
    Success,
    Warning
}

/**
 * shadcn/ui inspired Input component that completely overrides Material 3 styling
 */
@Composable
fun ShadcnInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnInputVariant = ShadcnInputVariant.Default,
    size: ShadcnInputSize = ShadcnInputSize.Default,
    state: ShadcnInputState = ShadcnInputState.Default,
    placeholder: String = "",
    label: String = "",
    helper: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    // Map to StitchSearchField or other Stitch input components based on usage
    // For now, we'll use StitchSearchField as a placeholder
    StitchSearchField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        enabled = enabled
    )
}

/**
 * Password input with visibility toggle
 */
@Composable
fun ShadcnPasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnInputVariant = ShadcnInputVariant.Default,
    size: ShadcnInputSize = ShadcnInputSize.Default,
    placeholder: String = "Enter password",
    label: String = "",
    helper: String = "",
    enabled: Boolean = true,
    error: String? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    ShadcnInput(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        placeholder = placeholder,
        label = label,
        helper = helper,
        trailingIcon = if (isPasswordVisible) Icons.Default.Clear else Icons.Default.Add,
        onTrailingIconClick = { isPasswordVisible = !isPasswordVisible },
        enabled = enabled,
        error = error,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true
    )
}

/**
 * Search input with clear functionality
 */
@Composable
fun ShadcnSearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    enabled: Boolean = true,
    onSearch: ((String) -> Unit)? = null,
    onClear: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    
    ShadcnInput(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = ShadcnInputVariant.Default,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Search,
        trailingIcon = if (value.isNotEmpty()) Icons.Default.Clear else null,
        onTrailingIconClick = if (value.isNotEmpty()) {
            {
                onValueChange("")
                onClear?.invoke()
            }
        } else null,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch?.invoke(value)
                focusManager.clearFocus()
            }
        ),
        singleLine = true
    )
}

/**
 * Email input with email keyboard
 */
@Composable
fun ShadcnEmailInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnInputVariant = ShadcnInputVariant.Default,
    size: ShadcnInputSize = ShadcnInputSize.Default,
    placeholder: String = "Enter email",
    label: String = "",
    helper: String = "",
    enabled: Boolean = true,
    error: String? = null
) {
    ShadcnInput(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        placeholder = placeholder,
        label = label,
        helper = helper,
        leadingIcon = Icons.Default.Email,
        enabled = enabled,
        error = error,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true
    )
}

/**
 * Number input with number keyboard
 */
@Composable
fun ShadcnNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnInputVariant = ShadcnInputVariant.Default,
    size: ShadcnInputSize = ShadcnInputSize.Default,
    placeholder: String = "Enter number",
    label: String = "",
    helper: String = "",
    enabled: Boolean = true,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    ShadcnInput(
        value = value,
        onValueChange = { newValue ->
            // Allow only numbers for basic number input
            if (keyboardType == KeyboardType.Number && newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            } else if (keyboardType != KeyboardType.Number) {
                onValueChange(newValue)
            }
        },
        modifier = modifier,
        variant = variant,
        size = size,
        placeholder = placeholder,
        label = label,
        helper = helper,
        enabled = enabled,
        error = error,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

/**
 * Multi-line text area
 */
@Composable
fun ShadcnTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnInputVariant = ShadcnInputVariant.Default,
    placeholder: String = "",
    label: String = "",
    helper: String = "",
    enabled: Boolean = true,
    error: String? = null,
    maxLines: Int = 4,
    minHeight: Int = 3
) {
    ShadcnInput(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.heightIn(min = (minHeight * 24).dp),
        variant = variant,
        size = ShadcnInputSize.Default,
        placeholder = placeholder,
        label = label,
        helper = helper,
        enabled = enabled,
        error = error,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default
        ),
        singleLine = false,
        maxLines = maxLines
    )
}

/**
 * Legacy TextField wrapper for backward compatibility
 * @deprecated Use ShadcnInput instead
 */
@Deprecated("Use ShadcnInput instead", ReplaceWith("ShadcnInput"))
@Composable
fun ShadcnTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    ShadcnInput(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder ?: "",
        label = label ?: "",
        helper = supportingText ?: "",
        enabled = enabled,
        readOnly = readOnly,
        error = if (isError) "Error" else null,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        maxLines = maxLines
    )
}
