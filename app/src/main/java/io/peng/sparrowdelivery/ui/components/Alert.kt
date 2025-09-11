package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import io.peng.sparrowdelivery.ui.theme.*

enum class AlertVariant {
    Default,
    Destructive,
    Warning,
    Success,
    Info
}


@Composable
fun Alert(
    title: String? = null,
    description: String,
    modifier: Modifier = Modifier,
    variant: AlertVariant = AlertVariant.Default,
    icon: ImageVector? = null,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    val colors = SparrowTheme.colors
    
    val (backgroundColor, borderColor, iconColor, titleColor, descriptionColor) = when (variant) {
        AlertVariant.Default -> listOf(
            colors.card,
            colors.border,
            colors.foreground,
            colors.foreground,
            colors.foreground.copy(alpha = 0.8f)
        )
        AlertVariant.Destructive -> listOf(
            colors.destructive.copy(alpha = 0.1f),
            colors.destructive.copy(alpha = 0.5f),
            colors.destructive,
            colors.destructive,
            colors.destructive.copy(alpha = 0.8f)
        )
        AlertVariant.Warning -> listOf(
            colors.warning.copy(alpha = 0.1f),
            colors.warning.copy(alpha = 0.5f),
            colors.warning,
            colors.warning,
            colors.warning.copy(alpha = 0.8f)
        )
        AlertVariant.Success -> listOf(
            colors.success.copy(alpha = 0.1f),
            colors.success.copy(alpha = 0.5f),
            colors.success,
            colors.success,
            colors.success.copy(alpha = 0.8f)
        )
        AlertVariant.Info -> listOf(
            colors.info.copy(alpha = 0.1f),
            colors.info.copy(alpha = 0.5f),
            colors.info,
            colors.info,
            colors.info.copy(alpha = 0.8f)
        )
    }
    
    // Default icons for each variant
    val defaultIcon = icon ?: when (variant) {
        AlertVariant.Default -> Icons.Default.Info
        AlertVariant.Destructive -> Icons.Default.Warning
        AlertVariant.Warning -> Icons.Default.Warning
        AlertVariant.Success -> Icons.Default.CheckCircle
        AlertVariant.Info -> Icons.Default.Info
    }
    
    Box(
        modifier = modifier
            .background(
                color = backgroundColor as Color,
                shape = RoundedCornerShape(SparrowBorderRadius.lg)
            )
            .border(
                width = 1.dp,
                color = borderColor as Color,
                shape = RoundedCornerShape(SparrowBorderRadius.lg)
            )
            .padding(SparrowSpacing.lg)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.md),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Icon(
                imageVector = defaultIcon,
                contentDescription = null,
                tint = iconColor as Color,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = if (title != null) 2.dp else 0.dp)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SparrowSpacing.xs)
            ) {
                title?.let {
                    SText(
                        text = it,
                        style = TextStyle.Small,
                        color = titleColor as Color,
                        modifier = Modifier
                    )
                }
                
                SText(
                    text = description,
                    style = TextStyle.Small,
                    color = descriptionColor as Color,
                    modifier = Modifier
                )
                
                action?.let {
                    Box(
                        modifier = Modifier.padding(top = SparrowSpacing.sm)
                    ) {
                        it()
                    }
                }
            }
            
            // Dismiss button
            if (dismissible && onDismiss != null) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = (iconColor as Color).copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onDismiss() }
                        .padding(top = if (title != null) 2.dp else 0.dp)
                )
            }
        }
    }
}

/**
 * Toast notification component with auto-dismiss
 */
