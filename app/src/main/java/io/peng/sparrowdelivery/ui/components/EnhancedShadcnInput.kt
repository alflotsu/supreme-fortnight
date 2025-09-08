package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.*

@Composable
fun EnhancedShadcnInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    hapticFeedback: Boolean = true
) {
    val colors = ShadcnTheme.colors
    val haptic = LocalHapticFeedback.current
    val focusRequester = remember { FocusRequester() }
    
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    // Animation values
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> colors.destructive
            isFocused -> colors.primary
            else -> colors.border
        },
        animationSpec = tween(200),
        label = "border_color"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.muted.copy(alpha = 0.5f)
            isFocused -> colors.background
            else -> colors.background
        },
        animationSpec = tween(200),
        label = "background_color"
    )
    
    val labelColor by animateColorAsState(
        targetValue = when {
            isError -> colors.destructive
            isFocused -> colors.primary
            else -> colors.mutedForeground
        },
        animationSpec = tween(200),
        label = "label_color"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "input_scale"
    )
    
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "border_width"
    )
    
    LaunchedEffect(isFocused) {
        if (isFocused && hapticFeedback) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }
    
    Column(modifier = modifier) {
        // Label
        label?.let { labelText ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = labelText,
                    style = ShadcnTypography.small,
                    color = labelColor,
                    modifier = Modifier.padding(bottom = ShadcnSpacing.xs)
                )
            }
        }
        
        // Input field container
        Box(
            modifier = Modifier
                .scale(scale)
                .fillMaxWidth()
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(ShadcnBorderRadius.md)
                )
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(ShadcnBorderRadius.md)
                )
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = ShadcnTypography.p.copy(color = colors.foreground),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                enabled = enabled,
                readOnly = readOnly,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(colors.primary),
                decorationBox = { innerTextField ->
                    InputDecorationBox(
                        value = value,
                        innerTextField = innerTextField,
                        placeholder = placeholder,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        onTrailingIconClick = onTrailingIconClick,
                        colors = colors,
                        enabled = enabled,
                        isFocused = isFocused
                    )
                }
            )
        }
        
        // Supporting text with animation
        AnimatedVisibility(
            visible = supportingText != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            supportingText?.let { text ->
                Text(
                    text = text,
                    style = ShadcnTypography.small,
                    color = if (isError) colors.destructive else colors.mutedForeground,
                    modifier = Modifier.padding(top = ShadcnSpacing.xs)
                )
            }
        }
    }
}

@Composable
private fun InputDecorationBox(
    value: String,
    innerTextField: @Composable () -> Unit,
    placeholder: String,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    onTrailingIconClick: (() -> Unit)?,
    colors: ShadcnThemeColors,
    enabled: Boolean,
    isFocused: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading icon with animation
        leadingIcon?.let { icon ->
            val iconColor by animateColorAsState(
                targetValue = if (isFocused) colors.primary else colors.mutedForeground,
                animationSpec = tween(200),
                label = "leading_icon_color"
            )
            
            val iconScale by animateFloatAsState(
                targetValue = if (isFocused) 1.1f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "leading_icon_scale"
            )
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .scale(iconScale)
                    .size(18.dp)
            )
            Spacer(modifier = Modifier.width(ShadcnSpacing.sm))
        }
        
        // Text field content
        Box(
            modifier = Modifier.weight(1f)
        ) {
            // Placeholder with fade animation
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = ShadcnTypography.p,
                    color = colors.mutedForeground
                )
            }
            
            // Actual text field
            innerTextField()
        }
        
        // Trailing icon with animation
        trailingIcon?.let { icon ->
            val iconColor by animateColorAsState(
                targetValue = if (isFocused) colors.primary else colors.mutedForeground,
                animationSpec = tween(200),
                label = "trailing_icon_color"
            )
            
            val iconScale by animateFloatAsState(
                targetValue = if (isFocused) 1.1f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "trailing_icon_scale"
            )
            
            Spacer(modifier = Modifier.width(ShadcnSpacing.sm))
            IconButton(
                onClick = { onTrailingIconClick?.invoke() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .scale(iconScale)
                        .size(18.dp)
                )
            }
        }
    }
}

// Specialized input variants with built-in animations
@Composable
fun EnhancedShadcnEmailInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Email",
    placeholder: String = "Enter your email",
    enabled: Boolean = true
) {
    EnhancedShadcnInput(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        enabled = enabled,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        ),
        leadingIcon = Icons.Default.Email
    )
}

@Composable
fun EnhancedShadcnPasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter your password",
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    EnhancedShadcnInput(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        enabled = enabled,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        },
        leadingIcon = Icons.Default.Lock,
        trailingIcon = if (passwordVisible) {
            Icons.Default.Visibility
        } else {
            Icons.Default.VisibilityOff
        },
        onTrailingIconClick = { passwordVisible = !passwordVisible }
    )
}
