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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import io.peng.sparrowdelivery.ui.theme.*

enum class ShadcnBadgeVariant {
    Default,
    Secondary,
    Destructive,
    Outline,
    Success,
    Warning,
    Info
}

enum class ShadcnBadgeSize {
    Small,
    Default,
    Large
}

/**
 * shadcn/ui inspired Badge component for status indicators and labels
 */
@Composable
fun ShadcnBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: ShadcnBadgeVariant = ShadcnBadgeVariant.Default,
    size: ShadcnBadgeSize = ShadcnBadgeSize.Default,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    val colors = SparrowTheme.colors
    
    val (backgroundColor, textColor, borderColor) = when (variant) {
        ShadcnBadgeVariant.Default -> Triple(
            colors.primary,
            colors.primaryForeground,
            Color.Transparent
        )
        ShadcnBadgeVariant.Secondary -> Triple(
            colors.secondary,
            colors.secondaryForeground,
            Color.Transparent
        )
        ShadcnBadgeVariant.Destructive -> Triple(
            colors.destructive,
            colors.destructiveForeground,
            Color.Transparent
        )
        ShadcnBadgeVariant.Outline -> Triple(
            Color.Transparent,
            colors.foreground,
            colors.border
        )
        ShadcnBadgeVariant.Success -> Triple(
            colors.success,
            colors.primaryForeground,
            Color.Transparent
        )
        ShadcnBadgeVariant.Warning -> Triple(
            colors.warning,
            colors.foreground,
            Color.Transparent
        )
        ShadcnBadgeVariant.Info -> Triple(
            colors.info,
            colors.primaryForeground,
            Color.Transparent
        )
    }
    
    val horizontalPadding = when (size) {
        ShadcnBadgeSize.Small -> SparrowSpacing.xs
        ShadcnBadgeSize.Default -> SparrowSpacing.sm
        ShadcnBadgeSize.Large -> SparrowSpacing.md
    }
    
    val verticalPadding = when (size) {
        ShadcnBadgeSize.Small -> 2.dp
        ShadcnBadgeSize.Default -> 4.dp
        ShadcnBadgeSize.Large -> SparrowSpacing.xs
    }
    
    val textStyle = when (size) {
        ShadcnBadgeSize.Small -> SparrowTypography.small.copy(fontWeight = FontWeight.Medium)
        ShadcnBadgeSize.Default -> SparrowTypography.small.copy(fontWeight = FontWeight.Medium)
        ShadcnBadgeSize.Large -> SparrowTypography.p.copy(fontWeight = FontWeight.Medium)
    }
    
    val iconSize = when (size) {
        ShadcnBadgeSize.Small -> 12.dp
        ShadcnBadgeSize.Default -> 14.dp
        ShadcnBadgeSize.Large -> 16.dp
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

/**
 * Removable chip component similar to shadcn/ui
 */
@Composable
fun ShadcnChip(
    text: String,
    modifier: Modifier = Modifier,
    variant: ShadcnBadgeVariant = ShadcnBadgeVariant.Secondary,
    size: ShadcnBadgeSize = ShadcnBadgeSize.Default,
    icon: ImageVector? = null,
    onRemove: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val colors = SparrowTheme.colors
    
    val (backgroundColor, textColor, borderColor) = when (variant) {
        ShadcnBadgeVariant.Default -> Triple(
            colors.primary.copy(alpha = 0.1f),
            colors.primary,
            colors.primary.copy(alpha = 0.2f)
        )
        ShadcnBadgeVariant.Secondary -> Triple(
            colors.secondary,
            colors.secondaryForeground,
            colors.border
        )
        ShadcnBadgeVariant.Destructive -> Triple(
            colors.destructive.copy(alpha = 0.1f),
            colors.destructive,
            colors.destructive.copy(alpha = 0.2f)
        )
        ShadcnBadgeVariant.Outline -> Triple(
            Color.Transparent,
            colors.foreground,
            colors.border
        )
        ShadcnBadgeVariant.Success -> Triple(
            colors.success.copy(alpha = 0.1f),
            colors.success,
            colors.success.copy(alpha = 0.2f)
        )
        ShadcnBadgeVariant.Warning -> Triple(
            colors.warning.copy(alpha = 0.1f),
            colors.warning,
            colors.warning.copy(alpha = 0.2f)
        )
        ShadcnBadgeVariant.Info -> Triple(
            colors.info.copy(alpha = 0.1f),
            colors.info,
            colors.info.copy(alpha = 0.2f)
        )
    }
    
    val horizontalPadding = when (size) {
        ShadcnBadgeSize.Small -> SparrowSpacing.xs
        ShadcnBadgeSize.Default -> SparrowSpacing.sm
        ShadcnBadgeSize.Large -> SparrowSpacing.md
    }
    
    val verticalPadding = when (size) {
        ShadcnBadgeSize.Small -> 2.dp
        ShadcnBadgeSize.Default -> 4.dp
        ShadcnBadgeSize.Large -> SparrowSpacing.xs
    }
    
    val textStyle = when (size) {
        ShadcnBadgeSize.Small -> SparrowTypography.small
        ShadcnBadgeSize.Default -> SparrowTypography.small
        ShadcnBadgeSize.Large -> SparrowTypography.p
    }
    
    val iconSize = when (size) {
        ShadcnBadgeSize.Small -> 12.dp
        ShadcnBadgeSize.Default -> 14.dp
        ShadcnBadgeSize.Large -> 16.dp
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
    variant: ShadcnBadgeVariant = ShadcnBadgeVariant.Default,
    size: Dp = 8.dp
) {
    val colors = SparrowTheme.colors
    
    val dotColor = when (variant) {
        ShadcnBadgeVariant.Default -> colors.primary
        ShadcnBadgeVariant.Secondary -> colors.mutedForeground
        ShadcnBadgeVariant.Destructive -> colors.destructive
        ShadcnBadgeVariant.Outline -> colors.border
        ShadcnBadgeVariant.Success -> colors.success
        ShadcnBadgeVariant.Warning -> colors.warning
        ShadcnBadgeVariant.Info -> colors.info
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
    variant: ShadcnBadgeVariant = ShadcnBadgeVariant.Destructive,
    maxCount: Int = 99,
    showZero: Boolean = false
) {
    if (count == 0 && !showZero) return
    
    val displayText = if (count > maxCount) "$maxCount+" else count.toString()
    
    ShadcnBadge(
        text = displayText,
        modifier = modifier,
        variant = variant,
        size = ShadcnBadgeSize.Small
    )
}

/**
 * Versatile tag component
 */
@Composable
fun ShadcnTag(
    text: String,
    modifier: Modifier = Modifier,
    variant: ShadcnBadgeVariant = ShadcnBadgeVariant.Secondary,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    val colors = SparrowTheme.colors
    
    val actualVariant = if (selected) ShadcnBadgeVariant.Default else variant
    
    if (onRemove != null) {
        ShadcnChip(
            text = text,
            modifier = modifier,
            variant = actualVariant,
            onClick = onClick,
            onRemove = onRemove
        )
    } else {
        ShadcnBadge(
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
object ShadcnBadges {
    @Composable
    fun Online(modifier: Modifier = Modifier) = ShadcnBadge(
        text = "Online",
        modifier = modifier,
        variant = ShadcnBadgeVariant.Success,
        size = ShadcnBadgeSize.Small
    )
    
    @Composable
    fun Offline(modifier: Modifier = Modifier) = ShadcnBadge(
        text = "Offline",
        modifier = modifier,
        variant = ShadcnBadgeVariant.Secondary,
        size = ShadcnBadgeSize.Small
    )
    
    @Composable
    fun New(modifier: Modifier = Modifier) = ShadcnBadge(
        text = "New",
        modifier = modifier,
        variant = ShadcnBadgeVariant.Default,
        size = ShadcnBadgeSize.Small
    )
    
    @Composable
    fun Beta(modifier: Modifier = Modifier) = ShadcnBadge(
        text = "Beta",
        modifier = modifier,
        variant = ShadcnBadgeVariant.Warning,
        size = ShadcnBadgeSize.Small
    )
    
    @Composable
    fun Error(modifier: Modifier = Modifier) = ShadcnBadge(
        text = "Error",
        modifier = modifier,
        variant = ShadcnBadgeVariant.Destructive,
        size = ShadcnBadgeSize.Small
    )
}
