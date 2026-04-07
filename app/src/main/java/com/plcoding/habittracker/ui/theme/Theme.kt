package com.plcoding.habittracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val surfaceElevated: Color,
    val surfaceBright: Color,
    val success: Color,
    val streak: Color,
    val accentPink: Color,
    val destructive: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val border: Color,
)

private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        surfaceElevated = Color.Unspecified,
        surfaceBright = Color.Unspecified,
        success = Color.Unspecified,
        streak = Color.Unspecified,
        accentPink = Color.Unspecified,
        destructive = Color.Unspecified,
        textSecondary = Color.Unspecified,
        textTertiary = Color.Unspecified,
        border = Color.Unspecified,
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = TextPrimary,
    tertiary = AccentPink,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLow = Surface,
    surfaceContainer = Surface,
    surfaceContainerHigh = SurfaceElevated,
    inversePrimary = PrimaryDark,
    error = Destructive,
    onError = TextPrimary,
    outline = Border,
    outlineVariant = Border,
)

private val DarkExtendedColors = ExtendedColors(
    surfaceElevated = SurfaceElevated,
    surfaceBright = SurfaceBright,
    success = Success,
    streak = Streak,
    accentPink = AccentPink,
    destructive = Destructive,
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    border = Border,
)

@Composable
fun HabitTrackerTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalExtendedColors provides DarkExtendedColors,
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content,
        )
    }
}

object HabitTrackerTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}
