package org.example.quiversync.presentation.screens.skeletons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.ShimmerBrush
import org.example.quiversync.utils.WindowWidthSize
import org.example.quiversync.utils.rememberShimmerBrush

@Composable
fun ExpandableSpotCardSkeleton(brush: Brush) {
    val isDark = isSystemInDarkTheme()
    val cardColor = if(isDark) MaterialTheme.colorScheme.surface else Color.White.copy(alpha = 0.8f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Placeholder for Title and Subtitle
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
            }

            // Placeholder for Arrow Icon
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
        }
    }
}

@Composable
fun FavoriteSpotsScreenSkeleton(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val brush = rememberShimmerBrush()
    val windowInfo = LocalWindowInfo.current

    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(6) {
                    ExpandableSpotCardSkeleton(brush = brush)
                }
            }
        }
        else -> { // MEDIUM or EXPANDED
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(8) {
                    ExpandableSpotCardSkeleton(brush = brush)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Favorite Spots Skeleton - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewFavoriteSpotsScreenSkeleton() {
    QuiverSyncTheme(darkTheme = true) {
        FavoriteSpotsScreenSkeleton()
    }
}

@Preview(showBackground = true, name = "Favorite Spots Skeleton - Light", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewFavoriteSpotsScreenSkeletonLight() {
    QuiverSyncTheme(darkTheme = false) {
        FavoriteSpotsScreenSkeleton()
    }
}


