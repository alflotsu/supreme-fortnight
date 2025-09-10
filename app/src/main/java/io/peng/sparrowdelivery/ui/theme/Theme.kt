package io.peng.sparrowdelivery.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Local provider for Stitch color scheme
 */
val LocalStitchColorScheme = staticCompositionLocalOf { StitchLightColorScheme }

/**
 * Convert Stitch color scheme to Material3 ColorScheme
 */
fun StitchColorScheme.toMaterial3ColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = this.primary,
        onPrimary = this.onPrimary,
        primaryContainer = this.primaryContainer,
        onPrimaryContainer = this.onPrimaryContainer,
        secondary = this.secondary,
        onSecondary = this.onSecondary,
        secondaryContainer = this.secondaryContainer,
        onSecondaryContainer = this.onSecondaryContainer,
        tertiary = this.tertiary,
        onTertiary = this.onTertiary,
        tertiaryContainer = this.tertiaryContainer,
        onTertiaryContainer = this.onTertiaryContainer,
        error = this.error,
        onError = this.onError,
        errorContainer = this.errorContainer,
        onErrorContainer = this.onErrorContainer,
        background = this.background,
        onBackground = this.onBackground,
        surface = this.surface,
        onSurface = this.onSurface,
        surfaceVariant = this.surfaceVariant,
        onSurfaceVariant = this.onSurfaceVariant,
        outline = this.outline,
        outlineVariant = this.outlineVariant,
        scrim = this.scrim,
        inverseSurface = this.inverseSurface,
        inverseOnSurface = this.inverseOnSurface,
        inversePrimary = this.inversePrimary,
        surfaceTint = this.surfaceTint
    )
}

@Composable
fun SparrowDeliveryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ but disabled by default to use Stitch theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val stitchColorScheme = if (darkTheme) StitchDarkColorScheme else StitchLightColorScheme
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> stitchColorScheme.toMaterial3ColorScheme()
    }

    CompositionLocalProvider(
        LocalStitchColorScheme provides stitchColorScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = StitchTypography,
            content = content
        )
    }
}

/**
 * Alias for backward compatibility
 * Use this in your components to access Stitch-specific colors
 */
@Composable
fun StitchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    SparrowDeliveryTheme(
        darkTheme = darkTheme,
        content = content
    )
}
