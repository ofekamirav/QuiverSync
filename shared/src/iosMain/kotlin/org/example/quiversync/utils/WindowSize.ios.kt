package org.example.quiversync.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
actual fun ProvideWindowInfo(windowInfo: AppWindowInfo, content: @Composable (() -> Unit)) {
    CompositionLocalProvider(LocalWindowInfo provides windowInfo) {
        content()
    }
}