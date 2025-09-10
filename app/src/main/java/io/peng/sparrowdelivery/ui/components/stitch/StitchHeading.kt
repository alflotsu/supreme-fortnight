package io.peng.sparrowdelivery.ui.components.stitch

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme

/**
 * Stitch Heading component
 * Displays a heading with the appropriate styling from the Stitch design system
 */
@Composable
fun StitchHeading(
    text: String,
    modifier: Modifier = Modifier,
    level: Int = 1, // 1-6, corresponding to HTML heading levels
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null
) {
    val stitchColors = LocalStitchColorScheme.current
    
    val textStyle = when (level) {
        1 -> MaterialTheme.typography.displayLarge
        2 -> MaterialTheme.typography.displayMedium
        3 -> MaterialTheme.typography.displaySmall
        4 -> MaterialTheme.typography.headlineLarge
        5 -> MaterialTheme.typography.headlineMedium
        6 -> MaterialTheme.typography.headlineSmall
        else -> MaterialTheme.typography.headlineMedium
    }
    
    val fontWeight = when (level) {
        1 -> FontWeight.Bold
        2 -> FontWeight.Bold
        3 -> FontWeight.Bold
        4 -> FontWeight.Bold
        5 -> FontWeight.SemiBold
        6 -> FontWeight.SemiBold
        else -> FontWeight.Normal
    }
    
    Text(
        text = text,
        style = textStyle,
        fontWeight = fontWeight,
        color = color ?: stitchColors.onSurface,
        modifier = modifier,
        textAlign = textAlign
    )
}
