package io.peng.sparrowdelivery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.peng.sparrowdelivery.R

/**
 * Stitch Design System Typography
 * Based on Spline Sans font family from the reference designs
 */

// Spline Sans font family - now properly loaded from res/font/
val SplineSansFontFamily = FontFamily(
    Font(R.font.spline_sans_regular, FontWeight.Normal),
    Font(R.font.spline_sans_medium, FontWeight.Medium),
    Font(R.font.spline_sans_semibold, FontWeight.SemiBold),
    Font(R.font.spline_sans_bold, FontWeight.Bold),
)

/**
 * Stitch Typography Scale
 * Optimized for mobile delivery app with clear hierarchy
 */
val StitchTypography = Typography(
    // Display styles - Large headings, onboarding
    displayLarge = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp
    ),
    displaySmall = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // Headlines - Screen titles, section headers
    headlineLarge = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.15).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    
    // Titles - Card headers, form labels
    titleLarge = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.15).sp
    ),
    titleMedium = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body text - Main content, descriptions
    bodyLarge = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Labels - Buttons, tabs, small UI elements
    labelLarge = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Additional text styles for Stitch-specific components
 */
object StitchTextStyles {
    // Onboarding hero text
    val HeroTitle = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    )
    
    // Onboarding subtitle
    val HeroSubtitle = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
    
    // Button text
    val ButtonLarge = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    )
    
    val ButtonMedium = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    
    // Tab labels
    val TabLabel = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    
    // Form field labels
    val FieldLabel = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    
    // Input placeholder text
    val Placeholder = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    
    // Price/cost display
    val PriceDisplay = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.1).sp
    )
    
    // Driver info/timeline text
    val InfoText = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    
    // Error/warning messages
    val StatusText = TextStyle(
        fontFamily = SplineSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}

/**
 * Spline Sans Font Implementation
 * 
 * ✅ Spline Sans fonts are now properly loaded from res/font/:
 *    - spline_sans_regular.ttf (400)
 *    - spline_sans_medium.ttf (500) 
 *    - spline_sans_semibold.ttf (600)
 *    - spline_sans_bold.ttf (700)
 *    - spline_sans_font_family.xml (font family definition)
 * 
 * The beautiful Spline Sans typography from the Stitch reference designs
 * is now active throughout the app!
 */
