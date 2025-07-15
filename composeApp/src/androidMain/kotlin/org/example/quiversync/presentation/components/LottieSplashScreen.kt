package org.example.quiversync.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@Composable
fun LottieSplashScreen(
    animationFileName: String,
    iterations: Int = 1,
    onAnimationFinished: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current

    val actualAnimationSize = when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> 400.dp
        WindowWidthSize.MEDIUM -> 500.dp
        WindowWidthSize.EXPANDED -> 600.dp
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(animationFileName))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        speed = 1f,
        restartOnPlay = false
    )

    if (progress == 1f && composition != null) {
        onAnimationFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(actualAnimationSize)
        )
    }
}