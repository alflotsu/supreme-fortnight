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

enum class StitchBadgeVariant {
    Default,
    Secondary,
    Destructive,
    Outline,
    Success,
    Warning,
    Info
}

enum class StitchBadgeSize {
    Small,
    Default,
    Large
}

@Composable
fun StitchBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: StitchBadgeVariant = StitchBadgeVariant.Default,
    size: StitchBadgeSize = StitchBadgeSize.Default,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    val stitchColors = LocalStitchColorScheme.current
    
    val (backgroundColor, textColor, borderColor) = when (variant) {
        StitchBadgeVariant.Default -> Triple(
            stitchColors.primary,
            stitchColors.onPrimary,
            Color.Transparent
        )
        StitchBadgeVariant.Secondary -> Triple(
            stitchColors.secondaryContainer,
            stitchColors.onSecondaryContainer,
            Color.Transparent
        )
        StitchBadgeVariant.Destructive -> Triple(
            stitchColors.error,
            stitchColors.onError,
            Color.Transparent
        )
        StitchBadgeVariant.Outline -> Triple(
            Color.Transparent,
            stitchColors.onSurface,
            stitchColors.outline
        )
        StitchBadgeVariant.Success -> Triple(
            stitchColors.success,
            stitchColors.onSuccess,
            Color.Transparent
        )
        StitchBadgeVariant.Warning -> Triple(
            stitchColors.warning,
            stitchColors.onWarning,
            Color.Transparent
        )
        StitchBadgeVariant.Info -> Triple(
            stitchColors.info,
            stitchColors.onInfo,
            Color.Transparent
        )
    }
    
    val horizontalPadding = when (size) {
        StitchBadgeSize.Small -> 4.dp
        StitchBadgeSize.Default -> 8.dp
        StitchBadgeSize.Large -> 16.dp
    }
    
    val verticalPadding = when (size) {
        StitchBadgeSize.Small -> 2.dp
        StitchBadgeSize.Default -> 4.dp
        StitchBadgeSize.Large -> 8.dp
    }
    
    val textStyle = when (size) {
        StitchBadgeSize.Small -> MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
        StitchBadgeSize.Default -> MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
        StitchBadgeSize.Large -> MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
    }
    
    val iconSize = when (size) {
        StitchBadgeSize.Small -> 12.dp
        StitchBadgeSize.Default -> 14.dp
        StitchBadgeSize.Large -> 16.dp
    }
    
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .let { mod ->
                if (borderColor != Color.Transparent) {
                    mod.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp)
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
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
fun StitchChip(
    text: String,
    modifier: Modifier = Modifier,
    variant: StitchBadgeVariant = StitchBadgeVariant.Secondary,
    size: StitchBadgeSize = StitchBadgeSize.Default,
    icon: ImageVector? = null,
    onRemove: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val stitchColors = LocalStitchColorScheme.current
    
    val (backgroundColor, textColor, borderColor) = when (variant) {
        StitchBadgeVariant.Default -> Triple(
            stitchColors.primary.copy(alpha = 0.1f),
            stitchColors.onPrimary,
            stitchColors.primary.copy(alpha = 0.2f)
        )
        StitchBadgeVariant.Secondary -> Triple(
            stitchColors.secondaryContainer,
            stitchColors.onSecondaryContainer,
            stitchColors.outline
        )
        StitchBadgeVariant.Destructive -> Triple(
            stitchColors.error.copy(alpha = 0.1f),
            stitchColors.onError,
            stitchColors.error.copy(alpha = 0.2f)
        )
        StitchBadgeVariant.Outline -> Triple(
            Color.Transparent,
            stitchColors.onSurface,
            stitchColors.outline
        )
        StitchBadgeVariant.Success -> Triple(
            stitchColors.success.copy(alpha = 0.1f),
            stitchColors.onSuccess,
            stitchColors.success.copy(alpha = 0.2f)
        )
        StitchBadgeVariant.Warning -> Triple(
            stitchColors.warning.copy(alpha = 0.1f),
            stitchColors.onWarning,
            stitchColors.warning.copy(alpha = 0.2f)
        )
        StitchBadgeVariant.Info -> Triple(
            stitchColors.info.copy(alpha = 0.1f),
            stitchColors.onInfo,
            stitchColors.info.copy(alpha = 0.2f)
        )
    }
    
    val horizontalPadding = when (size) {
        StitchBadgeSize.Small -> 4.dp
        StitchBadgeSize.Default -> 8.dp
        StitchBadgeSize.Large -> 16.dp
    }
    
    val verticalPadding = when (size) {
        StitchBadgeSize.Small -> 2.dp
        StitchBadgeSize.Default -> 4.dp
        StitchBadgeSize.Large -> 8.dp
    }
    
    val textStyle = when (size) {
        StitchBadgeSize.Small -> MaterialTheme.typography.labelSmall
        StitchBadgeSize.Default -> MaterialTheme.typography.labelSmall
        StitchBadgeSize.Large -> MaterialTheme.typography.labelMedium
    }
    
    val iconSize = when (size) {
        StitchBadgeSize.Small -> 12.dp
        StitchBadgeSize.Default -> 14.dp
        StitchBadgeSize.Large -> 16.dp
    }
    
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
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
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
fun StitchStatusDot(
    modifier: Modifier = Modifier,
    variant: StitchBadgeVariant = StitchBadgeVariant.Default,
    size: Dp = 8.dp
) {
    val stitchColors = LocalStitchColorScheme.current
    
    val dotColor = when (variant) {
        StitchBadgeVariant.Default -> stitchColors.primary
        StitchBadgeVariant.Secondary -> stitchColors.textSecondary
        StitchBadgeVariant.Destructive -> stitchColors.error
        StitchBadgeVariant.Outline -> stitchColors.outline
        StitchBadgeVariant.Success -> stitchColors.success
        StitchBadgeVariant.Warning -> stitchColors.warning
        StitchBadgeVariant.Info -> stitchColors.info
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
fun StitchNotificationBadge(
    count: Int,
    modifier: Modifier = Modifier,
    variant: StitchBadgeVariant = StitchBadgeVariant.Destructive,
    maxCount: Int = 99,
    showZero: Boolean = false
) {
    if (count == 0 && !showZero) return
    
    val displayText = if (count > maxCount) "$maxCount+" else count.toString()
    
    StitchBadge(
        text = displayText,
        modifier = modifier,
        variant = variant,
        size = StitchBadgeSize.Small
    )
}

/**
 * Versatile tag component
 */
@Composable
fun StitchTag(
    text: String,
    modifier: Modifier = Modifier,
    variant: StitchBadgeVariant = StitchBadgeVariant.Secondary,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    val actualVariant = if (selected) StitchBadgeVariant.Default else variant
    
    if (onRemove != null) {
        StitchChip(
            text = text,
            modifier = modifier,
            variant = actualVariant,
            onClick = onClick,
            onRemove = onRemove
        )
    } else {
        StitchBadge(
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
object StitchBadges {
    @Composable
    fun Online(modifier: Modifier = Modifier) = StitchBadge(
        text = "Online",
        modifier = modifier,
        variant = StitchBadgeVariant.Success,
        size = StitchBadgeSize.Small
    )
    
    @Composable
    fun Offline(modifier: Modifier = Modifier) = StitchBadge(
        text = "Offline",
        modifier = modifier,
        variant = StitchBadgeVariant.Secondary,
        size = StitchBadgeSize.Small
    )
    
    @Composable
    fun New(modifier: Modifier = Modifier) = StitchBadge(
        text = "New",
        modifier = modifier,
        variant = StitchBadgeVariant.Default,
        size = StitchBadgeSize.Small
    )
    
    @Composable
    fun Beta(modifier: Modifier = Modifier) = StitchBadge(
        text = "Beta",
        modifier = modifier,
        variant = StitchBadgeVariant.Warning,
        size = StitchBadgeSize.Small
    )
    
    @Composable
    fun Error(modifier: Modifier = Modifier) = StitchBadge(
        text = "Error",
        modifier = modifier,
        variant = StitchBadgeVariant.Destructive,
        size = StitchBadgeSize.Small
    )
}
