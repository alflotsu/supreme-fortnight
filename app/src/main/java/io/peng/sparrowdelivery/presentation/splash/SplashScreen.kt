package io.peng.sparrowdelivery.presentation.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.peng.sparrowdelivery.ui.components.EnhancedSplashScreen

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedSplashScreen(
        onSplashComplete = onSplashFinished,
        modifier = modifier,
        splashDuration = 3000L,
        particleCount = 60,
        showParticles = true,
        showGradientBackground = true
    )
}
