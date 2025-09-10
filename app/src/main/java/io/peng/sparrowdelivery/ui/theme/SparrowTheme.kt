package io.peng.sparrowdelivery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.peng.sparrowdelivery.R

// Sparrow delivery app color palette using Stitch design system
object SparrowColors {
    // Light theme colors - Stitch design system
    val Background = Color(0xFFFCF8F8) // Stitch cream background
    val Foreground = Color(0xFF1B0E0E) // Stitch dark text
    val Card = Color(0xFFFFFFFF)
    val CardForeground = Color(0xFF1B0E0E)
    val Popover = Color(0xFFFFFFFF)
    val PopoverForeground = Color(0xFF1B0E0E)
    val Primary = Color(0xFFEA2A33) // Stitch red
    val PrimaryForeground = Color(0xFFFFFFFF)
    val Secondary = Color(0xFFF3E7E8) // Stitch light cream
    val SecondaryForeground = Color(0xFF1B0E0E)
    val Muted = Color(0xFFF3E7E8)
    val MutedForeground = Color(0xFF994D51) // Stitch muted red
    val Accent = Color(0xFF22C55E) // Stitch green
    val AccentForeground = Color(0xFFFFFFFF)
    val Destructive = Color(0xFFEA2A33) // Use Stitch red for destructive
    val DestructiveForeground = Color(0xFFFFFFFF)
    val Border = Color(0xFFE5E5E5)
    val Input = Color(0xFFF5F5F5)
    val Ring = Color(0xFFEA2A33) // Stitch red ring
    val Success = Color(0xFF22C55E) // Stitch green
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)
    
    // Dark theme colors - Stitch design system
    object Dark {
        val Background = Color(0xFF121212) // Stitch dark background
        val Foreground = Color(0xFFFFFFFF)
        val Card = Color(0xFF1F1F1F) // Stitch dark card
        val CardForeground = Color(0xFFFFFFFF)
        val Popover = Color(0xFF181818) // Stitch dark surface
        val PopoverForeground = Color(0xFFFFFFFF)
        val Primary = Color(0xFFEA2A33) // Stitch red (unchanged in dark)
        val PrimaryForeground = Color(0xFFFFFFFF)
        val Secondary = Color(0xFF262626) // Dark neutral
        val SecondaryForeground = Color(0xFFE5E5E5)
        val Muted = Color(0xFF262626)
        val MutedForeground = Color(0xFFA3A3A3) // Stitch muted text dark
        val Accent = Color(0xFF22C55E) // Stitch green (unchanged)
        val AccentForeground = Color(0xFF000000)
        val Destructive = Color(0xFFEA2A33) // Stitch red
        val DestructiveForeground = Color(0xFFFFFFFF)
        val Border = Color(0xFF404040)
        val Input = Color(0xFF262626)
        val Ring = Color(0xFFEA2A33) // Stitch red ring
        val Success = Color(0xFF22C55E) // Stitch green
        val Warning = Color(0xFFF59E0B)
        val Info = Color(0xFF3B82F6)
    }
}

// Spline Sans font family - Sparrow delivery app typography
val SparrowFontFamily = FontFamily(
    Font(R.font.spline_sans_regular, FontWeight.Normal),   // 400
    Font(R.font.spline_sans_medium, FontWeight.Medium),    // 500
    Font(R.font.spline_sans_semibold, FontWeight.SemiBold), // 600
    Font(R.font.spline_sans_bold, FontWeight.Bold),        // 700
)

// Sparrow delivery app spacing system
object SparrowSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val xxxl = 64.dp
}

// Sparrow delivery app typography with Spline Sans
object SparrowTypography {
    val h1 = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 48.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.Bold, // Spline Sans only goes to 700
        letterSpacing = (-0.02).sp
    )
    
    val h2 = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.02).sp
    )
    
    val h3 = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.02).sp
    )
    
    val h4 = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.02).sp
    )
    
    val p = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )
    
    val large = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.01).sp
    )
    
    val small = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    )
    
    val muted = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )
    
    val lead = TextStyle(
        fontFamily = SparrowFontFamily,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.01).sp
    )
}

// Sparrow delivery app border radius values
object SparrowBorderRadius {
    val sm = 6.dp
    val md = 12.dp  // Stitch uses more rounded corners
    val lg = 16.dp
    val xl = 20.dp  // For bottom sheets
}

// Sparrow delivery app shadow elevations
object SparrowElevation {
    val sm = 2.dp
    val md = 4.dp
    val lg = 8.dp
    val xl = 12.dp
}

