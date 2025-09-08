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
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideInFromLeft(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToRight(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )
}

// Scale transitions for modal-like screens
@OptIn(ExperimentalAnimationApi::class)
fun scaleInTransition(): EnterTransition {
    return scaleIn(
        initialScale = 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun scaleOutTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
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
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToBottom(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
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
