package io.peng.sparrowdelivery.ui.components.stitch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme

/**
 * Stitch Card variants
 */
enum class StitchCardVariant {
    Default, Outlined, Filled, Elevated, Compact
}

/**
 * Get card properties based on variant
 */
@Composable
fun StitchCardVariant.toCardColors(): androidx.compose.material3.CardColors {
    val colors = LocalStitchColorScheme.current
    return when (this) {
        StitchCardVariant.Default -> CardDefaults.cardColors(
            containerColor = colors.surface,
            contentColor = colors.onSurface
        )
        StitchCardVariant.Outlined -> CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
            contentColor = colors.onSurface
        )
        StitchCardVariant.Filled -> CardDefaults.cardColors(
            containerColor = colors.surfaceVariant,
            contentColor = colors.onSurfaceVariant
        )
        StitchCardVariant.Elevated -> CardDefaults.elevatedCardColors(
            containerColor = colors.surface,
            contentColor = colors.onSurface
        )
        StitchCardVariant.Compact -> CardDefaults.cardColors(
            containerColor = colors.surface,
            contentColor = colors.onSurface
        )
    }
}

@Composable
fun StitchCardVariant.toElevation(): Dp {
    return when (this) {
        StitchCardVariant.Elevated -> 4.dp
        StitchCardVariant.Compact -> 0.dp
        else -> 2.dp
    }
}

@Composable
fun StitchCardVariant.toPadding(): Dp {
    return when (this) {
        StitchCardVariant.Compact -> 8.dp
        else -> 16.dp
    }
}

/**
 * Stitch Card component with variant support
 */
@Composable
fun StitchCard(
    modifier: Modifier = Modifier,
    variant: StitchCardVariant = StitchCardVariant.Default,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    elevation: Dp? = null,
    shape: Shape? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColors = variant.toCardColors()
    
    Card(
        modifier = modifier
            .let { if (onClick != null) it.clickable { onClick() } else it },
        colors = if (backgroundColor != null || contentColor != null) {
            CardDefaults.cardColors(
                containerColor = backgroundColor ?: cardColors.containerColor,
                contentColor = contentColor ?: cardColors.contentColor,
            )
        } else cardColors,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation ?: variant.toElevation()
        ),
        shape = shape ?: androidx.compose.material3.MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(variant.toPadding())
        ) {
            content()
        }
    }
}

/**
 * Legacy Stitch Card component (for backward compatibility)
 */
@Composable
fun StitchCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    elevation: Dp = 0.dp,
    shape: Shape? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor ?: stitchColors.surface,
            contentColor = contentColor ?: stitchColors.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = shape ?: androidx.compose.material3.MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * Compact Card component with optional variant override
 */
@Composable
fun CompactCard(
    modifier: Modifier = Modifier,
    variant: StitchCardVariant = StitchCardVariant.Compact,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    StitchCard(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        content = content
    )
}