// Sparrow delivery app theme data class
data class SparrowThemeColors(
    val background: Color,
    val foreground: Color,
    val card: Color,
    val cardForeground: Color,
    val popover: Color,
    val popoverForeground: Color,
    val primary: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val secondaryForeground: Color,
    val muted: Color,
    val mutedForeground: Color,
    val accent: Color,
    val accentForeground: Color,
    val destructive: Color,
    val destructiveForeground: Color,
    val border: Color,
    val input: Color,
    val ring: Color,
    val success: Color,
    val warning: Color,
    val info: Color
)

// CompositionLocal for Sparrow theme
val LocalSparrowColors = staticCompositionLocalOf {
    SparrowThemeColors(
        background = SparrowColors.Background,
        foreground = SparrowColors.Foreground,
        card = SparrowColors.Card,
        cardForeground = SparrowColors.CardForeground,
        popover = SparrowColors.Popover,
        popoverForeground = SparrowColors.PopoverForeground,
        primary = SparrowColors.Primary,
        primaryForeground = SparrowColors.PrimaryForeground,
        secondary = SparrowColors.Secondary,
        secondaryForeground = SparrowColors.SecondaryForeground,
        muted = SparrowColors.Muted,
        mutedForeground = SparrowColors.MutedForeground,
        accent = SparrowColors.Accent,
        accentForeground = SparrowColors.AccentForeground,
        destructive = SparrowColors.Destructive,
        destructiveForeground = SparrowColors.DestructiveForeground,
        border = SparrowColors.Border,
        input = SparrowColors.Input,
        ring = SparrowColors.Ring,
        success = SparrowColors.Success,
        warning = SparrowColors.Warning,
        info = SparrowColors.Info
    )
}

@Composable
fun SparrowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        SparrowThemeColors(
            background = SparrowColors.Dark.Background,
            foreground = SparrowColors.Dark.Foreground,
            card = SparrowColors.Dark.Card,
            cardForeground = SparrowColors.Dark.CardForeground,
            popover = SparrowColors.Dark.Popover,
            popoverForeground = SparrowColors.Dark.PopoverForeground,
            primary = SparrowColors.Dark.Primary,
            primaryForeground = SparrowColors.Dark.PrimaryForeground,
            secondary = SparrowColors.Dark.Secondary,
            secondaryForeground = SparrowColors.Dark.SecondaryForeground,
            muted = SparrowColors.Dark.Muted,
            mutedForeground = SparrowColors.Dark.MutedForeground,
            accent = SparrowColors.Dark.Accent,
            accentForeground = SparrowColors.Dark.AccentForeground,
            destructive = SparrowColors.Dark.Destructive,
            destructiveForeground = SparrowColors.Dark.DestructiveForeground,
            border = SparrowColors.Dark.Border,
            input = SparrowColors.Dark.Input,
            ring = SparrowColors.Dark.Ring,
            success = SparrowColors.Dark.Success,
            warning = SparrowColors.Dark.Warning,
            info = SparrowColors.Dark.Info
        )
    } else {
        SparrowThemeColors(
            background = SparrowColors.Background,
            foreground = SparrowColors.Foreground,
            card = SparrowColors.Card,
            cardForeground = SparrowColors.CardForeground,
            popover = SparrowColors.Popover,
            popoverForeground = SparrowColors.PopoverForeground,
            primary = SparrowColors.Primary,
            primaryForeground = SparrowColors.PrimaryForeground,
            secondary = SparrowColors.Secondary,
            secondaryForeground = SparrowColors.SecondaryForeground,
            muted = SparrowColors.Muted,
            mutedForeground = SparrowColors.MutedForeground,
            accent = SparrowColors.Accent,
            accentForeground = SparrowColors.AccentForeground,
            destructive = SparrowColors.Destructive,
            destructiveForeground = SparrowColors.DestructiveForeground,
            border = SparrowColors.Border,
            input = SparrowColors.Input,
            ring = SparrowColors.Ring,
            success = SparrowColors.Success,
            warning = SparrowColors.Warning,
            info = SparrowColors.Info
        )
    }

    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            background = colors.background,
            surface = colors.card,
            primary = colors.primary,
            secondary = colors.secondary,
            onBackground = colors.foreground,
            onSurface = colors.cardForeground,
            onPrimary = colors.primaryForeground,
            onSecondary = colors.secondaryForeground
        )
    } else {
        lightColorScheme(
            background = colors.background,
            surface = colors.card,
            primary = colors.primary,
            secondary = colors.secondary,
            onBackground = colors.foreground,
            onSurface = colors.cardForeground,
            onPrimary = colors.primaryForeground,
            onSecondary = colors.secondaryForeground
        )
    }

    CompositionLocalProvider(LocalSparrowColors provides colors) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}

// Extension to access Sparrow colors easily
object SparrowTheme {
    val colors: SparrowThemeColors
        @Composable
        get() = LocalSparrowColors.current
}

