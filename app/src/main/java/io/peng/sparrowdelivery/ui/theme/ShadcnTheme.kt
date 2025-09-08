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

// shadcn/ui inspired color palette
object ShadcnColors {
    // Light theme colors
    val Background = Color(0xFFFFFFFF)
    val Foreground = Color(0xFF0F0F0F)
    val Card = Color(0xFFFFFFFF)
    val CardForeground = Color(0xFF0F0F0F)
    val Popover = Color(0xFFFFFFFF)
    val PopoverForeground = Color(0xFF0F0F0F)
    val Primary = Color(0xFF2563EB) // Beautiful blue
    val PrimaryForeground = Color(0xFFFFFFFF)
    val Secondary = Color(0xFFF1F5F9) // Light blue-gray
    val SecondaryForeground = Color(0xFF0F172A)
    val Muted = Color(0xFFF1F5F9)
    val MutedForeground = Color(0xFF64748B)
    val Accent = Color(0xFF3B82F6) // Slightly lighter blue
    val AccentForeground = Color(0xFFFFFFFF)
    val Destructive = Color(0xFFEF4444)
    val DestructiveForeground = Color(0xFFFAFAFA)
    val Border = Color(0xFFE4E4E7)
    val Input = Color(0xFFE4E4E7)
    val Ring = Color(0xFF2563EB) // Blue ring color
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)
    
    // Dark theme colors
    object Dark {
        val Background = Color(0xFF0F0F0F)
        val Foreground = Color(0xFFFAFAFA)
        val Card = Color(0xFF0F0F0F)
        val CardForeground = Color(0xFFFAFAFA)
        val Popover = Color(0xFF0F0F0F)
        val PopoverForeground = Color(0xFFFAFAFA)
        val Primary = Color(0xFF3B82F6) // Bright blue for dark mode
        val PrimaryForeground = Color(0xFFFFFFFF)
        val Secondary = Color(0xFF1E293B) // Dark blue-gray
        val SecondaryForeground = Color(0xFFF1F5F9)
        val Muted = Color(0xFF1E293B)
        val MutedForeground = Color(0xFF94A3B8)
        val Accent = Color(0xFF60A5FA) // Lighter blue accent
        val AccentForeground = Color(0xFF0F172A)
        val Destructive = Color(0xFF7F1D1D)
        val DestructiveForeground = Color(0xFFFAFAFA)
        val Border = Color(0xFF27272A)
        val Input = Color(0xFF27272A)
        val Ring = Color(0xFF3B82F6) // Blue ring for dark mode
        val Success = Color(0xFF059669)
        val Warning = Color(0xFFD97706)
        val Info = Color(0xFF2563EB)
    }
}

// Inter font family with explicit weights
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),     // 400
    Font(R.font.inter_medium, FontWeight.Medium),      // 500
    Font(R.font.inter_semibold, FontWeight.SemiBold),  // 600
    Font(R.font.inter_bold, FontWeight.Bold),          // 700
    Font(R.font.inter_extrabold, FontWeight.ExtraBold) // 800
)

// shadcn/ui inspired spacing system
object ShadcnSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val xxxl = 64.dp
}

// shadcn/ui inspired typography with Inter font
object ShadcnTypography {
    val h1 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 48.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).sp
    )
    
    val h2 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.02).sp
    )
    
    val h3 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.02).sp
    )
    
    val h4 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.02).sp
    )
    
    val p = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )
    
    val large = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.01).sp
    )
    
    val small = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    )
    
    val muted = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )
    
    val lead = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.01).sp
    )
}

// Border radius values
object ShadcnBorderRadius {
    val sm = 6.dp
    val md = 8.dp
    val lg = 12.dp
    val xl = 16.dp
}

// Shadow elevations
object ShadcnElevation {
    val sm = 2.dp
    val md = 4.dp
    val lg = 8.dp
    val xl = 12.dp
}

