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
import io.peng.sparrowdelivery.ui.components.stitch.*

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
    // Map to StitchCard
    StitchCard(
        modifier = modifier,
        content = content
    )
}

@Composable
fun ShadcnCompactCard(
    modifier: Modifier = Modifier,
    variant: ShadcnCardVariant = ShadcnCardVariant.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    // Map to StitchCard with smaller padding
    StitchCard(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier.padding(8.dp), // Smaller padding for compact card
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
                    StitchHeading(
                        text = it,
                        level = 4 // H4 equivalent
                    )
                }
                description?.let {
                    StitchText(
                        text = it,
                        color = androidx.compose.ui.graphics.Color.Gray // Use appropriate muted color from Stitch theme
                    )
                }
            }
        }
        
        content()
    }
}
