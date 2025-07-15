package org.example.quiversync.presentation.screens.spots

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.quiversync.R
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.FavoriteSpots
import org.example.quiversync.features.quiver.QuiverState
import org.example.quiversync.features.spots.FavSpotMainPage.FavSpotsData
import org.example.quiversync.features.spots.FavSpotMainPage.FavSpotsEvent
import org.example.quiversync.features.spots.FavSpotMainPage.FavSpotsState
import org.example.quiversync.features.spots.FavSpotMainPage.FavSpotsViewModel
import org.example.quiversync.presentation.components.ErrorContent
import org.example.quiversync.presentation.screens.quiver.QuiverContent
import org.example.quiversync.presentation.screens.skeletons.FavoriteSpotsScreenSkeleton
import org.example.quiversync.presentation.screens.skeletons.QuiverScreenSkeleton
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.widgets.spots_screen.ExpandableSpotCard
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteSpotsScreen(
    modifier: Modifier = Modifier,
    onAddSpotClick: () -> Unit = {},
    viewModel: FavSpotsViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val uiState = viewModel.uiState.collectAsState().value
    val isImperial = viewModel.isImperialUnits.collectAsState().value

    when (uiState) {
        is FavSpotsState.Error -> ErrorContent(uiState.message)
        is FavSpotsState.Loading -> {
            FavoriteSpotsScreenSkeleton(modifier)
        }
        is FavSpotsState.Loaded -> FavoriteSpotsContent(
            modifier = modifier.fillMaxSize(),
            favSpotsData = uiState.favSpotsData,
            onAddSpotClick = onAddSpotClick,
            onDeleteSpot = { spotToDelete ->
                viewModel.onEvent(FavSpotsEvent.DeleteSpot(spotToDelete))
            },
            onUndoDeleteSpot = {
                viewModel.onEvent(FavSpotsEvent.UndoDeleteSpot)
            },
            snackbarHostState = snackbarHostState,
            isImperial = isImperial
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FavoriteSpotsContent(
    modifier: Modifier,
    favSpotsData: FavSpotsData,
    onDeleteSpot: (FavoriteSpot) -> Unit,
    onUndoDeleteSpot: () -> Unit,
    onAddSpotClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    isImperial: Boolean
){
    val spots = favSpotsData.spots
    val allPredictions = favSpotsData.allSpotsDailyPredictions
    val boards = favSpotsData.boards
    val currentForecasts = favSpotsData.currentForecastsForAllSpots
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val windowInfo = LocalWindowInfo.current
        if (spots.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Favorite Spots Found",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add your first favorite spot by clicking the button below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            when (windowInfo.widthSize) {
                WindowWidthSize.COMPACT -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(
                            spots.size,
                            key = { spots[it].spotID }) { index ->
                            val spot = spots[index]

                            val associatedPrediction = allPredictions.find { prediction ->
                                prediction.forecastLatitude == spot.spotLatitude &&
                                        prediction.forecastLongitude == spot.spotLongitude
                            }
                            val associatedForecast = currentForecasts.find { forecast ->
                                forecast.latitude == spot.spotLatitude &&
                                        forecast.longitude == spot.spotLongitude
                            }
                            val bestBoard = associatedPrediction?.let { prediction ->
                                boards.find { it.id == prediction.surfboardID }
                            }
                            val score = associatedPrediction?.score?.toString() ?: "N/A"

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                        coroutineScope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Spot deleted: ${spot.name}",
                                                actionLabel = "Undo",
                                                duration = SnackbarDuration.Long
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                onUndoDeleteSpot()
                                            } else {
                                                onDeleteSpot(spot)
                                            }
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromEndToStart = true,
                                enableDismissFromStartToEnd = false,
                                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                backgroundContent = {
                                    val direction = dismissState.dismissDirection ?: return@SwipeToDismissBox
                                    val color by animateColorAsState(
                                        when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                            else -> Color.Transparent
                                        }, label = "dismissColor"
                                    )
                                    val icon = Icons.Filled.Delete
                                    val scale by animateFloatAsState(
                                        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                                        animationSpec = tween(durationMillis = 200), label = "dismissIconScale"
                                    )
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = when (direction) {
                                            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                            else -> Alignment.Center
                                        }
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = "Delete Icon",
                                            modifier = Modifier.scale(scale),
                                            tint = Color.White
                                        )
                                    }
                                },
                                content = {
                                    bestBoard?.let {
                                        associatedForecast?.let { forecast ->
                                            ExpandableSpotCard(
                                                spot = spot,
                                                score = score,
                                                surfboard = it,
                                                forecast = forecast,
                                                onWeeklyForecastClick = {
                                                    // Handle weekly forecast click if needed
                                                },
                                                isImperial = isImperial
                                            )
                                        }
                                    }
                                }
                            )
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
                        items(
                            spots.size,
                            key = { spots[it].spotID }) { index ->
                            val spot = spots[index]

                            val associatedPrediction = allPredictions.find { prediction ->
                                prediction.forecastLatitude == spot.spotLatitude &&
                                        prediction.forecastLongitude == spot.spotLongitude
                            }
                            val associatedForecast = currentForecasts.find { forecast ->
                                forecast.latitude == spot.spotLatitude &&
                                        forecast.longitude == spot.spotLongitude
                            }
                            val bestBoard = associatedPrediction?.let { prediction ->
                                boards.find { it.id == prediction.surfboardID }
                            }
                            val score = associatedPrediction?.score?.toString() ?: "N/A"

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                        coroutineScope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Spot deleted: ${spot.name}",
                                                actionLabel = "Undo",
                                                duration = SnackbarDuration.Long
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                onUndoDeleteSpot()
                                            } else {
                                                onDeleteSpot(spot)
                                            }
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromEndToStart = true,
                                enableDismissFromStartToEnd = false,
                                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                backgroundContent = {
                                    val direction = dismissState.dismissDirection ?: return@SwipeToDismissBox
                                    val color by animateColorAsState(
                                        when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                            else -> Color.Transparent
                                        }, label = "dismissColor"
                                    )
                                    val icon = Icons.Filled.Delete
                                    val scale by animateFloatAsState(
                                        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                                        animationSpec = tween(durationMillis = 200), label = "dismissIconScale"
                                    )
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = when (direction) {
                                            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                            else -> Alignment.Center
                                        }
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = "Delete Icon",
                                            modifier = Modifier.scale(scale),
                                            tint = Color.White
                                        )
                                    }
                                },
                                content = {
                                    bestBoard?.let {
                                        associatedForecast?.let { forecast ->
                                            ExpandableSpotCard(
                                                spot = spot,
                                                score = score,
                                                surfboard = it,
                                                forecast = forecast,
                                                onWeeklyForecastClick = {
                                                    // Handle weekly forecast click if needed
                                                }
                                            )
                                        }
                                    }
                                }
                            )

                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { onAddSpotClick() },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(80.dp)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Spot",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


@Preview(name = "FavoriteSpots Light", showBackground = true)
@Composable
fun FavoriteSpotsScreenLightPreview() {
    FavoriteSpotsScreen(
        snackbarHostState = SnackbarHostState(),
        onAddSpotClick = {}
    )
}

@Preview(name = "FavoriteSpots Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FavoriteSpotsScreenDarkPreview() {
    FavoriteSpotsScreen(
        snackbarHostState = SnackbarHostState(),
        onAddSpotClick = {}
    )
}