@Composable
fun ShadcnToast(
    message: String,
    modifier: Modifier = Modifier,
    variant: AlertVariant = AlertVariant.Default,
    icon: ImageVector? = null,
    duration: Long = 4000L,
    action: (@Composable () -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    var visible by remember { mutableStateOf(true) }
    
    // Auto-dismiss after duration
    LaunchedEffect(Unit) {
        delay(duration)
        visible = false
        delay(300) // Wait for exit animation
        onDismiss?.invoke()
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        Alert(
            description = message,
            modifier = modifier,
            variant = variant,
            icon = icon,
            dismissible = true,
            onDismiss = {
                visible = false
            },
            action = action
        )
    }
}

/**
 * Banner component for persistent messages
 */
@Composable
fun ShadcnBanner(
    message: String,
    modifier: Modifier = Modifier,
    variant: AlertVariant = AlertVariant.Info,
    icon: ImageVector? = null,
    dismissible: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    val colors = SparrowTheme.colors
    
    val (backgroundColor, textColor, borderColor) = when (variant) {
        AlertVariant.Default -> Triple(
            colors.muted,
            colors.foreground,
            colors.border
        )
        AlertVariant.Destructive -> Triple(
            colors.destructive,
            colors.destructiveForeground,
            colors.destructive
        )
        AlertVariant.Warning -> Triple(
            colors.warning,
            colors.foreground,
            colors.warning
        )
        AlertVariant.Success -> Triple(
            colors.success,
            colors.primaryForeground,
            colors.success
        )
        AlertVariant.Info -> Triple(
            colors.info,
            colors.primaryForeground,
            colors.info
        )
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .padding(
                horizontal = SparrowSpacing.lg,
                vertical = SparrowSpacing.md
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            SText(
                text = message,
                style = TextStyle.Small,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            
            action?.let {
                it()
                Spacer(modifier = Modifier.width(SparrowSpacing.sm))
            }
            
            if (dismissible && onDismiss != null) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = textColor.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onDismiss() }
                )
            }
        }
    }
}

/**
 * Inline alert for form validation and contextual messages
 */
@Composable
fun InlineAlert(
    message: String,
    modifier: Modifier = Modifier,
    variant: AlertVariant = AlertVariant.Destructive,
    icon: ImageVector? = null
) {
    val colors = SparrowTheme.colors
    
    val (iconColor, textColor) = when (variant) {
        AlertVariant.Default -> Pair(colors.foreground, colors.foreground)
        AlertVariant.Destructive -> Pair(colors.destructive, colors.destructive)
        AlertVariant.Warning -> Pair(colors.warning, colors.warning)
        AlertVariant.Success -> Pair(colors.success, colors.success)
        AlertVariant.Info -> Pair(colors.info, colors.info)
    }
    
    val defaultIcon = icon ?: when (variant) {
        AlertVariant.Default -> Icons.Default.Info
        AlertVariant.Destructive -> Icons.Default.Warning
        AlertVariant.Warning -> Icons.Default.Warning
        AlertVariant.Success -> Icons.Default.CheckCircle
        AlertVariant.Info -> Icons.Default.Info
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = defaultIcon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(14.dp)
        )
        
        SText(
            text = message,
            style = TextStyle.Small,
            color = textColor
        )
    }
}

/**
 * Collection of commonly used alert variants
 */
object Alerts {
    @Composable
    fun Error(
        message: String,
        modifier: Modifier = Modifier,
        title: String? = "Error",
        dismissible: Boolean = false,
        onDismiss: (() -> Unit)? = null
    ) = Alert(
        title = title,
        description = message,
        modifier = modifier,
        variant = AlertVariant.Destructive,
        dismissible = dismissible,
        onDismiss = onDismiss
    )
    
    @Composable
    fun Success(
        message: String,
        modifier: Modifier = Modifier,
        title: String? = "Success",
        dismissible: Boolean = false,
        onDismiss: (() -> Unit)? = null
    ) = Alert(
        title = title,
        description = message,
        modifier = modifier,
        variant = AlertVariant.Success,
        dismissible = dismissible,
        onDismiss = onDismiss
    )
    
    @Composable
    fun Warning(
        message: String,
        modifier: Modifier = Modifier,
        title: String? = "Warning",
        dismissible: Boolean = false,
        onDismiss: (() -> Unit)? = null
    ) = Alert(
        title = title,
        description = message,
        modifier = modifier,
        variant = AlertVariant.Warning,
        dismissible = dismissible,
        onDismiss = onDismiss
    )
    
    @Composable
    fun Info(
        message: String,
        modifier: Modifier = Modifier,
        title: String? = "Info",
        dismissible: Boolean = false,
        onDismiss: (() -> Unit)? = null
    ) = Alert(
        title = title,
        description = message,
        modifier = modifier,
        variant = AlertVariant.Info,
        dismissible = dismissible,
        onDismiss = onDismiss
    )
}
