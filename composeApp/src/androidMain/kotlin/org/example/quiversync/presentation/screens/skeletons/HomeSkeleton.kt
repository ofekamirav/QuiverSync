package org.example.quiversync.presentation.screens.skeletons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.example.quiversync.utils.rememberShimmerBrush


@Composable
fun HomeSkeleton(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()
    val windowInfo = LocalWindowInfo.current

    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp) // ריווח זהה לתוכן האמיתי
            ) {
                MainConditionsSkeleton(brush = brush)
                ForecastPanelSkeleton(brush = brush)
            }
        }

        else -> {
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // ריווח זהה לתוכן האמיתי
            ) {
                Column(
                    modifier = Modifier.weight(0.6f)
                ) {
                    MainConditionsSkeleton(brush = brush)
                }
                Column(
                    modifier = Modifier.weight(0.4f)
                ) {
                    ForecastPanelSkeleton(brush = brush)
                }
            }
        }
    }
}


@Composable
private fun MainConditionsSkeleton(brush: Brush) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerBox(modifier = Modifier.size(20.dp).clip(CircleShape), brush = brush)
            ShimmerBox(modifier = Modifier.height(20.dp).width(150.dp), brush = brush)
        }

        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            brush = brush
        )

        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            brush = brush
        )
    }
}

@Composable
private fun ForecastPanelSkeleton(brush: Brush) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ShimmerBox(
            modifier = Modifier
                .height(24.dp)
                .width(140.dp),
            brush = brush
        )
        repeat(5) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                brush = brush
            )
        }
    }
}

@Composable
private fun ShimmerBox(modifier: Modifier, brush: Brush) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
    )
}

@Preview(showBackground = true, name = "Home Skeleton Preview")
@Composable
fun HomeScreenSkeletonPreview() {
    QuiverSyncTheme {
        HomeSkeleton()
    }
}