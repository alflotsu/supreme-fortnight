package io.peng.sparrowdelivery.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.*

enum class ShadcnButtonVariant {
    Default,
    Destructive,
    Outline,
    Secondary,
    Ghost,
    Link
}

enum class ShadcnButtonSize {
    Small,
    Default,
    Large,
    Icon
}

@Composable
fun ShadcnButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnButtonVariant = ShadcnButtonVariant.Default,
    size: ShadcnButtonSize = ShadcnButtonSize.Default,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    content: @Composable RowScope.() -> Unit
) {
    val colors = ShadcnTheme.colors
    
    val (backgroundColor, contentColor, borderStroke, elevation) = when (variant) {
        ShadcnButtonVariant.Default -> Quadruple(
            colors.primary,
            colors.primaryForeground,
            null,
            ShadcnElevation.sm
        )
        ShadcnButtonVariant.Destructive -> Quadruple(
            colors.destructive,
            colors.destructiveForeground,
            null,
            ShadcnElevation.sm
        )
        ShadcnButtonVariant.Outline -> Quadruple(
            Color.Transparent,
            colors.foreground,
            BorderStroke(1.dp, colors.border),
            0.dp
        )
        ShadcnButtonVariant.Secondary -> Quadruple(
            colors.secondary,
            colors.secondaryForeground,
            null,
            0.dp
        )
        ShadcnButtonVariant.Ghost -> Quadruple(
            Color.Transparent,
            colors.foreground,
            null,
            0.dp
        )
        ShadcnButtonVariant.Link -> Quadruple(
            Color.Transparent,
            colors.primary,
            null,
            0.dp
        )
    }
    
    val (horizontalPadding, verticalPadding, textStyle) = when (size) {
        ShadcnButtonSize.Small -> Triple(
            ShadcnSpacing.sm,
            ShadcnSpacing.xs,
            ShadcnTypography.small
        )
        ShadcnButtonSize.Default -> Triple(
            ShadcnSpacing.md,
            ShadcnSpacing.sm,
            ShadcnTypography.p
        )
        ShadcnButtonSize.Large -> Triple(
            ShadcnSpacing.lg,
            ShadcnSpacing.md,
            ShadcnTypography.large
        )
        ShadcnButtonSize.Icon -> Triple(
            ShadcnSpacing.sm,
            ShadcnSpacing.sm,
            ShadcnTypography.p
        )
    }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = if (enabled) elevation else 0.dp,
                shape = RoundedCornerShape(ShadcnBorderRadius.md),
                ambientColor = colors.foreground.copy(alpha = 0.1f),
                spotColor = colors.foreground.copy(alpha = 0.1f)
            ),
        enabled = enabled,
        shape = RoundedCornerShape(ShadcnBorderRadius.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        border = borderStroke,
        contentPadding = PaddingValues(
            horizontal = horizontalPadding,
            vertical = verticalPadding
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            content()
            
            trailingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ShadcnTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnButtonVariant = ShadcnButtonVariant.Default,
    size: ShadcnButtonSize = ShadcnButtonSize.Default,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    ShadcnButton(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    ) {
        val textStyle = when (size) {
            ShadcnButtonSize.Small -> ShadcnTypography.small
            ShadcnButtonSize.Default -> ShadcnTypography.p
            ShadcnButtonSize.Large -> ShadcnTypography.large
            ShadcnButtonSize.Icon -> ShadcnTypography.p
        }
        
        Text(
            text = text,
            style = textStyle.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun ShadcnIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ShadcnButtonVariant = ShadcnButtonVariant.Ghost,
    size: ShadcnButtonSize = ShadcnButtonSize.Icon,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    ShadcnButton(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Helper data class for quadruple return type
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
