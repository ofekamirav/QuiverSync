package org.example.quiversync.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun ProvideWindowInfo(windowInfo: AppWindowInfo,content: @Composable (() -> Unit)) {
    CompositionLocalProvider(LocalWindowInfo provides windowInfo) {
        content()
    }
}