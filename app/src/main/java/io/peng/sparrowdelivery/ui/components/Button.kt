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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.components.stitch.*

enum class ButtonVariant {
    Default,
    Destructive,
    Outline,
    Secondary,
    Ghost,
    Link,
    Success
}

enum class ButtonSize {
    Small,
    Default,
    Large,
    Icon
}

@Composable
fun StitchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: io.peng.sparrowdelivery.ui.components.stitch.StitchButtonVariant = io.peng.sparrowdelivery.ui.components.stitch.StitchButtonVariant.Primary,
    size: io.peng.sparrowdelivery.ui.components.stitch.StitchButtonSize = io.peng.sparrowdelivery.ui.components.stitch.StitchButtonSize.Default,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    content: @Composable RowScope.() -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    val (backgroundColor, contentColor, borderStroke, elevation) = when (variant) {
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonVariant.Primary -> Quadruple(
            stitchColors.primary,
            stitchColors.onPrimary,
            null,
            2.dp
        )
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonVariant.Secondary -> Quadruple(
            stitchColors.secondaryContainer,
            stitchColors.onSecondaryContainer,
            null,
            1.dp
        )
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonVariant.Outline -> Quadruple(
            Color.Transparent,
            stitchColors.onSurface,
            BorderStroke(1.dp, stitchColors.outline),
            0.dp
        )
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonVariant.Destructive -> Quadruple(
            stitchColors.error,
            stitchColors.onError,
            null,
            2.dp
        )
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonVariant.Ghost -> Quadruple(
            Color.Transparent,
            stitchColors.onSurface,
            null,
            0.dp
        )
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonVariant.Success -> Quadruple(
            stitchColors.success,
            stitchColors.onSuccess,
            null,
            2.dp
        )
    }
    
    val (horizontalPadding, verticalPadding) = when (size) {
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonSize.Small -> Pair(8.dp, 4.dp)
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonSize.Default -> Pair(16.dp, 8.dp)
        io.peng.sparrowdelivery.ui.components.stitch.StitchButtonSize.Large -> Pair(24.dp, 16.dp)
    }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = if (enabled) elevation else 0.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = stitchColors.onSurface.copy(alpha = 0.1f),
                spotColor = stitchColors.onSurface.copy(alpha = 0.1f)
            ),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
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
            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
fun SparrowTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    // Map Shadcn variants to Stitch button components
    when (variant) {
        ButtonVariant.Default -> {
            StitchPrimaryButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled,
                icon = leadingIcon ?: trailingIcon
            )
        }
        ButtonVariant.Secondary -> {
            StitchSecondaryButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled
            )
        }
        ButtonVariant.Outline -> {
            StitchOutlineButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled
            )
        }
        ButtonVariant.Ghost -> {
            StitchIconButton(
                onClick = onClick,
                icon = leadingIcon ?: trailingIcon ?: Icons.Default.Add,
                variant = StitchIconButtonVariant.Secondary
            )
        }
        ButtonVariant.Link -> {
            StitchText(
                text = text,
                modifier = modifier.clickable(
                    enabled = enabled,
                    onClick = onClick
                ),
                color = androidx.compose.ui.graphics.Color.Blue // Use appropriate color from Stitch theme
            )
        }
        ButtonVariant.Destructive -> {
            StitchPrimaryButton(
                onClick = onClick,
                text = text,
                modifier = modifier,
                enabled = enabled
            )
            // Note: Would need to customize styling for destructive variant
        }
        ButtonVariant.Success -> {
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
    variant: ButtonVariant = ButtonVariant.Ghost,
    size: ButtonSize = ButtonSize.Icon,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    // Map to Stitch IconButton
    StitchIconButton(
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        variant = when (variant) {
            ButtonVariant.Default -> StitchIconButtonVariant.Primary
            ButtonVariant.Secondary -> StitchIconButtonVariant.Secondary
            ButtonVariant.Success -> StitchIconButtonVariant.Success
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
