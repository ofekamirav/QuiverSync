package org.example.quiversync.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
        WindowWidthSize.COMPACT -> 500.dp
        WindowWidthSize.MEDIUM -> 540.dp
        WindowWidthSize.EXPANDED -> 740.dp
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
        ,
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(actualAnimationSize)
        )
    }
}