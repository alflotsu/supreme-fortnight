package io.peng.sparrowdelivery.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.ShadcnTheme
import io.peng.sparrowdelivery.ui.theme.ShadcnBorderRadius
import io.peng.sparrowdelivery.ui.theme.ShadcnElevation
import io.peng.sparrowdelivery.ui.theme.ShadcnSpacing

enum class ShadcnCardVariant {
    Default,
    Elevated,
    Outlined,
    Ghost
}

@Composable
fun ShadcnCard(
    modifier: Modifier = Modifier,
    variant: ShadcnCardVariant = ShadcnCardVariant.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = ShadcnTheme.colors
    
    val (backgroundColor, borderStroke, elevation) = when (variant) {
        ShadcnCardVariant.Default -> Triple(
            colors.card,
            BorderStroke(1.dp, colors.border),
            ShadcnElevation.sm
        )
        ShadcnCardVariant.Elevated -> Triple(
            colors.card,
            BorderStroke(1.dp, colors.border),
            ShadcnElevation.lg
        )
        ShadcnCardVariant.Outlined -> Triple(
            Color.Transparent,
            BorderStroke(2.dp, colors.border),
            0.dp
        )
        ShadcnCardVariant.Ghost -> Triple(
            Color.Transparent,
            null,
            0.dp
        )
    }
    
    Card(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(ShadcnBorderRadius.lg),
                ambientColor = colors.foreground.copy(alpha = 0.1f),
                spotColor = colors.foreground.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(ShadcnBorderRadius.lg),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = borderStroke,
        onClick = onClick ?: {},
        content = {
            Column(
                modifier = Modifier.padding(ShadcnSpacing.lg),
                content = content
            )
        }
    )
}

@Composable
fun ShadcnCompactCard(
    modifier: Modifier = Modifier,
    variant: ShadcnCardVariant = ShadcnCardVariant.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = ShadcnTheme.colors
    
    val (backgroundColor, borderStroke, elevation) = when (variant) {
        ShadcnCardVariant.Default -> Triple(
            colors.card,
            BorderStroke(1.dp, colors.border),
            ShadcnElevation.sm
        )
        ShadcnCardVariant.Elevated -> Triple(
            colors.card,
            BorderStroke(1.dp, colors.border),
            ShadcnElevation.md
        )
        ShadcnCardVariant.Outlined -> Triple(
            Color.Transparent,
            BorderStroke(1.dp, colors.border),
            0.dp
        )
        ShadcnCardVariant.Ghost -> Triple(
            Color.Transparent,
            null,
            0.dp
        )
    }
    
    Card(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(ShadcnBorderRadius.md),
                ambientColor = colors.foreground.copy(alpha = 0.08f),
                spotColor = colors.foreground.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(ShadcnBorderRadius.md),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = borderStroke,
        onClick = onClick ?: {},
        content = {
            Column(
                modifier = Modifier.padding(ShadcnSpacing.md),
                content = content
            )
        }
    )
}

@Composable
fun ShadcnSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
    ) {
        if (title != null || description != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.xs)
            ) {
                title?.let {
                    ShadcnText(
                        text = it,
                        style = ShadcnTextStyle.H4,
                        color = ShadcnTheme.colors.foreground
                    )
                }
                description?.let {
                    ShadcnText(
                        text = it,
                        style = ShadcnTextStyle.Muted,
                        color = ShadcnTheme.colors.mutedForeground
                    )
                }
            }
        }
        
        content()
    }
}
