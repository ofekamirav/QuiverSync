//package org.example.quiversync.presentation.screens.spots
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import org.example.quiversync.R
//import org.example.quiversync.domain.model.FavoriteSpot
//import org.example.quiversync.domain.model.FavoriteSpots
//import org.example.quiversync.features.quiver.QuiverState
//import org.example.quiversync.features.spots.FavSpotMainPage.FavSpotsState
//import org.example.quiversync.features.spots.FavSpotMainPage.FavSpotsViewModel
//import org.example.quiversync.presentation.components.ErrorContent
//import org.example.quiversync.presentation.screens.quiver.QuiverContent
//import org.example.quiversync.presentation.screens.skeletons.FavoriteSpotsScreenSkeleton
//import org.example.quiversync.presentation.screens.skeletons.QuiverScreenSkeleton
//import org.example.quiversync.presentation.theme.OceanPalette
//import org.example.quiversync.presentation.widgets.spots_screen.ExpandableSpotCard
//import org.example.quiversync.utils.LocalWindowInfo
//import org.example.quiversync.utils.WindowWidthSize
//import org.koin.androidx.compose.koinViewModel
//
//
//@Composable
//fun FavoriteSpotsScreen(
//    modifier: Modifier = Modifier,
//    onAddSpotClick: () -> Unit = {},
//    viewModel: FavSpotsViewModel = koinViewModel()
//) {
//    val uiState = viewModel.uiState.collectAsState().value
//
//    when (uiState) {
//        is FavSpotsState.Error -> ErrorContent(uiState.message)
//        is FavSpotsState.Loading -> {
//            FavoriteSpotsScreenSkeleton(modifier)
//        }
//        is FavSpotsState.Loaded -> FavoriteSpotsContent(modifier, uiState.spots, onAddSpotClick)
//    }
//}
//
//@Composable
//fun FavoriteSpotsContent(modifier: Modifier, spots: FavoriteSpots, onAddSpotClick: () -> Unit){
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//            .padding(16.dp)
//    ) {
//        val windowInfo = LocalWindowInfo.current
//
//        when (windowInfo.widthSize) {
//            WindowWidthSize.COMPACT -> {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    items(spots.items.size) { spot ->
//                        ExpandableSpotCard(spots.items[spot])
//                    }
//                }
//            }
//
//            else -> { // MEDIUM or EXPANDED
//                LazyVerticalGrid(
//                    columns = GridCells.Adaptive(minSize = 300.dp),
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    items(spots.items.size) { spot ->
//                        ExpandableSpotCard(spots.items[spot])
//                    }
//                }
//            }
//        }
//        FloatingActionButton(
//            onClick = { onAddSpotClick() },
//            containerColor = MaterialTheme.colorScheme.primary,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .size(60.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Add,
//                contentDescription = "Add Spot",
//                tint = Color.White,
//                modifier = Modifier.size(32.dp)
//            )
//        }
//    }
//}
//
//
