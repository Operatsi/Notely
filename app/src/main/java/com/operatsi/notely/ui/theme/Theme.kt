package com.operatsi.notely.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeSetting {
    SYSTEM,
    LIGHT,
    DARK
}

private val DarkColorScheme = darkColorScheme(
    primary = NotelyBlueDark,
    onPrimary = DarkBackground,
    primaryContainer = NotelyBlueContainerDark,
    onPrimaryContainer = NotelyBlueDark,
    secondary = NotelyBlueDark,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = NotelyBlueLight,
    onPrimary = LightSurface,
    primaryContainer = NotelyBlueContainerLight,
    onPrimaryContainer = NotelyBlueLight,
    secondary = NotelyBlueLight,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

val LocalThemeIsDark = staticCompositionLocalOf { false }

@Composable
fun getNoteColor(colorIndex: Int): Color {
    val isDark = LocalThemeIsDark.current
    val list = if (isDark) NoteTintsDark else NoteTintsLight
    return list[colorIndex.coerceIn(0, list.lastIndex)]
}

@Composable
fun getNoteBorderColor(colorIndex: Int): Color {
    val isDark = LocalThemeIsDark.current
    val list = if (isDark) NoteBordersDark else NoteBordersLight
    return list[colorIndex.coerceIn(0, list.lastIndex)]
}

@Composable
fun NotelyTheme(
    themeSetting: ThemeSetting = ThemeSetting.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeSetting) {
        ThemeSetting.SYSTEM -> isSystemInDarkTheme()
        ThemeSetting.LIGHT -> false
        ThemeSetting.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
