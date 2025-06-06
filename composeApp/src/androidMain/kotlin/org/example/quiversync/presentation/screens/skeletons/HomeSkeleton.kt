package org.example.quiversync.presentation.screens.skeletons

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.ShimmerBrush

@Composable
fun HomeSkeleton() {
    val baseShimmerColor = if (isSystemInDarkTheme()) {
        OceanPalette.DarkText
    } else {
        OceanPalette.TextDark
    }

    val brush = ShimmerBrush(baseColor = baseShimmerColor)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(brush)
        )

        // Current Conditions Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
        )

        // Board Recommendation Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
        )

        // Forecast Items
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

@Preview(showBackground = true, name = "Skeleton - Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenSkeletonPreviewDark() {
    QuiverSyncTheme(darkTheme = true) {
        HomeSkeleton()
    }
}