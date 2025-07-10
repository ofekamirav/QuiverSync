package org.example.quiversync.presentation.screens.skeletons

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.ShimmerBrush
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@Composable
private fun MainContentSkeleton(brush: Brush) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(brush)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
        )
    }
}

@Composable
private fun ForecastPanelSkeleton(brush: Brush) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(brush)
        )
        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )
        }
    }
}


@Composable
fun HomeSkeleton(modifier: Modifier = Modifier) {
    val baseShimmerColor = if (isSystemInDarkTheme()) OceanPalette.DarkText else OceanPalette.TextDark
    val brush = ShimmerBrush(baseColor = baseShimmerColor)

    val windowInfo = LocalWindowInfo.current

    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MainContentSkeleton(brush = brush)
                ForecastPanelSkeleton(brush = brush)
            }
        }
        else -> { // MEDIUM or EXPANDED
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(0.6f)) {
                    MainContentSkeleton(brush = brush)
                }
                Column(modifier = Modifier.weight(0.4f)) {
                    ForecastPanelSkeleton(brush = brush)
                }
            }
        }
    }
}

@Preview(showBackground = true,)
@Composable
fun HomeScreenSkeletonPreview() {
    QuiverSyncTheme() {
        HomeSkeleton()
    }
}