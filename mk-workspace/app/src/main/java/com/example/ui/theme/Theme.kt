package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CustomDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = DarkGraySurface400,
    onPrimaryContainer = DarkTextPrimary,
    secondary = CyberCyan,
    onSecondary = DeepBlackBg,
    secondaryContainer = DarkGrayCard600,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = SoftOrange,
    onTertiary = DeepBlackBg,
    background = DeepBlackBg,
    onBackground = DarkTextPrimary,
    surface = DarkGraySurface200,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkGraySurface400,
    onSurfaceVariant = DarkTextSecondary,
    outline = GlassBorderColor,
    error = Color(0xFFCF6679)
)

// Fallback light color scheme is automatically mapped to keep builds safe, 
// though the operational environment enforces the luxury Dark Mode first model.
private val CustomLightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    background = Color(0xFFF5F5FA),
    onBackground = Color(0xFF1c1b1f),
    surface = Color.White,
    onSurface = Color(0xFF1c1b1f),
    surfaceVariant = Color(0xFFE2E2EC),
    onSurfaceVariant = Color(0xFF49454f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark mode format
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve our designer theme brand
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CustomDarkColorScheme else CustomLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
