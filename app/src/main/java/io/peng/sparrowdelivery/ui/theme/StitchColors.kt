package io.peng.sparrowdelivery.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Stitch Design System Colors
 * Based on the beautiful Stitch UI designs in styles/reference/
 */
object StitchColors {
    // Primary Brand Colors
    val Red = Color(0xFFEA2A33)           // Primary action color
    val RedLight = Color(0xFFF3E7E8)      // Secondary/background tint
    val Cream = Color(0xFFFCF8F8)         // Light background
    val Green = Color(0xFF36e27b)         // Success/active states - UPDATED to match reference
    val GreenDark = Color(0xFF254632)     // Dark green variant - UPDATED to match reference
    
    // Text Colors - Light Theme
    val TextPrimary = Color(0xFF1B0E0E)   // Primary text
    val TextSecondary = Color(0xFF95c6a9) // Secondary text - UPDATED to match reference
    val TextMuted = Color(0xFF645959)     // Muted text
    
    // Dark Theme Colors - Based on reference designs
    val DarkBg = Color(0xFF122118)        // Dark background - UPDATED to match reference
    val DarkSurface = Color(0xFF254632)   // Dark surface - UPDATED to match reference
    val DarkCard = Color(0xFF1F1F1F)      // Dark card background
    val DarkTextPrimary = Color(0xFFFFFFFF)
    val DarkTextSecondary = Color(0xFF95c6a9) // UPDATED to match reference
    val DarkTextMuted = Color(0xFFA3A3A3)
    
    // Neutral Grays
    val Gray100 = Color(0xFFF5F5F5)
    val Gray200 = Color(0xFFE5E5E5)
    val Gray300 = Color(0xFFD4D4D4)
    val Gray400 = Color(0xFFA3A3A3)
    val Gray500 = Color(0xFF737373)
    val Gray600 = Color(0xFF525252)
    val Gray700 = Color(0xFF404040)
    val Gray800 = Color(0xFF262626)
    val Gray900 = Color(0xFF171717)
    
    // Semantic Colors
    val Success = Green
    val Warning = Color(0xFFF59E0B)
    val Error = Red
    val Info = Color(0xFF3B82F6)
    
    // Map Overlay Colors
    val OverlayLight = Color.White.copy(alpha = 0.85f)
    val OverlayDark = DarkCard.copy(alpha = 0.90f)
    val OverlayHandle = Gray400.copy(alpha = 0.6f)
    
    // Border Colors
    val BorderLight = Gray200
    val BorderDark = Gray700
    
    // Input Field Colors
    val InputBackground = Gray100
    val InputBackgroundDark = Color(0xFF254632) // UPDATED to match reference
    val InputBorder = Gray300
    val InputBorderDark = Color(0xFF95c6a9) // UPDATED to match reference
    val InputFocused = Red
    val InputFocusedDark = Green // UPDATED to match reference
}

/**
 * Extended color scheme for Stitch theme
 */
data class StitchColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val scrim: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val surfaceTint: Color,
    // Stitch-specific colors
    val accent: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val overlay: Color,
    val handle: Color,
    val inputBackground: Color,
    val inputBorder: Color,
    val inputFocused: Color,
    // Semantic colors for badges and components
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val info: Color,
    val onInfo: Color,
) {
    // Convenient aliases for common usage patterns
    val destructive: Color get() = error
    val onDestructive: Color get() = onError
}

