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
 * Stitch Text Style variants
 */
enum class StitchTextStyle {
    H1, H2, H3, H4, H5, H6,
    BodyLarge, BodyMedium, BodySmall,
    LabelLarge, LabelMedium, LabelSmall,
    Caption, Overline,
    Destructive, // For error/destructive text
    // Additional commonly used text styles
    P,     // Paragraph text (alias for BodyMedium)
    Small, // Small text (alias for LabelSmall)
    Muted  // Muted text style
}

/**
 * Get TextStyle and color for StitchTextStyle
 */
@Composable
fun StitchTextStyle.toTextStyle(): TextStyle {
    return when (this) {
        StitchTextStyle.H1 -> MaterialTheme.typography.headlineLarge
        StitchTextStyle.H2 -> MaterialTheme.typography.headlineMedium
        StitchTextStyle.H3 -> MaterialTheme.typography.headlineSmall
        StitchTextStyle.H4 -> MaterialTheme.typography.titleLarge
        StitchTextStyle.H5 -> MaterialTheme.typography.titleMedium
        StitchTextStyle.H6 -> MaterialTheme.typography.titleSmall
        StitchTextStyle.BodyLarge -> MaterialTheme.typography.bodyLarge
        StitchTextStyle.BodyMedium, StitchTextStyle.P -> MaterialTheme.typography.bodyMedium
        StitchTextStyle.BodySmall -> MaterialTheme.typography.bodySmall
        StitchTextStyle.LabelLarge -> MaterialTheme.typography.labelLarge
        StitchTextStyle.LabelMedium -> MaterialTheme.typography.labelMedium
        StitchTextStyle.LabelSmall, StitchTextStyle.Small -> MaterialTheme.typography.labelSmall
        StitchTextStyle.Caption -> MaterialTheme.typography.labelSmall
        StitchTextStyle.Overline -> MaterialTheme.typography.labelSmall
        StitchTextStyle.Destructive -> MaterialTheme.typography.bodyMedium
        StitchTextStyle.Muted -> MaterialTheme.typography.bodySmall
    }
}

@Composable
fun StitchTextStyle.toColor(): Color {
    val colors = LocalStitchColorScheme.current
    return when (this) {
        StitchTextStyle.Destructive -> colors.error
        StitchTextStyle.Muted -> colors.textMuted
        else -> colors.onSurface
    }
}

/**
 * Stitch Text component with StitchTextStyle
 */
@Composable
fun StitchText(
    text: String,
    modifier: Modifier = Modifier,
    stitchStyle: StitchTextStyle,
    color: Color? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true
) {
    Text(
        text = text,
        style = stitchStyle.toTextStyle(),
        color = color ?: stitchStyle.toColor(),
        fontWeight = fontWeight,
        modifier = modifier,
        textAlign = textAlign,
        textDecoration = textDecoration,
        maxLines = maxLines,
        softWrap = softWrap
    )
}

/**
 * Stitch Text component with flexible style support
 * Handles both TextStyle and StitchTextStyle for backward compatibility
 */
@Composable
fun StitchText(
    text: String,
    modifier: Modifier = Modifier,
    style: Any? = null, // Can be TextStyle or StitchTextStyle
    color: Color? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true
) {
    val stitchColors = LocalStitchColorScheme.current
    
    // Handle both StitchTextStyle and TextStyle
    val (finalStyle, finalColor) = when (style) {
        is StitchTextStyle -> Pair(style.toTextStyle(), color ?: style.toColor())
        is TextStyle -> Pair(style, color ?: stitchColors.onSurface)
        null -> Pair(MaterialTheme.typography.bodyLarge, color ?: stitchColors.onSurface)
        else -> Pair(MaterialTheme.typography.bodyLarge, color ?: stitchColors.onSurface)
    }
    
    Text(
        text = text,
        style = finalStyle,
        color = finalColor,
        fontWeight = fontWeight,
        modifier = modifier,
        textAlign = textAlign,
        textDecoration = textDecoration,
        maxLines = maxLines,
        softWrap = softWrap
    )
}
