package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.components.stitch.*

@Composable
fun EnhancedShadcnButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnButtonVariant = ShadcnButtonVariant.Default,
    size: ShadcnButtonSize = ShadcnButtonSize.Default,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    hapticFeedback: Boolean = true
) {
    // For now, we'll keep the existing implementation but add a TODO to replace with Stitch components
    // TODO: Replace with Stitch button components
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    
    // Animation values
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed && enabled) 2.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_elevation"
    )
    
    // Color animations
    val colors = SparrowTheme.colors
    val buttonColors = getButtonColors(variant, colors)
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> buttonColors.background.copy(alpha = 0.6f)
            isPressed -> buttonColors.background.copy(alpha = 0.8f)
            else -> buttonColors.background
        },
        animationSpec = tween(150),
        label = "background_color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (enabled) buttonColors.content else buttonColors.content.copy(alpha = 0.6f),
        animationSpec = tween(150),
        label = "content_color"
    )
    
    // Size configurations
    val buttonSize = getButtonSize(size)
    
    Box(
        modifier = modifier
            .scale(scale)
            .height(buttonSize.height)
            .clip(RoundedCornerShape(SparrowBorderRadius.md))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading
            ) {
                if (hapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            LoadingContent(contentColor = contentColor, size = size)
        } else {
            ButtonContent(
                text = text,
                contentColor = contentColor,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                size = size
            )
        }
    }
}

@Composable
private fun LoadingContent(
    contentColor: Color,
    size: ShadcnButtonSize
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_transition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_rotation"
    )
    
    val loadingSize = when (size) {
        ShadcnButtonSize.Small -> 16.dp
        ShadcnButtonSize.Default -> 20.dp
        ShadcnButtonSize.Large -> 24.dp
        ShadcnButtonSize.Icon -> 20.dp
    }
    
    Box(
        modifier = Modifier
            .size(loadingSize)
            .scale(scaleX = 1f, scaleY = 1f),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(loadingSize),
            color = contentColor,
            strokeWidth = 2.dp
        )
    }
}

@Composable
private fun ButtonContent(
    text: String,
    contentColor: Color,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    size: ShadcnButtonSize
) {
    Row(
        modifier = Modifier.padding(
            horizontal = when (size) {
                ShadcnButtonSize.Small -> 12.dp
                ShadcnButtonSize.Default -> 16.dp
                ShadcnButtonSize.Large -> 20.dp
                ShadcnButtonSize.Icon -> 12.dp
            },
            vertical = when (size) {
                ShadcnButtonSize.Small -> 6.dp
                ShadcnButtonSize.Default -> 8.dp
                ShadcnButtonSize.Large -> 12.dp
                ShadcnButtonSize.Icon -> 8.dp
            }
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Leading icon
        leadingIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(
                    when (size) {
                        ShadcnButtonSize.Small -> 16.dp
                        ShadcnButtonSize.Default -> 18.dp
                        ShadcnButtonSize.Large -> 20.dp
                        ShadcnButtonSize.Icon -> 18.dp
                    }
                )
            )
            Spacer(modifier = Modifier.width(SparrowSpacing.sm))
        }
        
        // Button text
        Text(
            text = text,
            color = contentColor,
            fontSize = when (size) {
                ShadcnButtonSize.Small -> SparrowTypography.small.fontSize
                ShadcnButtonSize.Default -> SparrowTypography.p.fontSize
                ShadcnButtonSize.Large -> SparrowTypography.large.fontSize
                ShadcnButtonSize.Icon -> SparrowTypography.p.fontSize
            },
            fontWeight = FontWeight.Medium,
            fontFamily = SparrowFontFamily
        )
        
        // Trailing icon
        trailingIcon?.let { icon ->
            Spacer(modifier = Modifier.width(SparrowSpacing.sm))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(
                    when (size) {
                        ShadcnButtonSize.Small -> 16.dp
                        ShadcnButtonSize.Default -> 18.dp
                        ShadcnButtonSize.Large -> 20.dp
                        ShadcnButtonSize.Icon -> 18.dp
                    }
                )
            )
        }
    }
}

private data class ButtonColors(
    val background: Color,
    val content: Color
)

private fun getButtonColors(variant: ShadcnButtonVariant, colors: SparrowThemeColors): ButtonColors {
    return when (variant) {
        ShadcnButtonVariant.Default -> ButtonColors(
            background = colors.primary,
            content = colors.primaryForeground
        )
        ShadcnButtonVariant.Secondary -> ButtonColors(
            background = colors.secondary,
            content = colors.secondaryForeground
        )
        ShadcnButtonVariant.Outline -> ButtonColors(
            background = Color.Transparent,
            content = colors.foreground
        )
        ShadcnButtonVariant.Ghost -> ButtonColors(
            background = Color.Transparent,
            content = colors.foreground
        )
        ShadcnButtonVariant.Link -> ButtonColors(
            background = Color.Transparent,
            content = colors.primary
        )
        ShadcnButtonVariant.Destructive -> ButtonColors(
            background = colors.destructive,
            content = colors.destructiveForeground
        )
        ShadcnButtonVariant.Success -> ButtonColors(
            background = colors.success ?: colors.primary,
            content = colors.primaryForeground
        )
    }
}

private data class ButtonSize(
    val height: androidx.compose.ui.unit.Dp
)

private fun getButtonSize(size: ShadcnButtonSize): ButtonSize {
    return when (size) {
        ShadcnButtonSize.Small -> ButtonSize(height = 32.dp)
        ShadcnButtonSize.Default -> ButtonSize(height = 40.dp)
        ShadcnButtonSize.Large -> ButtonSize(height = 48.dp)
        ShadcnButtonSize.Icon -> ButtonSize(height = 40.dp)
    }
}
