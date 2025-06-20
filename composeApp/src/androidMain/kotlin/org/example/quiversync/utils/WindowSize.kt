package org.example.quiversync.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

enum class WindowWidthSize {
    COMPACT,
    MEDIUM,
    EXPANDED
}

data class AppWindowInfo(
    val widthSize: WindowWidthSize
)
@Composable
fun ProvideWindowInfo(windowInfo: AppWindowInfo,content: @Composable (() -> Unit)) {
    CompositionLocalProvider(LocalWindowInfo provides windowInfo) {
        content()
    }
}
val LocalWindowInfo = staticCompositionLocalOf {
    AppWindowInfo(widthSize = WindowWidthSize.COMPACT)
}

