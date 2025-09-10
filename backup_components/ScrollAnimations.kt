package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.peng.sparrowdelivery.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*

// Enhanced pull-to-refresh component
@Composable
fun EnhancedPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshThreshold: Float = 120f,
    hapticFeedback: Boolean = true,
    content: @Composable (pullOffset: Float, isTriggered: Boolean) -> Unit
) {
    var pullOffset by remember { mutableFloatStateOf(0f) }
    var isTriggered by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    
    // Animation for pull offset
    val animatedPullOffset by animateFloatAsState(
        targetValue = if (isRefreshing) refreshThreshold else pullOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pull_offset"
    )
    
    // Refresh indicator rotation
    val refreshRotation by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "refresh_rotation"
    )
    
    Column(
        modifier = modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val down = awaitFirstDown()
                    var drag = 0f
                    
                    drag(down.id) { change ->
                        val dragAmount = change.positionChange().y
                        drag = (drag + dragAmount).coerceAtLeast(0f)
                        pullOffset = drag * 0.5f
                        
                        val newTriggered = pullOffset >= refreshThreshold
                        if (newTriggered != isTriggered) {
                            isTriggered = newTriggered
                            if (hapticFeedback && newTriggered) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                        
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                    
                    if (isTriggered && !isRefreshing) {
                        onRefresh()
                    }
                    
                    pullOffset = 0f
                    isTriggered = false
                }
            }
        }
    ) {
        // Pull-to-refresh indicator
        AnimatedVisibility(
            visible = animatedPullOffset > 0f || isRefreshing,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((animatedPullOffset / 2).dp.coerceAtMost(80.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SparrowTheme.colors.primary.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val scale = (animatedPullOffset / refreshThreshold).coerceIn(0f, 1f)
                val alpha = scale.coerceIn(0.3f, 1f)
                
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Pull to refresh",
                    tint = SparrowTheme.colors.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(scale)
                        .alpha(alpha)
                        .rotate(if (isRefreshing) refreshRotation else 0f)
                )
            }
        }
        
        content(animatedPullOffset, isTriggered)
    }
}

// Parallax scroll effect
@Composable
fun ParallaxScrollBox(
    backgroundContent: @Composable (scrollOffset: Float) -> Unit,
    foregroundContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    parallaxRatio: Float = 0.5f
) {
    val scrollState = rememberLazyListState()
    val scrollOffset = scrollState.firstVisibleItemScrollOffset.toFloat()
    
    Box(modifier = modifier) {
        // Background with parallax effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = scrollOffset * parallaxRatio
                }
        ) {
            backgroundContent(scrollOffset)
        }
        
        // Foreground content
        foregroundContent()
    }
}

// Animated scroll to top FAB
@Composable
fun ScrollToTopFab(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    visibilityThreshold: Int = 3,
    icon: ImageVector = Icons.Default.KeyboardArrowUp
) {
    val isVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex >= visibilityThreshold
        }
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scroll_fab_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "scroll_fab_alpha"
    )
    
    AnimatedFloatingActionButton(
        onClick = {
            // Scroll to top with animation
        },
        icon = icon,
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .zIndex(1f),
        containerColor = SparrowTheme.colors.primary,
        contentColor = SparrowTheme.colors.primaryForeground
    )
}

