package io.peng.sparrowdelivery.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.SparrowSpacing
import io.peng.sparrowdelivery.ui.components.stitch.*

enum class CardVariant {
    Default,
    Elevated,
    Outlined,
    Ghost
}

@Composable
fun SparrowCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Default,
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
fun CompactCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    // Map to StitchCard with smaller padding and click handling
    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }
    
    StitchCard(
        modifier = clickableModifier,
        content = {
            Column(
                modifier = Modifier.padding(8.dp), // Smaller padding for compact card
                content = content
            )
        }
    )
}

@Composable
fun Section(
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
