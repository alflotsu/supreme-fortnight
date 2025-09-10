package io.peng.sparrowdelivery.ui.components.stitch

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme

/**
 * Stitch Text component
 * A flexible text component that follows the Stitch design system
 */
@Composable
fun StitchText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    color: Color? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Text(
        text = text,
        style = style ?: MaterialTheme.typography.bodyLarge,
        color = color ?: stitchColors.onSurface,
        fontWeight = fontWeight,
        modifier = modifier,
        textAlign = textAlign,
        textDecoration = textDecoration,
        maxLines = maxLines,
        softWrap = softWrap
    )
}
