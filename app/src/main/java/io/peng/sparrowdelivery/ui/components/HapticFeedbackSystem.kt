package io.peng.sparrowdelivery.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay

// Enhanced haptic feedback patterns
enum class HapticFeedbackPattern {
    LIGHT_TAP,           // Quick, light tap
    MEDIUM_TAP,          // Standard button press
    HEAVY_TAP,           // Strong confirmation
    SUCCESS,             // Success feedback
    ERROR,               // Error/failure feedback
    WARNING,             // Warning/attention
    SELECTION,           // Item selection
    SCROLL_TICK,         // Scrolling through items
    PULL_TO_REFRESH,     // Pull to refresh trigger
    LONG_PRESS,          // Long press confirmation
    SWIPE,               // Swipe gesture
    NOTIFICATION,        // Notification received
    HEARTBEAT,           // Double tap pattern
    CUSTOM_WAVE          // Complex wave pattern
}

class EnhancedHapticFeedback(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    
    private val hasVibrator = vibrator?.hasVibrator() == true
    
    fun performHapticFeedback(pattern: HapticFeedbackPattern, intensity: Float = 1f) {
        if (!hasVibrator) return
        
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                performAdvancedHaptic(pattern, intensity)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                performBasicHaptic(pattern, intensity)
            }
            else -> {
                performLegacyHaptic(pattern)
            }
        }
    }
    
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    private fun performAdvancedHaptic(pattern: HapticFeedbackPattern, intensity: Float) {
        val effect = when (pattern) {
            HapticFeedbackPattern.LIGHT_TAP -> {
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
            }
            HapticFeedbackPattern.MEDIUM_TAP -> {
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            }
            HapticFeedbackPattern.HEAVY_TAP -> {
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            }
            HapticFeedbackPattern.SUCCESS -> {
                VibrationEffect.createWaveform(
                    longArrayOf(0, 50, 50, 100),
                    intArrayOf(0, (100 * intensity).toInt(), 0, (150 * intensity).toInt()),
                    -1
                )
            }
            HapticFeedbackPattern.ERROR -> {
                VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 100, 100, 100, 100),
                    intArrayOf(0, (200 * intensity).toInt(), 0, (200 * intensity).toInt(), 0, (200 * intensity).toInt()),
                    -1
                )
            }
            HapticFeedbackPattern.WARNING -> {
                VibrationEffect.createWaveform(
                    longArrayOf(0, 200, 100, 200),
                    intArrayOf(0, (150 * intensity).toInt(), 0, (150 * intensity).toInt()),
                    -1
                )
            }
            HapticFeedbackPattern.SELECTION -> {
                VibrationEffect.createOneShot(30, (50 * intensity).toInt())
            }
            HapticFeedbackPattern.SCROLL_TICK -> {
                VibrationEffect.createOneShot(10, (30 * intensity).toInt())
            }
            HapticFeedbackPattern.PULL_TO_REFRESH -> {
                VibrationEffect.createWaveform(
                    longArrayOf(0, 50, 20, 80),
                    intArrayOf(0, (80 * intensity).toInt(), 0, (120 * intensity).toInt()),
                    -1
                )
            }
            HapticFeedbackPattern.LONG_PRESS -> {
                VibrationEffect.createOneShot(150, (100 * intensity).toInt())
            }
            HapticFeedbackPattern.SWIPE -> {
                VibrationEffect.createOneShot(40, (60 * intensity).toInt())
            }
            HapticFeedbackPattern.NOTIFICATION -> {
                VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 100, 100),
                    intArrayOf(0, (120 * intensity).toInt(), 0, (80 * intensity).toInt()),
                    -1
                )
            }
            HapticFeedbackPattern.HEARTBEAT -> {
                VibrationEffect.createWaveform(
                    longArrayOf(0, 50, 50, 50, 200),
                    intArrayOf(0, (100 * intensity).toInt(), 0, (100 * intensity).toInt(), 0),
                    -1
                )
            }
            HapticFeedbackPattern.CUSTOM_WAVE -> {
                VibrationEffect.createWaveform(
                    longArrayOf(0, 20, 20, 40, 20, 60, 20, 80),
                    intArrayOf(0, 50, 0, 100, 0, 150, 0, 200).map { (it * intensity).toInt() }.toIntArray(),
                    -1
                )
            }
        }
        
        try {
            vibrator?.vibrate(effect)
        } catch (e: SecurityException) {
            // Vibration permission not granted or not available
            // Silently fail - this is acceptable for haptic feedback
        } catch (e: Exception) {
            // Handle any other potential vibration errors
            // Silently fail - haptic feedback is not critical
        }
    }
    
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun performBasicHaptic(pattern: HapticFeedbackPattern, intensity: Float) {
        val effect = when (pattern) {
            HapticFeedbackPattern.LIGHT_TAP -> {
                VibrationEffect.createOneShot(20, (50 * intensity).toInt())
            }
            HapticFeedbackPattern.MEDIUM_TAP -> {
                VibrationEffect.createOneShot(50, (100 * intensity).toInt())
            }
            HapticFeedbackPattern.HEAVY_TAP -> {
                VibrationEffect.createOneShot(100, (150 * intensity).toInt())
            }
            HapticFeedbackPattern.SUCCESS -> {
                VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 100), -1)
            }
            HapticFeedbackPattern.ERROR -> {
                VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 100, 100, 100), -1)
            }
            HapticFeedbackPattern.WARNING -> {
                VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1)
            }
            else -> {
                VibrationEffect.createOneShot(50, (100 * intensity).toInt())
            }
        }
        
        try {
            vibrator?.vibrate(effect)
        } catch (e: SecurityException) {
            // Vibration permission not granted or not available
            // Silently fail - this is acceptable for haptic feedback
        } catch (e: Exception) {
            // Handle any other potential vibration errors
            // Silently fail - haptic feedback is not critical
        }
    }
    
    @Suppress("DEPRECATION")
    private fun performLegacyHaptic(pattern: HapticFeedbackPattern) {
        val duration = when (pattern) {
            HapticFeedbackPattern.LIGHT_TAP -> 20
            HapticFeedbackPattern.MEDIUM_TAP -> 50
            HapticFeedbackPattern.HEAVY_TAP -> 100
            HapticFeedbackPattern.SUCCESS -> 150
            HapticFeedbackPattern.ERROR -> 300
            HapticFeedbackPattern.WARNING -> 200
            else -> 50
        }
        
        try {
            vibrator?.vibrate(duration.toLong())
        } catch (e: SecurityException) {
            // Vibration permission not granted or not available
            // Silently fail - this is acceptable for haptic feedback
        } catch (e: Exception) {
            // Handle any other potential vibration errors
            // Silently fail - haptic feedback is not critical
        }
    }
}

