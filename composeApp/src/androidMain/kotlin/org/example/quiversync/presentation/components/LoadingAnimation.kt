package org.example.quiversync.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*


@Composable
fun LoadingAnimation(
    isLoading: Boolean,
    animationFileName: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    animationSize: Dp = 120.dp
) {
    if (isLoading) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset(animationFileName))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever //loop
            )

            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(animationSize)
            )
        }
    }
}


