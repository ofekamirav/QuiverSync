package org.example.quiversync

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.Firebase
import com.google.firebase.initialize
import org.example.quiversync.presentation.navigation.AppNavigation
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.AppWindowInfo
import org.example.quiversync.utils.ProvideWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //Check window size
            val windowSizeClass = calculateWindowSizeClass(this)
            val widthSize = when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> WindowWidthSize.COMPACT
                WindowWidthSizeClass.Medium -> WindowWidthSize.MEDIUM
                WindowWidthSizeClass.Expanded -> WindowWidthSize.EXPANDED
                else -> WindowWidthSize.COMPACT
            }
            val windowInfo = AppWindowInfo(widthSize = widthSize)
            ProvideWindowInfo(windowInfo = windowInfo) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    QuiverSyncTheme {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    QuiverSyncTheme {
        AppNavigation()
    }
}
