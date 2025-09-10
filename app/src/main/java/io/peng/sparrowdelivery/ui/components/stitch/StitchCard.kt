package io.peng.sparrowdelivery.ui.components.stitch

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
 * Stitch Card component
 * A container for grouping related content and actions
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