// Composable that provides enhanced haptic feedback
@Composable
fun rememberEnhancedHapticFeedback(): EnhancedHapticFeedback {
    val context = LocalContext.current
    return remember { EnhancedHapticFeedback(context) }
}

// Enhanced clickable modifier with haptic feedback
@Composable
fun Modifier.enhancedClickable(
    onClick: () -> Unit,
    hapticPattern: HapticFeedbackPattern = HapticFeedbackPattern.MEDIUM_TAP,
    hapticIntensity: Float = 1f,
    enabled: Boolean = true,
    indication: androidx.compose.foundation.Indication? = null
): Modifier {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    val interactionSource = remember { MutableInteractionSource() }
    
    return this.clickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled
    ) {
        if (enabled) {
            hapticFeedback.performHapticFeedback(hapticPattern, hapticIntensity)
            onClick()
        }
    }
}

// Enhanced button with contextual haptic feedback
@Composable
fun HapticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticPattern: HapticFeedbackPattern = HapticFeedbackPattern.MEDIUM_TAP,
    hapticIntensity: Float = 1f,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: androidx.compose.foundation.BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    
    Button(
        onClick = {
            if (enabled) {
                hapticFeedback.performHapticFeedback(hapticPattern, hapticIntensity)
                onClick()
            }
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

// Haptic scroll indicator
@Composable
fun HapticScrollIndicator(
    scrollProgress: Float,
    totalItems: Int,
    modifier: Modifier = Modifier,
    tickThreshold: Float = 0.1f
) {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    var lastTickIndex by remember { mutableIntStateOf(-1) }
    
    LaunchedEffect(scrollProgress) {
        val currentTickIndex = (scrollProgress / tickThreshold).toInt()
        if (currentTickIndex != lastTickIndex && currentTickIndex > 0) {
            hapticFeedback.performHapticFeedback(HapticFeedbackPattern.SCROLL_TICK, 0.3f)
            lastTickIndex = currentTickIndex
        }
    }
}

// Success/Error feedback helpers
@Composable
fun HapticFeedbackEffect(
    trigger: Boolean,
    pattern: HapticFeedbackPattern,
    intensity: Float = 1f
) {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    
    LaunchedEffect(trigger) {
        if (trigger) {
            hapticFeedback.performHapticFeedback(pattern, intensity)
        }
    }
}

// Contextual haptic feedback for different UI states
@Composable
fun ContextualHapticFeedback(
    isSuccess: Boolean = false,
    isError: Boolean = false,
    isWarning: Boolean = false,
    isLoading: Boolean = false,
    intensity: Float = 1f
) {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            hapticFeedback.performHapticFeedback(HapticFeedbackPattern.SUCCESS, intensity)
        }
    }
    
    LaunchedEffect(isError) {
        if (isError) {
            hapticFeedback.performHapticFeedback(HapticFeedbackPattern.ERROR, intensity)
        }
    }
    
    LaunchedEffect(isWarning) {
        if (isWarning) {
            hapticFeedback.performHapticFeedback(HapticFeedbackPattern.WARNING, intensity)
        }
    }
    
    LaunchedEffect(isLoading) {
        if (isLoading) {
            // Subtle heartbeat pattern while loading
            while (isLoading) {
                hapticFeedback.performHapticFeedback(HapticFeedbackPattern.HEARTBEAT, 0.2f)
                delay(2000) // Every 2 seconds
            }
        }
    }
}

