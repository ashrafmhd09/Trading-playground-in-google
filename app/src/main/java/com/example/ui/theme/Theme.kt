package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    primaryContainer = BlueContainer,
    onPrimaryContainer = OnBlueContainer,
    secondary = BluePrimaryLight,
    onSecondary = White,
    secondaryContainer = BlueContainer,
    onSecondaryContainer = OnBlueContainer,
    tertiary = BlueLighter,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkSurfaceBorder,
    error = WarningRed,
    errorContainer = Color(0xFF7F1D1D),
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF0C1E3B),
    secondary = BluePrimaryLight,
    onSecondary = White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF082F4F),
    tertiary = BlueLighter,
    background = Color(0xFFFAFBFC),
    onBackground = GrayDark,
    surface = White,
    onSurface = GrayDark,
    surfaceVariant = GrayLight,
    onSurfaceVariant = GrayText,
    outline = GrayMedium,
    error = WarningRed,
    errorContainer = Color(0xFFFEE2E2),
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to blue & white dark mode
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
