package org.example.quiversync.presentation.screens.spots

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.widgets.spots_screen.ExpandableSpotCard
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize


@Composable
fun FavoriteSpotsScreen(spots: List<FavoriteSpot> = emptyList()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val windowInfo = LocalWindowInfo.current

        when (windowInfo.widthSize) {
            WindowWidthSize.COMPACT -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(spots.size) { spot ->
                        ExpandableSpotCard(spots[spot])
                    }
                }
            }

            else -> { // MEDIUM or EXPANDED
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 300.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(spots.size) { spot ->
                        ExpandableSpotCard(spots[spot])
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        FloatingActionButton(
            onClick = { /* Handle floating action button click */ },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.End).size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Spot",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}



