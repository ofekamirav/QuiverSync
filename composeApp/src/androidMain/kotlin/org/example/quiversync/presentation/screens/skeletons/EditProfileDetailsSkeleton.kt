package org.example.quiversync.presentation.screens.skeletons

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.ShimmerBrush
import org.example.quiversync.utils.WindowWidthSize

@Composable
private fun EditProfileHeaderSkeleton(brush: Brush) {
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
}

@Composable
private fun EditFieldSkeleton(brush: Brush, labelWidth: Dp, valueWidth: Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(16.dp)
                .width(labelWidth)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .height(32.dp) // Made the field taller
                .width(valueWidth)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
    }
}

@Composable
private fun EditProfileFormSkeleton(brush: Brush, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditFieldSkeleton(brush = brush, labelWidth = 80.dp, valueWidth = 220.dp) // Name
        EditFieldSkeleton(brush = brush, labelWidth = 100.dp, valueWidth = 140.dp) // Height
        EditFieldSkeleton(brush = brush, labelWidth = 100.dp, valueWidth = 140.dp) // Weight
        EditFieldSkeleton(brush = brush, labelWidth = 120.dp, valueWidth = 100.dp) // Surf Level
        Spacer(modifier = Modifier.weight(1f))
        // Save button placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(brush)
        )
    }
}

@Composable
fun EditProfileSkeleton(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val baseShimmerColor = if (isDark) OceanPalette.DarkText else OceanPalette.TextDark
    val brush = ShimmerBrush(baseColor = baseShimmerColor)
    val backgroundColor = if (isDark) OceanPalette.DarkBackground else OceanPalette.background

    val windowInfo = LocalWindowInfo.current
    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EditProfileHeaderSkeleton(brush = brush)
                EditProfileFormSkeleton(brush = brush, modifier = Modifier.weight(1f))
            }
        }
        else -> { // MEDIUM or EXPANDED
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.weight(0.4f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EditProfileHeaderSkeleton(brush = brush)
                }
                Column(modifier = Modifier.weight(0.6f)) {
                    EditProfileFormSkeleton(brush = brush, modifier = Modifier.fillMaxHeight())
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Edit Profile Skeleton - Light")
@Composable
fun EditProfileSkeletonPreviewLight() {
    QuiverSyncTheme(darkTheme = false) {
        EditProfileSkeleton()
    }
}

@Preview(showBackground = true, name = "Edit Profile Skeleton - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EditProfileSkeletonPreviewDark() {
    QuiverSyncTheme(darkTheme = true) {
        EditProfileSkeleton()
    }
}

@Preview(showBackground = true, name = "Edit Profile Skeleton - Tablet", widthDp = 840)
@Composable
fun EditProfileSkeletonTabletPreview() {
    QuiverSyncTheme(darkTheme = false) {
        EditProfileSkeleton()
    }
}