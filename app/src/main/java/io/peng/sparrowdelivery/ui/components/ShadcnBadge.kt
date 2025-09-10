package io.peng.sparrowdelivery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import io.peng.sparrowdelivery.ui.theme.*

enum class SparrowBadge {
    Default,
    Secondary,
    Destructive,
    Outline,
    Success,
    Warning,
    Info
}

enum class SparrowBadgeSize {
    Small,
    Default,
    Large
}


@Composable
fun SparrowBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: SparrowBadge = SparrowBadge.Default,
    size: SparrowBadgeSize = SparrowBadgeSize.Default,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    val colors = SparrowTheme.colors
    
    val (backgroundColor, textColor, borderColor) = when (variant) {
        SparrowBadge.Default -> Triple(
            colors.primary,
            colors.primaryForeground,
            Color.Transparent
        )
        SparrowBadge.Secondary -> Triple(
            colors.secondary,
            colors.secondaryForeground,
            Color.Transparent
        )
        SparrowBadge.Destructive -> Triple(
            colors.destructive,
            colors.destructiveForeground,
            Color.Transparent
        )
        SparrowBadge.Outline -> Triple(
            Color.Transparent,
            colors.foreground,
            colors.border
        )
        SparrowBadge.Success -> Triple(
            colors.success,
            colors.primaryForeground,
            Color.Transparent
        )
        SparrowBadge.Warning -> Triple(
            colors.warning,
            colors.foreground,
            Color.Transparent
        )
        SparrowBadge.Info -> Triple(
            colors.info,
            colors.primaryForeground,
            Color.Transparent
        )
    }
    
    val horizontalPadding = when (size) {
        SparrowBadgeSize.Small -> SparrowSpacing.xs
        SparrowBadgeSize.Default -> SparrowSpacing.sm
        SparrowBadgeSize.Large -> SparrowSpacing.md
    }
    
    val verticalPadding = when (size) {
        SparrowBadgeSize.Small -> 2.dp
        SparrowBadgeSize.Default -> 4.dp
        SparrowBadgeSize.Large -> SparrowSpacing.xs
    }
    
    val textStyle = when (size) {
        SparrowBadgeSize.Small -> SparrowTypography.small.copy(fontWeight = FontWeight.Medium)
        SparrowBadgeSize.Default -> SparrowTypography.small.copy(fontWeight = FontWeight.Medium)
        SparrowBadgeSize.Large -> SparrowTypography.p.copy(fontWeight = FontWeight.Medium)
    }
    
    val iconSize = when (size) {
        SparrowBadgeSize.Small -> 12.dp
        SparrowBadgeSize.Default -> 14.dp
        SparrowBadgeSize.Large -> 16.dp
    }
    
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(SparrowBorderRadius.md)
            )
            .let { mod ->
                if (borderColor != Color.Transparent) {
                    mod.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(SparrowBorderRadius.md)
                    )
                } else mod
            }
            .let { mod ->
                if (onClick != null) {
                    mod.clickable { onClick() }
                } else mod
            }
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.xs)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(iconSize)
                )
            }
            
            Text(
                text = text,
                style = textStyle,
                color = textColor
            )
        }
    }
}


@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier,
    variant: SparrowBadge = SparrowBadge.Secondary,
    size: SparrowBadgeSize = SparrowBadgeSize.Default,
    icon: ImageVector? = null,
    onRemove: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val colors = SparrowTheme.colors
    
    val (backgroundColor, textColor, borderColor) = when (variant) {
        SparrowBadge.Default -> Triple(
            colors.primary.copy(alpha = 0.1f),
            colors.primary,
            colors.primary.copy(alpha = 0.2f)
        )
        SparrowBadge.Secondary -> Triple(
            colors.secondary,
            colors.secondaryForeground,
            colors.border
        )
        SparrowBadge.Destructive -> Triple(
            colors.destructive.copy(alpha = 0.1f),
            colors.destructive,
            colors.destructive.copy(alpha = 0.2f)
        )
        SparrowBadge.Outline -> Triple(
            Color.Transparent,
            colors.foreground,
            colors.border
        )
        SparrowBadge.Success -> Triple(
            colors.success.copy(alpha = 0.1f),
            colors.success,
            colors.success.copy(alpha = 0.2f)
        )
        SparrowBadge.Warning -> Triple(
            colors.warning.copy(alpha = 0.1f),
            colors.warning,
            colors.warning.copy(alpha = 0.2f)
        )
        SparrowBadge.Info -> Triple(
            colors.info.copy(alpha = 0.1f),
            colors.info,
            colors.info.copy(alpha = 0.2f)
        )
    }
    
    val horizontalPadding = when (size) {
        SparrowBadgeSize.Small -> SparrowSpacing.xs
        SparrowBadgeSize.Default -> SparrowSpacing.sm
        SparrowBadgeSize.Large -> SparrowSpacing.md
    }
    
    val verticalPadding = when (size) {
        SparrowBadgeSize.Small -> 2.dp
        SparrowBadgeSize.Default -> 4.dp
        SparrowBadgeSize.Large -> SparrowSpacing.xs
    }
    
    val textStyle = when (size) {
        SparrowBadgeSize.Small -> SparrowTypography.small
        SparrowBadgeSize.Default -> SparrowTypography.small
        SparrowBadgeSize.Large -> SparrowTypography.p
    }
    
    val iconSize = when (size) {
        SparrowBadgeSize.Small -> 12.dp
        SparrowBadgeSize.Default -> 14.dp
        SparrowBadgeSize.Large -> 16.dp
    }
    
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(SparrowBorderRadius.lg)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(SparrowBorderRadius.lg)
            )
            .let { mod ->
                if (onClick != null) {
                    mod.clickable { onClick() }
                } else mod
            }
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.xs)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(iconSize)
                )
            }
            
            Text(
                text = text,
                style = textStyle,
                color = textColor
            )
            
            onRemove?.let {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = textColor,
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { onRemove() }
                )
            }
        }
    }
}