// Pull-to-refresh haptic feedback
@Composable
fun PullToRefreshHaptic(
    pullOffset: Float,
    threshold: Float,
    isRefreshing: Boolean
) {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    var hasTriggered by remember { mutableStateOf(false) }
    
    LaunchedEffect(pullOffset, threshold) {
        if (pullOffset >= threshold && !hasTriggered && !isRefreshing) {
            hapticFeedback.performHapticFeedback(HapticFeedbackPattern.PULL_TO_REFRESH)
            hasTriggered = true
        } else if (pullOffset < threshold) {
            hasTriggered = false
        }
    }
}

// Swipe gesture haptic feedback
@Composable
fun SwipeHapticFeedback(
    swipeDirection: SwipeDirection?,
    intensity: Float = 0.8f
) {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    
    LaunchedEffect(swipeDirection) {
        swipeDirection?.let {
            hapticFeedback.performHapticFeedback(HapticFeedbackPattern.SWIPE, intensity)
        }
    }
}

enum class SwipeDirection {
    LEFT, RIGHT, UP, DOWN
}

// Long press haptic feedback
@Composable
fun Modifier.hapticLongPress(
    onLongPress: () -> Unit,
    hapticPattern: HapticFeedbackPattern = HapticFeedbackPattern.LONG_PRESS,
    intensity: Float = 1f
): Modifier {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(500) // Long press duration
            if (isPressed) { // Still pressed after delay
                hapticFeedback.performHapticFeedback(hapticPattern, intensity)
                onLongPress()
            }
        }
    }
    
    return this.clickable(
        interactionSource = interactionSource,
        indication = null
    ) { /* Handle normal click if needed */ }
}

// Selection haptic feedback for lists
@Composable
fun SelectionHapticFeedback(
    selectedItem: Any?,
    intensity: Float = 0.5f
) {
    val hapticFeedback = rememberEnhancedHapticFeedback()
    
    LaunchedEffect(selectedItem) {
        if (selectedItem != null) {
            hapticFeedback.performHapticFeedback(HapticFeedbackPattern.SELECTION, intensity)
        }
    }
}
