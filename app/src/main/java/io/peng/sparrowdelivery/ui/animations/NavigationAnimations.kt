package io.peng.sparrowdelivery.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.launch

// Enhanced slide animations for navigation
@OptIn(ExperimentalAnimationApi::class)
fun slideInFromRight(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 350,
            delayMillis = 50,
            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 250,
            easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideInFromLeft(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 350,
            delayMillis = 50,
            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToRight(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 250,
            easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        )
    )
}

// Scale transitions for modal-like screens
@OptIn(ExperimentalAnimationApi::class)
fun scaleInTransition(): EnterTransition {
    return scaleIn(
        initialScale = 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 400,
            easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun scaleOutTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.75f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 250,
            easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        )
    )
}

// Fade transitions for overlay screens
@OptIn(ExperimentalAnimationApi::class)
fun fadeInTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun fadeOutTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
}

// Slide up from bottom (for bottom sheets and modals)
@OptIn(ExperimentalAnimationApi::class)
fun slideInFromBottom(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 350,
            delayMillis = 100,
            easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToBottom(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 250,
            easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        )
    )
}

// Enhanced transitions for specific screen types
object NavigationTransitions {
    
    // For main app flows (forward navigation)
    val forwardTransition = Pair(
        slideInFromRight(),
        slideOutToLeft()
    )
    
    // Enhanced forward transition with scale
    val enhancedForwardTransition = Pair(
        slideInFromRightWithScale(),
        slideOutToLeftWithScale()
    )
    
    // For back navigation
    val backwardTransition = Pair(
        slideInFromLeft(),
        slideOutToRight()
    )
    
    // For modal/dialog-like screens
    val modalTransition = Pair(
        scaleInTransition(),
        scaleOutTransition()
    )
    
    // For detailed views with shared element style
    val sharedElementTransition = Pair(
        sharedElementEnterTransition(),
        sharedElementExitTransition()
    )
    
    // For overlay screens
    val fadeTransition = Pair(
        fadeInTransition(),
        fadeOutTransition()
    )
    
    // For bottom sheet style screens
    val bottomSheetTransition = Pair(
        slideInFromBottom(),
        slideOutToBottom()
    )
}

// Helper composable for animated content changes
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedContentTransition(
    targetState: Boolean,
    transitionSpec: @Composable AnimatedContentTransitionScope<Boolean>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
    },
    content: @Composable (Boolean) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = transitionSpec as AnimatedContentTransitionScope<Boolean>.() -> ContentTransform,
        label = "content_transition"
    ) { state ->
        content(state)
    }
}

// Advanced slide animations with shared element style transitions
@OptIn(ExperimentalAnimationApi::class)
fun slideInFromRightWithScale(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + scaleIn(
        initialScale = 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToLeftWithScale(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + scaleOut(
        targetScale = 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 200,
            easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        )
    )
}

// Shared element style transition for detailed views
@OptIn(ExperimentalAnimationApi::class)
fun sharedElementEnterTransition(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight / 3 },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + scaleIn(
        initialScale = 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 400,
            easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun sharedElementExitTransition(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight / 4 },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + scaleOut(
        targetScale = 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 250,
            easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        )
    )
}

// Staggered animation for list items
@Composable
fun rememberStaggeredAnimation(
    itemCount: Int,
    staggerDelayMs: Int = 50
): List<Animatable<Float, AnimationVector1D>> {
    return remember(itemCount) {
        List(itemCount) { index ->
            Animatable(0f).apply {
                // Launch animation with staggered delay
                kotlinx.coroutines.GlobalScope.launch {
                    kotlinx.coroutines.delay(index * staggerDelayMs.toLong())
                    animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            }
        }
    }
}
