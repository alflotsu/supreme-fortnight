package io.peng.sparrowdelivery.ui.components

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.MapStyleOptions
import io.peng.sparrowdelivery.R

/**
 * Map styling utilities for dark mode support
 * Provides consistent dark theme styling for Google Maps across the app
 */

@Composable
fun getMapStyleOptions(forceDarkMode: Boolean = false): MapStyleOptions? {
    val context = LocalContext.current
    val systemDarkTheme = isSystemInDarkTheme()
    
    // Use dark theme by default for better performance and visual appeal
    // Only use light theme when explicitly requested and system is in light mode
    val shouldUseDarkTheme = forceDarkMode || systemDarkTheme
    
    return if (shouldUseDarkTheme) {
        loadDarkMapStyle(context)
    } else {
        loadLightMapStyle(context) // Use custom light theme style
    }
}

/**
 * Get map style options with explicit theme control
 * @param context Android context
 * @param isDarkTheme true for dark theme, false for light theme
 * @param defaultToDark if true, defaults to dark theme for better rendering performance
 */
fun getMapStyleOptions(context: Context, isDarkTheme: Boolean, defaultToDark: Boolean = true): MapStyleOptions? {
    // Use dark style only when explicitly requested or when defaulting to dark
    val useDarkStyle = if (defaultToDark) {
        true // Always use dark when defaulting to dark
    } else {
        isDarkTheme // Follow the actual theme preference
    }
    
    return if (useDarkStyle) {
        loadDarkMapStyle(context)
    } else {
        loadLightMapStyle(context) // Use custom light theme style
    }
}

private fun loadDarkMapStyle(context: Context): MapStyleOptions? {
    return try {
        val darkStyleJson = context.resources.openRawResource(R.raw.map_style_dark)
            .bufferedReader()
            .use { it.readText() }
        
        MapStyleOptions(darkStyleJson)
    } catch (e: Exception) {
        // Log error and return null to use default style
        android.util.Log.e("MapStyle", "Failed to load dark map style", e)
        null
    }
}

private fun loadLightMapStyle(context: Context): MapStyleOptions? {
    return try {
        val lightStyleJson = context.resources.openRawResource(R.raw.map_style_light)
            .bufferedReader()
            .use { it.readText() }
        
        MapStyleOptions(lightStyleJson)
    } catch (e: Exception) {
        // Log error and return null to use default style
        android.util.Log.e("MapStyle", "Failed to load light map style", e)
        null
    }
}

/**
 * Get map style options for Stitch auth screens (always dark for consistency)
 */
fun getStitchMapStyleOptions(context: Context): MapStyleOptions? {
    return loadDarkMapStyle(context)
}
