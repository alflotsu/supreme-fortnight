package io.peng.sparrowdelivery.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.peng.sparrowdelivery.ui.components.stitch.*

enum class TextStyle {
    H1,
    H2,
    H3,
    H4,
    P,
    Large,
    Small,
    Muted,
    Lead
}

@Composable
fun SText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.P,
    color: Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    // Map to StitchText or StitchHeading based on style
    when (style) {
        TextStyle.H1, TextStyle.H2, TextStyle.H3, TextStyle.H4 -> {
            val level = when (style) {
                TextStyle.H1 -> 1
                TextStyle.H2 -> 2
                TextStyle.H3 -> 3
                else -> 4
            }
            StitchHeading(
                text = text,
                modifier = modifier,
                level = level,
                color = color,
                textAlign = textAlign
            )
        }
        else -> {
            StitchText(
                text = text,
                modifier = modifier,
                color = color,
                textAlign = textAlign,
                maxLines = maxLines
            )
        }
    }
}

@Composable
fun ShadcnHeading(
    text: String,
    modifier: Modifier = Modifier,
    level: Int = 1,
    color: Color? = null,
    textAlign: TextAlign? = null
) {
    // Map directly to StitchHeading
    StitchHeading(
        text = text,
        modifier = modifier,
        level = level,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun Paragraph(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    // Map to StitchText
    StitchText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines
    )
}

@Composable
fun ShadcnMutedText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    // Map to StitchText with muted color
    StitchText(
        text = text,
        modifier = modifier,
        color = androidx.compose.ui.graphics.Color.Gray, // Use appropriate muted color from Stitch theme
        textAlign = textAlign,
        maxLines = maxLines
    )
}

@Composable
fun SmallText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    // Map to StitchText
    StitchText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines
    )
}
