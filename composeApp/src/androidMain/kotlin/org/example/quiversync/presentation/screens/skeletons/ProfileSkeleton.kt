package org.example.quiversync.presentation.screens.skeletons

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private fun ProfileHeaderSkeleton(brush: Brush, cardColor: Color, dividerColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Avatar & Name Skeleton
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush)
            )
        }

        // Stats Section Skeleton
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardColor)
                .padding(vertical = 16.dp)
        ) {
            StatItemSkeleton(brush)
            VerticalDivider(color = dividerColor)
            StatItemSkeleton(brush)
            VerticalDivider(color = dividerColor)
            StatItemSkeleton(brush)
        }
    }
}
@Composable
private fun ProfileDetailsSkeleton(brush: Brush, cardColor: Color, dividerColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor)
            .padding(vertical = 4.dp)
    ) {
        repeat(5) {
            UserDetailItemSkeleton(brush)
            if (it < 4) {
                Divider(color = dividerColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
@Composable
private fun UserDetailItemSkeleton(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon placeholder
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(brush)
        )
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Label placeholder
            Box(
                modifier = Modifier
                    .height(13.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            // Value placeholder
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .width(120.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}

@Composable
private fun StatItemSkeleton(brush: Brush) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Value placeholder
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        // Label placeholder
        Box(
            modifier = Modifier
                .height(14.dp)
                .width(50.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}

@Composable
fun ProfileSkeleton(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()
    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (isDark) OceanPalette.DarkBackground else OceanPalette.background
    val cardColor = if (isDark) OceanPalette.DarkSurface else Color.White
    val dividerColor = if (isDark) OceanPalette.DarkBorder else OceanPalette.BorderGray

    val windowInfo = LocalWindowInfo.current
    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ProfileHeaderSkeleton(brush,cardColor = cardColor, dividerColor = dividerColor)
                ProfileDetailsSkeleton(brush, cardColor, dividerColor)
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        else -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(0.4f)) {
                    ProfileHeaderSkeleton(brush,cardColor = cardColor, dividerColor = dividerColor)
                }
                Column(modifier = Modifier.weight(0.6f)) {
                    ProfileDetailsSkeleton(brush, cardColor, dividerColor)
                }
            }
        }
    }
}
@Composable
fun VerticalDivider(color: Color = Color.LightGray) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .width(1.dp)
            .background(color)
    )
}

@Preview(
    showBackground = true,
    name = "Profile Skeleton - Dark Mode",
)
@Composable
fun ProfileScreenSkeletonPreviewDark() {
    QuiverSyncTheme(darkTheme = false) {
        ProfileSkeleton()
    }
}
@Preview(showBackground = true, name = "Profile Skeleton - Light Mode", widthDp = 840) // Preview לטאבלט
@Composable
fun ProfileScreenSkeletonPreviewTablet() {
    QuiverSyncTheme(darkTheme = false) {
        ProfileSkeleton()
    }
}