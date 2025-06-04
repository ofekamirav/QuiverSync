package org.example.quiversync.presentation.screens.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import org.example.quiversync.presentation.theme.QuiverSyncTheme

@Composable
fun HomeScreenSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Location placeholder
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .placeholder(
                    visible = true,
                    color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
                    highlight = PlaceholderHighlight.shimmer()
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {}

        Spacer(modifier = Modifier.height(16.dp))

        // Current Conditions Card
        repeat(1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .placeholder(visible = true,
                        highlight = PlaceholderHighlight.shimmer()
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Board Recommendation Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer()
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Forecast Items
        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(vertical = 4.dp)
                    .placeholder(visible = true,
                        highlight = PlaceholderHighlight.shimmer()
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenSkeletonPreview() {
    QuiverSyncTheme {
        HomeScreenSkeleton()
    }
}
