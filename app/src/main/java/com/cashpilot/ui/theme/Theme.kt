package com.cashpilot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrimaryGreen = Color(0xFF2E7D32)
private val SecondaryBlue = Color(0xFF1E88E5)
private val Background = Color(0xFFF5F7FA)
private val Accent = Color(0xFF43A047)

private val LightColorScheme: ColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    background = Background,
    onBackground = Color(0xFF111827),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    primaryContainer = PrimaryGreen.copy(alpha = 0.08f),
    secondaryContainer = SecondaryBlue.copy(alpha = 0.08f),
    tertiary = Accent
)

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    background = Color(0xFF020617),
    onBackground = Color(0xFFF9FAFB),
    surface = Color(0xFF020617),
    onSurface = Color(0xFFF9FAFB),
    primaryContainer = PrimaryGreen.copy(alpha = 0.24f),
    secondaryContainer = SecondaryBlue.copy(alpha = 0.24f),
    tertiary = Accent
)

@Composable
fun CashPilotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

