package io.peng.sparrowdelivery.ui.components.stitch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme
import io.peng.sparrowdelivery.ui.theme.StitchTheme

/**
 * Primary Stitch button - Red background, white text, rounded-full
 * Used for main actions like "Request Delivery", "Sign up for free"
 */
@Composable
fun StitchPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    val stitchColors = LocalStitchColorScheme.current
    val haptic = LocalHapticFeedback.current
    
    Button(
        onClick = {
            if (!loading) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        enabled = enabled && !loading,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = stitchColors.primary,
            contentColor = stitchColors.onPrimary,
            disabledContainerColor = stitchColors.primary.copy(alpha = 0.5f),
            disabledContentColor = stitchColors.onPrimary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(28.dp), // Fully rounded
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = stitchColors.onPrimary
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Secondary Stitch button - Light pink/cream background, dark text
 * Used for secondary actions like "Log in"
 */
@Composable
fun StitchSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    val stitchColors = LocalStitchColorScheme.current
    val haptic = LocalHapticFeedback.current
    
    Button(
        onClick = {
            if (!loading) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        enabled = enabled && !loading,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = stitchColors.primaryContainer,
            contentColor = stitchColors.onPrimaryContainer,
            disabledContainerColor = stitchColors.primaryContainer.copy(alpha = 0.5f),
            disabledContentColor = stitchColors.onPrimaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(28.dp), // Fully rounded
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 1.dp
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = stitchColors.onPrimaryContainer
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Green success button - Used for active states and confirmations
 */
@Composable
fun StitchSuccessButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    val stitchColors = LocalStitchColorScheme.current
    val haptic = LocalHapticFeedback.current
    
    Button(
        onClick = {
            if (!loading) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        enabled = enabled && !loading,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = stitchColors.accent, // Green
            contentColor = Color.White,
            disabledContainerColor = stitchColors.accent.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(28.dp), // Fully rounded
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Icon button for circular actions (phone, chat, etc.)
 */
@Composable
fun StitchIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    variant: StitchIconButtonVariant = StitchIconButtonVariant.Primary
) {
    val stitchColors = LocalStitchColorScheme.current
    val haptic = LocalHapticFeedback.current
    
    val containerColor = when (variant) {
        StitchIconButtonVariant.Primary -> stitchColors.primary
        StitchIconButtonVariant.Secondary -> stitchColors.primaryContainer
        StitchIconButtonVariant.Success -> stitchColors.accent
    }
    
    val contentColor = when (variant) {
        StitchIconButtonVariant.Primary -> stitchColors.onPrimary
        StitchIconButtonVariant.Secondary -> stitchColors.onPrimaryContainer
        StitchIconButtonVariant.Success -> Color.White
    }
    
    FilledIconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        enabled = enabled,
        modifier = modifier.size(48.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp) // Circular
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

enum class StitchIconButtonVariant {
    Primary, Secondary, Success
}

enum class StitchButtonVariant {
    Primary, Secondary, Outline, Destructive, Ghost, Success
}

enum class StitchButtonSize {
    Small, Default, Large
}

/**
 * Text button for less prominent actions like "Skip", "Cancel", etc.
 */
@Composable
fun StitchTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    variant: StitchButtonVariant = StitchButtonVariant.Primary,
    size: StitchButtonSize = StitchButtonSize.Default,
    icon: ImageVector? = null
) {
    val stitchColors = LocalStitchColorScheme.current
    val haptic = LocalHapticFeedback.current
    
    val contentColor = when (variant) {
        StitchButtonVariant.Primary -> stitchColors.primary
        StitchButtonVariant.Secondary -> stitchColors.textSecondary
        StitchButtonVariant.Success -> stitchColors.accent
        StitchButtonVariant.Destructive -> stitchColors.error
        else -> stitchColors.onSurface
    }
    
    val textStyle = when (size) {
        StitchButtonSize.Small -> MaterialTheme.typography.labelSmall
        StitchButtonSize.Default -> MaterialTheme.typography.labelMedium
        StitchButtonSize.Large -> MaterialTheme.typography.labelLarge
    }
    
    val iconSize = when (size) {
        StitchButtonSize.Small -> 16.dp
        StitchButtonSize.Default -> 20.dp
        StitchButtonSize.Large -> 24.dp
    }
    
    TextButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = contentColor,
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = contentColor
                )
            }
            
            Text(
                text = text,
                style = textStyle,
                color = contentColor
            )
        }
    }
}

/**
 * Outline button for less prominent actions
 */
@Composable
fun StitchOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val stitchColors = LocalStitchColorScheme.current
    val haptic = LocalHapticFeedback.current
    
    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        enabled = enabled,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = stitchColors.primary,
            disabledContentColor = stitchColors.primary.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) stitchColors.outline else stitchColors.outline.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StitchButtonsPreview() {
    StitchTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Stitch Button Components",
                style = MaterialTheme.typography.headlineMedium
            )
            
            StitchPrimaryButton(
                onClick = { },
                text = "Request Delivery",
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.LocalShipping
            )
            
            StitchSecondaryButton(
                onClick = { },
                text = "Log in",
                modifier = Modifier.fillMaxWidth()
            )
            
            StitchSuccessButton(
                onClick = { },
                text = "Confirm Booking",
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Check
            )
            
            StitchOutlineButton(
                onClick = { },
                text = "Cancel",
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StitchIconButton(
                    onClick = { },
                    icon = Icons.Outlined.Call,
                    variant = StitchIconButtonVariant.Primary
                )
                StitchIconButton(
                    onClick = { },
                    icon = Icons.Outlined.Message,
                    variant = StitchIconButtonVariant.Secondary
                )
                StitchIconButton(
                    onClick = { },
                    icon = Icons.Outlined.Navigation,
                    variant = StitchIconButtonVariant.Success
                )
            }
        }
    }
}
