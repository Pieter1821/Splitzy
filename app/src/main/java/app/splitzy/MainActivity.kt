package app.splitzy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.splitzy.ui.brand.SplitzyLogo
import app.splitzy.ui.theme.SplitzyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitzyTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SplitzyTheme.extraColors.hero),
                    contentAlignment = Alignment.Center,
                ) {
                    SplitzyLogo()
                }
            }
        }
    }
}
