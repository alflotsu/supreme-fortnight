package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.*

// Shimmer effect for skeleton loading
@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Restart
            ), label = "shimmer_translate"
        )

        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

// Loading spinner with custom animations
@Composable
fun EnhancedLoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color = ShadcnTheme.colors.primary,
    strokeWidth: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_spinner")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinner_rotation"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spinner_scale"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size * scale)
                .graphicsLayer {
                    rotationZ = rotation
                },
            color = color,
            strokeWidth = strokeWidth,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

// Skeleton components
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 20.dp,
    shape: RoundedCornerShape = RoundedCornerShape(ShadcnBorderRadius.sm)
) {
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .background(shimmerBrush())
    )
}

@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    lines: Int = 1,
    lineHeight: Dp = 20.dp,
    lineSpacing: Dp = 8.dp
) {
    Column(modifier = modifier) {
        repeat(lines) { index ->
            SkeletonBox(
                width = when (index) {
                    lines - 1 -> 180.dp // Last line is shorter
                    else -> 250.dp
                },
                height = lineHeight
            )
            
            if (index < lines - 1) {
                Spacer(modifier = Modifier.height(lineSpacing))
            }
        }
    }
}

@Composable
fun SkeletonCircle(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush())
    )
}

// Loading states for different components
@Composable
fun LoadingCard(
    modifier: Modifier = Modifier,
    showAvatar: Boolean = true
) {
    ShadcnCard(
        modifier = modifier.fillMaxWidth(),
        variant = ShadcnCardVariant.Default
    ) {
        Column(
            modifier = Modifier.padding(ShadcnSpacing.lg)
        ) {
            if (showAvatar) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkeletonCircle(size = 40.dp)
                    Spacer(modifier = Modifier.width(ShadcnSpacing.md))
                    Column {
                        SkeletonBox(width = 120.dp, height = 16.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        SkeletonBox(width = 80.dp, height = 12.dp)
                    }
                }
                Spacer(modifier = Modifier.height(ShadcnSpacing.md))
            }
            
            SkeletonText(lines = 3, lineHeight = 16.dp)
            
            Spacer(modifier = Modifier.height(ShadcnSpacing.md))
            
            Row {
                SkeletonBox(width = 100.dp, height = 32.dp)
                Spacer(modifier = Modifier.width(ShadcnSpacing.sm))
                SkeletonBox(width = 80.dp, height = 32.dp)
            }
        }
    }
}

@Composable
fun LoadingListItem(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(ShadcnSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonCircle(size = 48.dp)
        
        Spacer(modifier = Modifier.width(ShadcnSpacing.md))
        
        Column(modifier = Modifier.weight(1f)) {
            SkeletonBox(width = 150.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonBox(width = 100.dp, height = 12.dp)
        }
        
        SkeletonBox(width = 60.dp, height = 24.dp)
    }
}

// Pulsing loading indicator
@Composable
fun PulsingLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = ShadcnTheme.colors.primary,
    size: Dp = 12.dp,
    count: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_loading")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(size * scale)
                    .background(color, CircleShape)
            )
        }
    }
}

// Wave loading animation
@Composable
fun WaveLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = ShadcnTheme.colors.primary,
    waveHeight: Dp = 4.dp,
    waveCount: Int = 5
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_loading")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(waveCount) { index ->
            val height by infiniteTransition.animateFloat(
                initialValue = waveHeight.value * 0.3f,
                targetValue = waveHeight.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, delayMillis = index * 100),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_$index"
            )
            
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(height.dp)
                    .background(color, RoundedCornerShape(1.5.dp))
            )
        }
    }
}

// Full screen loading overlay
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    message: String = "Loading...",
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f)
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = isLoading,
        enter = androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            ShadcnCard(
                modifier = Modifier.wrapContentSize(),
                variant = ShadcnCardVariant.Default
            ) {
                Column(
                    modifier = Modifier.padding(ShadcnSpacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EnhancedLoadingSpinner()
                    
                    Spacer(modifier = Modifier.height(ShadcnSpacing.lg))
                    
                    ShadcnText(
                        text = message,
                        style = ShadcnTextStyle.Large,
                        color = ShadcnTheme.colors.foreground
                    )
                }
            }
        }
    }
}

// Skeleton for specific screens
@Composable
fun DeliveryItemSkeleton() {
    LoadingCard(showAvatar = false)
}

@Composable
fun ChatMessageSkeleton(
    isOwnMessage: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ShadcnSpacing.md, vertical = ShadcnSpacing.xs),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        if (!isOwnMessage) {
            SkeletonCircle(size = 32.dp)
            Spacer(modifier = Modifier.width(ShadcnSpacing.sm))
        }
        
        SkeletonBox(
            width = if (isOwnMessage) 200.dp else 150.dp,
            height = 40.dp,
            shape = RoundedCornerShape(
                topStart = if (isOwnMessage) 16.dp else 4.dp,
                topEnd = if (isOwnMessage) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            )
        )
        
        if (isOwnMessage) {
            Spacer(modifier = Modifier.width(ShadcnSpacing.sm))
            SkeletonCircle(size = 32.dp)
        }
    }
}