/**
 * Status indicator dot
 */
@Composable
fun ShadcnStatusDot(
    modifier: Modifier = Modifier,
    variant: SparrowBadge = SparrowBadge.Default,
    size: Dp = 8.dp
) {
    val colors = SparrowTheme.colors
    
    val dotColor = when (variant) {
        SparrowBadge.Default -> colors.primary
        SparrowBadge.Secondary -> colors.mutedForeground
        SparrowBadge.Destructive -> colors.destructive
        SparrowBadge.Outline -> colors.border
        SparrowBadge.Success -> colors.success
        SparrowBadge.Warning -> colors.warning
        SparrowBadge.Info -> colors.info
    }
    
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = dotColor,
                shape = RoundedCornerShape(size / 2)
            )
    )
}

/**
 * Notification badge with count
 */
@Composable
fun ShadcnNotificationBadge(
    count: Int,
    modifier: Modifier = Modifier,
    variant: SparrowBadge = SparrowBadge.Destructive,
    maxCount: Int = 99,
    showZero: Boolean = false
) {
    if (count == 0 && !showZero) return
    
    val displayText = if (count > maxCount) "$maxCount+" else count.toString()
    
    SparrowBadge(
        text = displayText,
        modifier = modifier,
        variant = variant,
        size = SparrowBadgeSize.Small
    )
}

/**
 * Versatile tag component
 */
@Composable
fun ShadcnTag(
    text: String,
    modifier: Modifier = Modifier,
    variant: SparrowBadge = SparrowBadge.Secondary,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    val colors = SparrowTheme.colors
    
    val actualVariant = if (selected) SparrowBadge.Default else variant
    
    if (onRemove != null) {
        Chip(
            text = text,
            modifier = modifier,
            variant = actualVariant,
            onClick = onClick,
            onRemove = onRemove
        )
    } else {
        SparrowBadge(
            text = text,
            modifier = modifier,
            variant = actualVariant,
            onClick = onClick
        )
    }
}

/**
 * Collection of commonly used badge variants
 */
object SparrowBadges {
    @Composable
    fun Online(modifier: Modifier = Modifier) = SparrowBadge(
        text = "Online",
        modifier = modifier,
        variant = SparrowBadge.Success,
        size = SparrowBadgeSize.Small
    )
    
    @Composable
    fun Offline(modifier: Modifier = Modifier) = SparrowBadge(
        text = "Offline",
        modifier = modifier,
        variant = SparrowBadge.Secondary,
        size = SparrowBadgeSize.Small
    )
    
    @Composable
    fun New(modifier: Modifier = Modifier) = SparrowBadge(
        text = "New",
        modifier = modifier,
        variant = SparrowBadge.Default,
        size = SparrowBadgeSize.Small
    )
    
    @Composable
    fun Beta(modifier: Modifier = Modifier) = SparrowBadge(
        text = "Beta",
        modifier = modifier,
        variant = SparrowBadge.Warning,
        size = SparrowBadgeSize.Small
    )
    
    @Composable
    fun Error(modifier: Modifier = Modifier) = SparrowBadge(
        text = "Error",
        modifier = modifier,
        variant = SparrowBadge.Destructive,
        size = SparrowBadgeSize.Small
    )
}
