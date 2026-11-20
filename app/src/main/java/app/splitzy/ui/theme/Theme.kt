package app.splitzy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalExtraColors = staticCompositionLocalOf { SplitzyExtraColors() }

/**
 * Splitzy theme. Dynamic (wallpaper) colour is intentionally not used: the brand
 * palette is the single source of truth in both light and dark modes.
 */
@Composable
fun SplitzyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalExtraColors provides SplitzyExtraColors()) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = SplitzyTypography,
            shapes = SplitzyShapes,
            content = content,
        )
    }
}

object SplitzyTheme {
    val extraColors: SplitzyExtraColors
        @Composable @ReadOnlyComposable
        get() = LocalExtraColors.current
}
