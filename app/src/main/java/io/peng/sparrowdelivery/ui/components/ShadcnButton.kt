package io.peng.sparrowdelivery.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.components.stitch.*

enum class ShadcnButtonVariant {
    Default,
    Destructive,
    Outline,
    Secondary,
    Ghost,
    Link,
    Success
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
    val colors = SparrowTheme.colors
    
    val (backgroundColor, contentColor, borderStroke, elevation) = when (variant) {
        ShadcnButtonVariant.Default -> Quadruple(
            colors.primary,
            colors.primaryForeground,
            null,
            SparrowElevation.sm
        )
        ShadcnButtonVariant.Destructive -> Quadruple(
            colors.destructive,
            colors.destructiveForeground,
            null,
            SparrowElevation.sm
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
        ShadcnButtonVariant.Success -> Quadruple(
            colors.success ?: colors.primary,
            colors.primaryForeground,
            null,
            SparrowElevation.sm
        )
    }
    
    val (horizontalPadding, verticalPadding, textStyle) = when (size) {
        ShadcnButtonSize.Small -> Triple(
            SparrowSpacing.sm,
            SparrowSpacing.xs,
            SparrowTypography.small
        )
        ShadcnButtonSize.Default -> Triple(
            SparrowSpacing.md,
            SparrowSpacing.sm,
            SparrowTypography.p
        )
        ShadcnButtonSize.Large -> Triple(
            SparrowSpacing.lg,
            SparrowSpacing.md,
            SparrowTypography.large
        )
        ShadcnButtonSize.Icon -> Triple(
            SparrowSpacing.sm,
            SparrowSpacing.sm,
            SparrowTypography.p
        )
    }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = if (enabled) elevation else 0.dp,
                shape = RoundedCornerShape(SparrowBorderRadius.md),
                ambientColor = colors.foreground.copy(alpha = 0.1f),
                spotColor = colors.foreground.copy(alpha = 0.1f)
            ),
        enabled = enabled,
        shape = RoundedCornerShape(SparrowBorderRadius.md),
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
            horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.xs),
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
    // Map Shadcn variants to Stitch button components
    when (variant) {
        ShadcnButtonVariant.Default -> {
            StitchPrimaryButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled,
                icon = leadingIcon ?: trailingIcon
            )
        }
        ShadcnButtonVariant.Secondary -> {
            StitchSecondaryButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled
            )
        }
        ShadcnButtonVariant.Outline -> {
            StitchOutlineButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled
            )
        }
        ShadcnButtonVariant.Ghost -> {
            StitchIconButton(
                onClick = onClick,
                icon = leadingIcon ?: trailingIcon ?: Icons.Default.Add,
                variant = StitchIconButtonVariant.Secondary
            )
        }
        ShadcnButtonVariant.Link -> {
            StitchText(
                text = text,
                modifier = modifier.clickable(
                    enabled = enabled,
                    onClick = onClick
                ),
                color = androidx.compose.ui.graphics.Color.Blue // Use appropriate color from Stitch theme
            )
        }
        ShadcnButtonVariant.Destructive -> {
            StitchPrimaryButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled
            )
            // Note: Would need to customize styling for destructive variant
        }
        ShadcnButtonVariant.Success -> {
            StitchSuccessButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled
            )
        }
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
    // Map to Stitch IconButton
    StitchIconButton(
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        variant = when (variant) {
            ShadcnButtonVariant.Default -> StitchIconButtonVariant.Primary
            ShadcnButtonVariant.Secondary -> StitchIconButtonVariant.Secondary
            ShadcnButtonVariant.Success -> StitchIconButtonVariant.Success
            else -> StitchIconButtonVariant.Secondary
        }
    )
}

// Helper data classes for multiple return types
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

private data class Triple<A, B, C>(
    val first: A,
    val second: B,
    val third: C
)
