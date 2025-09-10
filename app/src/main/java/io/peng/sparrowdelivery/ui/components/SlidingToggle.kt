package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.peng.sparrowdelivery.ui.theme.*
import kotlin.math.roundToInt

/**
 * A sliding toggle component inspired by iOS-style segmented control
 * Perfect for switching between "Now" and "Schedule" delivery options
 */
@Composable
fun <T> SlidingToggle(
    options: List<SlidingToggleOption<T>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    cornerRadius: Dp = 20.dp,
    animationDurationMs: Int = 300
) {
    val density = LocalDensity.current
    var containerWidth by remember { mutableIntStateOf(0) }
    val optionWidth = containerWidth / options.size
    
    val selectedIndex = options.indexOfFirst { it.value == selectedOption }
    
    val animatedOffset by animateIntAsState(
        targetValue = selectedIndex * optionWidth,
        animationSpec = tween(
            durationMillis = animationDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "sliding_toggle_offset"
    )

    SparrowTheme {
        Box(
            modifier = modifier
                .height(height)
                .onSizeChanged { size -> containerWidth = size.width }
                .clip(RoundedCornerShape(cornerRadius))
                .background(SparrowTheme.colors.muted)
                .padding(2.dp)
        ) {
            // Sliding background indicator
            if (containerWidth > 0) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(animatedOffset, 0) }
                        .size(
                            width = with(density) { (optionWidth - 4).toDp() },
                            height = height - 4.dp
                        )
                        .clip(RoundedCornerShape(cornerRadius - 2.dp))
                        .background(SparrowTheme.colors.background)
                        // Add subtle shadow/elevation effect
                        .background(
                            Color.Black.copy(alpha = 0.04f),
                            RoundedCornerShape(cornerRadius - 2.dp)
                        )
                )
            }
            
            // Option buttons
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                options.forEachIndexed { index, option ->
                    SlidingToggleOptionButton(
                        option = option,
                        isSelected = selectedOption == option.value,
                        onClick = { onOptionSelected(option.value) },
                        modifier = Modifier
                            .weight(1f)

                            .fillMaxHeight(),
                        cornerRadius = cornerRadius - 2.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> SlidingToggleOptionButton(
    option: SlidingToggleOption<T>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) SparrowTheme.colors.foreground else SparrowTheme.colors.mutedForeground,
        animationSpec = tween(200),
        label = "text_color"
    )
    
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) SparrowTheme.colors.primary else SparrowTheme.colors.mutedForeground,
        animationSpec = tween(200),
        label = "icon_tint"
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Remove ripple effect for cleaner look
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (option.icon != null) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = iconTint
                )
            }
            
            Text(
                text = option.label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = textColor,
                fontFamily = SparrowFontFamily
            )
        }
    }
}

/**
 * Data class for sliding toggle options
 */
data class SlidingToggleOption<T>(
    val value: T,
    val label: String,
    val icon: ImageVector? = null
)
