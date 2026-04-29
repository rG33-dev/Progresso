import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.plcoding.habittracker.ui.theme.BorderCoffee
import com.plcoding.habittracker.ui.theme.Cappuccino
import com.plcoding.habittracker.ui.theme.Cream
import com.plcoding.habittracker.ui.theme.DestructiveRed
import com.plcoding.habittracker.ui.theme.Espresso
import com.plcoding.habittracker.ui.theme.Froth
import com.plcoding.habittracker.ui.theme.Latte
import com.plcoding.habittracker.ui.theme.Mocha
import com.plcoding.habittracker.ui.theme.StreakOrange
import com.plcoding.habittracker.ui.theme.SuccessCoffee
import com.plcoding.habittracker.ui.theme.Typography


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
    DarkExtendedColors
}
private val DarkColorScheme = darkColorScheme(
    primary = Latte,
    onPrimary = Espresso,
    primaryContainer = Cappuccino,
    secondary = Cappuccino,
    onSecondary = Cream,
    tertiary = StreakOrange,
    background = Espresso,      // Dark background
    onBackground = Cream,       // Main text
    surface = Mocha,            // Card backgrounds
    onSurface = Cream,
    onSurfaceVariant = Froth,
    surfaceContainerLow = Espresso,
    surfaceContainer = Mocha,
    surfaceContainerHigh = Mocha,
    inversePrimary = Latte,
    error = DestructiveRed,
    onError = Cream,
    outline = BorderCoffee,
    outlineVariant = BorderCoffee,
)
private val DarkExtendedColors = ExtendedColors(
    surfaceElevated = Mocha,
    surfaceBright = Color(0xFF3D2B24),
    success = SuccessCoffee,
    streak = StreakOrange,
    accentPink = Color(0xFFE29578), // Peach/Coffee accent
    destructive = DestructiveRed,
    textSecondary = Froth,
    textTertiary = Froth.copy(alpha = 0.6f),
    border = BorderCoffee,
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