val StitchLightColorScheme = StitchColorScheme(
    primary = StitchColors.Red,
    onPrimary = Color.White,
    primaryContainer = StitchColors.RedLight,
    onPrimaryContainer = StitchColors.TextPrimary,
    secondary = StitchColors.Green,
    onSecondary = Color.White,
    secondaryContainer = StitchColors.Green.copy(alpha = 0.1f),
    onSecondaryContainer = StitchColors.Green,
    tertiary = StitchColors.Gray500,
    onTertiary = Color.White,
    tertiaryContainer = StitchColors.Gray100,
    onTertiaryContainer = StitchColors.Gray800,
    error = StitchColors.Error,
    onError = Color.White,
    errorContainer = StitchColors.Error.copy(alpha = 0.1f),
    onErrorContainer = StitchColors.Error,
    background = StitchColors.Cream,
    onBackground = StitchColors.TextPrimary,
    surface = Color.White,
    onSurface = StitchColors.TextPrimary,
    surfaceVariant = StitchColors.Gray100,
    onSurfaceVariant = StitchColors.TextSecondary,
    outline = StitchColors.BorderLight,
    outlineVariant = StitchColors.Gray200,
    scrim = Color.Black.copy(alpha = 0.5f),
    inverseSurface = StitchColors.DarkSurface,
    inverseOnSurface = StitchColors.DarkTextPrimary,
    inversePrimary = StitchColors.Red,
    surfaceTint = StitchColors.Red,
    // Stitch-specific
    accent = StitchColors.Green,
    textSecondary = StitchColors.TextSecondary,
    textMuted = StitchColors.TextMuted,
    overlay = StitchColors.OverlayLight,
    handle = StitchColors.OverlayHandle,
    inputBackground = StitchColors.InputBackground,
    inputBorder = StitchColors.InputBorder,
    inputFocused = StitchColors.InputFocused,
    // Semantic colors
    success = StitchColors.Success,
    onSuccess = Color.White,
    warning = StitchColors.Warning,
    onWarning = Color.White,
    info = StitchColors.Info,
    onInfo = Color.White,
)

val StitchDarkColorScheme = StitchColorScheme(
    primary = StitchColors.Green, // Use green as primary accent in dark mode
    onPrimary = StitchColors.DarkBg, // Dark text on green background
    primaryContainer = StitchColors.Green.copy(alpha = 0.2f),
    onPrimaryContainer = StitchColors.Green,
    secondary = StitchColors.Green,
    onSecondary = Color.Black,
    secondaryContainer = StitchColors.Green.copy(alpha = 0.2f),
    onSecondaryContainer = StitchColors.Green,
    tertiary = StitchColors.Gray400,
    onTertiary = StitchColors.Gray900,
    tertiaryContainer = StitchColors.Gray800,
    onTertiaryContainer = StitchColors.Gray300,
    error = StitchColors.Error,
    onError = Color.White,
    errorContainer = StitchColors.Error.copy(alpha = 0.2f),
    onErrorContainer = StitchColors.Error,
    background = StitchColors.DarkBg,
    onBackground = StitchColors.DarkTextPrimary,
    surface = StitchColors.DarkSurface,
    onSurface = StitchColors.DarkTextPrimary,
    surfaceVariant = StitchColors.DarkCard,
    onSurfaceVariant = StitchColors.DarkTextSecondary,
    outline = StitchColors.BorderDark,
    outlineVariant = StitchColors.Gray800,
    scrim = Color.Black.copy(alpha = 0.7f),
    inverseSurface = Color.White,
    inverseOnSurface = StitchColors.TextPrimary,
    inversePrimary = StitchColors.Red,
    surfaceTint = StitchColors.Red,
    // Stitch-specific
    accent = StitchColors.Green,
    textSecondary = StitchColors.DarkTextSecondary,
    textMuted = StitchColors.DarkTextMuted,
    overlay = StitchColors.OverlayDark,
    handle = StitchColors.OverlayHandle,
    inputBackground = StitchColors.InputBackgroundDark,
    inputBorder = StitchColors.InputBorderDark,
    inputFocused = StitchColors.InputFocusedDark,
    // Semantic colors
    success = StitchColors.Success,
    onSuccess = Color.White,
    warning = StitchColors.Warning,
    onWarning = Color.White,
    info = StitchColors.Info,
    onInfo = Color.White,
)
