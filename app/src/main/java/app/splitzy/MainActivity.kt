package app.splitzy

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import app.splitzy.data.DataStoreUserPreferences
import app.splitzy.data.ThemeMode
import app.splitzy.ui.calculator.CalculatorRoute
import app.splitzy.ui.theme.SplitzyTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Null until the saved theme loads; the splash stays up so there's no light/dark flash.
        val themeMode: StateFlow<ThemeMode?> = DataStoreUserPreferences(this).themeMode
            .stateIn(lifecycleScope, SharingStarted.Eagerly, null)
        splash.setKeepOnScreenCondition { themeMode.value == null }

        setContent {
            val mode by themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (mode) {
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
                ThemeMode.System, null -> isSystemInDarkTheme()
            }

            DisposableEffect(darkTheme) {
                // The hero header is always navy, so status bar icons stay light.
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    },
                )
                onDispose {}
            }

            SplitzyTheme(darkTheme = darkTheme) {
                CalculatorRoute(isDarkTheme = darkTheme)
            }
        }
    }
}
