package io.peng.sparrowdelivery.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.peng.sparrowdelivery.ui.theme.SparrowTheme
import io.peng.sparrowdelivery.ui.theme.SparrowTypography

enum class ShadcnTextStyle {
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
fun ShadcnText(
    text: String,
    modifier: Modifier = Modifier,
    style: ShadcnTextStyle = ShadcnTextStyle.P,
    color: Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val colors = SparrowTheme.colors
    
    val textStyle = when (style) {
        ShadcnTextStyle.H1 -> SparrowTypography.h1
        ShadcnTextStyle.H2 -> SparrowTypography.h2
        ShadcnTextStyle.H3 -> SparrowTypography.h3
        ShadcnTextStyle.H4 -> SparrowTypography.h4
        ShadcnTextStyle.P -> SparrowTypography.p
        ShadcnTextStyle.Large -> SparrowTypography.large
        ShadcnTextStyle.Small -> SparrowTypography.small
        ShadcnTextStyle.Muted -> SparrowTypography.muted
        ShadcnTextStyle.Lead -> SparrowTypography.lead
    }
    
    val textColor = color ?: when (style) {
        ShadcnTextStyle.Muted -> colors.mutedForeground
        else -> colors.foreground
    }
    
    Text(
        text = text,
        modifier = modifier,
        style = textStyle,
        color = textColor,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun ShadcnHeading(
    text: String,
    modifier: Modifier = Modifier,
    level: Int = 1,
    color: Color? = null,
    textAlign: TextAlign? = null
) {
    val style = when (level) {
        1 -> ShadcnTextStyle.H1
        2 -> ShadcnTextStyle.H2
        3 -> ShadcnTextStyle.H3
        4 -> ShadcnTextStyle.H4
        else -> ShadcnTextStyle.H4
    }
    
    ShadcnText(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun ShadcnParagraph(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    ShadcnText(
        text = text,
        modifier = modifier,
        style = ShadcnTextStyle.P,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
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
    ShadcnText(
        text = text,
        modifier = modifier,
        style = ShadcnTextStyle.Muted,
        color = SparrowTheme.colors.mutedForeground,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun ShadcnSmallText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    ShadcnText(
        text = text,
        modifier = modifier,
        style = ShadcnTextStyle.Small,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}
