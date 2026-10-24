package com.example.weekendx.ui.theme

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
import com.example.weekendx.data.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlueLight,
    onPrimary = DarkBackground,
    primaryContainer = Slate800,
    onPrimaryContainer = Color.White,
    secondary = Slate400,
    onSecondary = DarkBackground,
    surface = DarkSurface,
    onSurface = Slate100,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = Slate300,
    background = DarkBackground,
    onBackground = Slate100,
    outline = Slate700
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = Slate100,
    onPrimaryContainer = Slate900,
    secondary = Slate600,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate50,
    onSurfaceVariant = Slate700,
    background = Slate50,
    onBackground = Slate900,
    outline = Slate200
)

@Composable
fun WeekendXTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