// Enhanced scroll indicator
@Composable
fun EnhancedScrollIndicator(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    trackColor: Color = SparrowTheme.colors.border,
    thumbColor: Color = SparrowTheme.colors.primary,
    thickness: Float = 4f,
    minThumbSize: Float = 20f
) {
    val visibleItemsCount = listState.layoutInfo.visibleItemsInfo.size
    val totalItemsCount = listState.layoutInfo.totalItemsCount
    val firstVisibleItemIndex = listState.firstVisibleItemIndex
    val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
    
    if (totalItemsCount <= visibleItemsCount) return
    
    val scrollProgress = remember(firstVisibleItemIndex, firstVisibleItemScrollOffset, totalItemsCount) {
        val estimatedItemSize = if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
            listState.layoutInfo.visibleItemsInfo.first().size
        } else 0
        
        if (estimatedItemSize == 0) return@remember 0f
        
        val totalScrollableDistance = (totalItemsCount * estimatedItemSize).toFloat()
        val currentScrollDistance = firstVisibleItemIndex * estimatedItemSize + firstVisibleItemScrollOffset
        
        (currentScrollDistance / totalScrollableDistance).coerceIn(0f, 1f)
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = scrollProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scroll_progress"
    )
    
    BoxWithConstraints(modifier = modifier) {
        val trackHeight = maxHeight
        val thumbHeight = maxOf(
            trackHeight * (visibleItemsCount.toFloat() / totalItemsCount),
            minThumbSize.dp
        )
        val thumbPosition = (trackHeight - thumbHeight) * animatedProgress
        
        // Track
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(thickness.dp)
                .background(
                    color = trackColor,
                    shape = RoundedCornerShape(thickness.dp / 2)
                )
        )
        
        // Thumb
        Box(
            modifier = Modifier
                .height(thumbHeight)
                .width(thickness.dp)
                .offset(y = thumbPosition)
                .background(
                    color = thumbColor,
                    shape = RoundedCornerShape(thickness.dp / 2)
                )
                .graphicsLayer {
                    scaleX = if (listState.isScrollInProgress) 1.5f else 1f
                }
        )
    }
}

// Bounce scroll effect
@Composable
fun BounceScrollEffect(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    bounceHeight: Float = 50f
) {
    var bounceOffset by remember { mutableFloatStateOf(0f) }
    
    val animatedBounceOffset by animateFloatAsState(
        targetValue = bounceOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_offset"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown()
                        var totalDrag = 0f
                        
                        drag(down.id) { change ->
                            val dragAmount = change.positionChange().y
                            totalDrag += dragAmount
                            
                            // Apply bounce effect at edges
                            bounceOffset = if (totalDrag > 0) {
                                // Bouncing at top
                                (totalDrag * 0.3f).coerceAtMost(bounceHeight)
                            } else {
                                // Bouncing at bottom
                                (totalDrag * 0.3f).coerceAtLeast(-bounceHeight)
                            }
                            
                            if (change.positionChange() != Offset.Zero) change.consume()
                        }
                        
                        // Reset bounce effect
                        bounceOffset = 0f
                    }
                }
            }
            .graphicsLayer {
                translationY = animatedBounceOffset
            }
    ) {
        content()
    }
}

// Sticky header with fade effect
@Composable
fun StickyHeaderWithFade(
    header: @Composable (alpha: Float) -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    fadeStartOffset: Float = 0f,
    fadeEndOffset: Float = 200f
) {
    val listState = rememberLazyListState()
    val scrollOffset = listState.firstVisibleItemScrollOffset.toFloat()
    
    val headerAlpha = remember(scrollOffset) {
        when {
            scrollOffset <= fadeStartOffset -> 1f
            scrollOffset >= fadeEndOffset -> 0f
            else -> 1f - (scrollOffset - fadeStartOffset) / (fadeEndOffset - fadeStartOffset)
        }
    }
    
    val animatedAlpha by animateFloatAsState(
        targetValue = headerAlpha,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "header_alpha"
    )
    
    Box(modifier = modifier) {
        content()
        
        // Sticky header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animatedAlpha)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SparrowTheme.colors.background,
                            SparrowTheme.colors.background.copy(alpha = 0f)
                        )
                    )
                )
        ) {
            header(animatedAlpha)
        }
    }
}

// Elastic scroll effect
@Composable
fun ElasticScrollContainer(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    elasticityFactor: Float = 0.3f
) {
    var elasticOffset by remember { mutableFloatStateOf(0f) }
    
    val animatedElasticOffset by animateFloatAsState(
        targetValue = elasticOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { elasticOffset = 0f },
        label = "elastic_offset"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = animatedElasticOffset
                val scale = 1f - abs(animatedElasticOffset) / 2000f
                scaleX = scale.coerceAtLeast(0.9f)
                scaleY = scale.coerceAtLeast(0.9f)
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown()
                        var totalDrag = 0f
                        
                        drag(down.id) { change ->
                            val dragAmount = change.positionChange().y
                            totalDrag += dragAmount
                            elasticOffset = totalDrag * elasticityFactor
                            
                            if (change.positionChange() != Offset.Zero) change.consume()
                        }
                        
                        // Trigger elastic animation
                        elasticOffset = totalDrag * elasticityFactor * 2f
                    }
                }
            }
    ) {
        content()
    }
}
