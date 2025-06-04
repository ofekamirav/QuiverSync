package org.example.quiversync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.example.quiversync.presentation.navigation.AppNavigation
import org.example.quiversync.presentation.theme.QuiverSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                QuiverSyncTheme {
                    AppNavigation()
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