// Custom theme data class
data class ShadcnThemeColors(
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

// CompositionLocal for shadcn theme
val LocalShadcnColors = staticCompositionLocalOf {
    ShadcnThemeColors(
        background = ShadcnColors.Background,
        foreground = ShadcnColors.Foreground,
        card = ShadcnColors.Card,
        cardForeground = ShadcnColors.CardForeground,
        popover = ShadcnColors.Popover,
        popoverForeground = ShadcnColors.PopoverForeground,
        primary = ShadcnColors.Primary,
        primaryForeground = ShadcnColors.PrimaryForeground,
        secondary = ShadcnColors.Secondary,
        secondaryForeground = ShadcnColors.SecondaryForeground,
        muted = ShadcnColors.Muted,
        mutedForeground = ShadcnColors.MutedForeground,
        accent = ShadcnColors.Accent,
        accentForeground = ShadcnColors.AccentForeground,
        destructive = ShadcnColors.Destructive,
        destructiveForeground = ShadcnColors.DestructiveForeground,
        border = ShadcnColors.Border,
        input = ShadcnColors.Input,
        ring = ShadcnColors.Ring,
        success = ShadcnColors.Success,
        warning = ShadcnColors.Warning,
        info = ShadcnColors.Info
    )
}

@Composable
fun ShadcnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        ShadcnThemeColors(
            background = ShadcnColors.Dark.Background,
            foreground = ShadcnColors.Dark.Foreground,
            card = ShadcnColors.Dark.Card,
            cardForeground = ShadcnColors.Dark.CardForeground,
            popover = ShadcnColors.Dark.Popover,
            popoverForeground = ShadcnColors.Dark.PopoverForeground,
            primary = ShadcnColors.Dark.Primary,
            primaryForeground = ShadcnColors.Dark.PrimaryForeground,
            secondary = ShadcnColors.Dark.Secondary,
            secondaryForeground = ShadcnColors.Dark.SecondaryForeground,
            muted = ShadcnColors.Dark.Muted,
            mutedForeground = ShadcnColors.Dark.MutedForeground,
            accent = ShadcnColors.Dark.Accent,
            accentForeground = ShadcnColors.Dark.AccentForeground,
            destructive = ShadcnColors.Dark.Destructive,
            destructiveForeground = ShadcnColors.Dark.DestructiveForeground,
            border = ShadcnColors.Dark.Border,
            input = ShadcnColors.Dark.Input,
            ring = ShadcnColors.Dark.Ring,
            success = ShadcnColors.Dark.Success,
            warning = ShadcnColors.Dark.Warning,
            info = ShadcnColors.Dark.Info
        )
    } else {
        ShadcnThemeColors(
            background = ShadcnColors.Background,
            foreground = ShadcnColors.Foreground,
            card = ShadcnColors.Card,
            cardForeground = ShadcnColors.CardForeground,
            popover = ShadcnColors.Popover,
            popoverForeground = ShadcnColors.PopoverForeground,
            primary = ShadcnColors.Primary,
            primaryForeground = ShadcnColors.PrimaryForeground,
            secondary = ShadcnColors.Secondary,
            secondaryForeground = ShadcnColors.SecondaryForeground,
            muted = ShadcnColors.Muted,
            mutedForeground = ShadcnColors.MutedForeground,
            accent = ShadcnColors.Accent,
            accentForeground = ShadcnColors.AccentForeground,
            destructive = ShadcnColors.Destructive,
            destructiveForeground = ShadcnColors.DestructiveForeground,
            border = ShadcnColors.Border,
            input = ShadcnColors.Input,
            ring = ShadcnColors.Ring,
            success = ShadcnColors.Success,
            warning = ShadcnColors.Warning,
            info = ShadcnColors.Info
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

    CompositionLocalProvider(LocalShadcnColors provides colors) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}

// Extension to access shadcn colors easily
object ShadcnTheme {
    val colors: ShadcnThemeColors
        @Composable
        get() = LocalShadcnColors.current
}
