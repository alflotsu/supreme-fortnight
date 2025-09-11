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
import io.peng.sparrowdelivery.ui.theme.SparrowTheme
import io.peng.sparrowdelivery.ui.theme.SparrowBorderRadius
import io.peng.sparrowdelivery.ui.theme.SparrowElevation
import io.peng.sparrowdelivery.ui.theme.SparrowSpacing

enum class CardVariant {
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
    val colors = SparrowTheme.colors
    
    val (backgroundColor, borderStroke, elevation) = when (variant) {
        ShadcnCardVariant.Default -> Triple(
            colors.card,
            BorderStroke(1.dp, colors.border),
            SparrowElevation.sm
        )
        ShadcnCardVariant.Elevated -> Triple(
            colors.card,
            BorderStroke(1.dp, colors.border),
            SparrowElevation.lg
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
                shape = RoundedCornerShape(SparrowBorderRadius.lg),
                ambientColor = colors.foreground.copy(alpha = 0.1f),
                spotColor = colors.foreground.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(SparrowBorderRadius.lg),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = borderStroke,
        onClick = onClick ?: {},
        content = {
            Column(
                modifier = Modifier.padding(SparrowSpacing.lg),
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
    val colors = SparrowTheme.colors
    
    val (backgroundColor, borderStroke, elevation) = when (variant) {
        ShadcnCardVariant.Default -> Triple(
            colors.card,
            BorderStroke(1.dp, colors.border),
            SparrowElevation.sm
        )
        ShadcnCardVariant.Elevated -> Triple(
            colors.card,
            BorderStroke(1.dp, colors.border),
            SparrowElevation.md
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
                shape = RoundedCornerShape(SparrowBorderRadius.md),
                ambientColor = colors.foreground.copy(alpha = 0.08f),
                spotColor = colors.foreground.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(SparrowBorderRadius.md),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = borderStroke,
        onClick = onClick ?: {},
        content = {
            Column(
                modifier = Modifier.padding(SparrowSpacing.md),
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
        verticalArrangement = Arrangement.spacedBy(SparrowSpacing.md)
    ) {
        if (title != null || description != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(SparrowSpacing.xs)
            ) {
                title?.let {
                    ShadcnText(
                        text = it,
                        style = ShadcnTextStyle.H4,
                        color = SparrowTheme.colors.foreground
                    )
                }
                description?.let {
                    ShadcnText(
                        text = it,
                        style = ShadcnTextStyle.Muted,
                        color = SparrowTheme.colors.mutedForeground
                    )
                }
            }
        }
        
        content()
    }
}
