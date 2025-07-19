package org.example.quiversync.presentation.screens.spots

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.features.spots.fav_spot_main_page.FavSpotsData
import org.example.quiversync.features.spots.fav_spot_main_page.FavSpotsEvent
import org.example.quiversync.features.spots.fav_spot_main_page.FavSpotsState
import org.example.quiversync.features.spots.fav_spot_main_page.FavSpotsViewModel
import org.example.quiversync.presentation.components.ErrorContent
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.screens.skeletons.FavoriteSpotsScreenSkeleton
import org.example.quiversync.presentation.widgets.spots_screen.ExpandableSpotCard
import org.example.quiversync.presentation.widgets.spots_screen.WeeklyForecastPopup
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
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
            onDeleteSpot = {
            },
            onUndoDeleteSpot = {
                viewModel.onEvent(FavSpotsEvent.UndoDeleteSpot)
            },
            snackbarHostState = snackbarHostState,
            isImperial = isImperial,
            onEvent = viewModel::onEvent
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FavoriteSpotsContent(
    modifier: Modifier,
    favSpotsData: FavSpotsData,
    onDeleteSpot: (FavoriteSpot) -> Unit,
    onUndoDeleteSpot: () -> Unit,
    onAddSpotClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    isImperial: Boolean,
    onEvent: (FavSpotsEvent) -> Unit = { }
) {
    val spots = favSpotsData.spots
    val allPredictions = favSpotsData.allSpotsDailyPredictions
    val boards = favSpotsData.boards
    val currentForecasts = favSpotsData.currentForecastsForAllSpots
    val coroutineScope = rememberCoroutineScope()
    var showWeeklyForecastPopup by remember { mutableStateOf(false) }
    var isLoadingWeeklyForecast by remember { mutableStateOf(false) }
    var selectedSpotForWeeklyForecast by remember { mutableStateOf<FavoriteSpot?>(null) }

    LaunchedEffect(
        selectedSpotForWeeklyForecast,
        favSpotsData.weeklyForecastForSpecificSpot,
        favSpotsData.weeklyPredictionsForSpecificSpot
    ) {
        selectedSpotForWeeklyForecast?.let { spot ->
            val loadedForecast = favSpotsData.weeklyForecastForSpecificSpot
            val loadedPredictions = favSpotsData.weeklyPredictionsForSpecificSpot
            if (loadedForecast.isNotEmpty() && loadedPredictions.isNotEmpty()) {
                showWeeklyForecastPopup = true
                isLoadingWeeklyForecast = false
            } else {
                isLoadingWeeklyForecast = true
                onEvent(FavSpotsEvent.LoadWeekPredictions(spot))
            }
        }
    }
    val blurModifier = if (isLoadingWeeklyForecast) Modifier.blur(16.dp) else Modifier



    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        val windowInfo = LocalWindowInfo.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .then(blurModifier)
        ) {
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
                                                    message = "Spot Deleted",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Long
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    onUndoDeleteSpot()
                                                }
                                            }
                                            val snackbarDurationMillis = 3000L // 3 seconds
                                            onEvent(
                                                FavSpotsEvent.DeleteSpot(
                                                    spot,
                                                    snackbarDurationMillis
                                                )
                                            )
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
                                        val direction =
                                            dismissState.dismissDirection
                                                ?: return@SwipeToDismissBox
                                        val color by animateColorAsState(
                                            when (dismissState.targetValue) {
                                                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                                else -> Color.Transparent
                                            }, label = "dismissColor"
                                        )
                                        val icon = Icons.Filled.Delete
                                        val scale by animateFloatAsState(
                                            targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                                            animationSpec = tween(durationMillis = 200),
                                            label = "dismissIconScale"
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
                                                        selectedSpotForWeeklyForecast = spot
                                                        isLoadingWeeklyForecast = true
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
                                                }
                                            }
                                            val snackbarDurationMillis = 4000L // 4 seconds
                                            onEvent(
                                                FavSpotsEvent.DeleteSpot(
                                                    spot,
                                                    snackbarDurationMillis
                                                )
                                            )
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
                                        val direction =
                                            dismissState.dismissDirection
                                                ?: return@SwipeToDismissBox
                                        val color by animateColorAsState(
                                            when (dismissState.targetValue) {
                                                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                                else -> Color.Transparent
                                            }, label = "dismissColor"
                                        )
                                        val icon = Icons.Filled.Delete
                                        val scale by animateFloatAsState(
                                            targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                                            animationSpec = tween(durationMillis = 200),
                                            label = "dismissIconScale"
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
                                                        selectedSpotForWeeklyForecast = spot
                                                        isLoadingWeeklyForecast = true
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
        }
        FloatingActionButton(
            onClick = { onAddSpotClick() },
            modifier = Modifier
                .padding(16.dp)
                .size(60.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Surfboard",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        if (isLoadingWeeklyForecast) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(
                    isLoading = true,
                    animationFileName = "quiver_sync_loading_animation.json",
                    animationSize = 240.dp
                )
            }
        } else if (showWeeklyForecastPopup) {
            favSpotsData.weeklyForecastForSpecificSpot.let { weeklyForecast ->
                val predictionsForSpot = favSpotsData.weeklyPredictionsForSpecificSpot
                val currentSpotData = favSpotsData.copy(
                    weeklyForecastForSpecificSpot = weeklyForecast,
                    weeklyPredictionsForSpecificSpot = predictionsForSpot
                )
                WeeklyForecastPopup(
                    data = currentSpotData,
                    onClose = {
                        showWeeklyForecastPopup = false
                        selectedSpotForWeeklyForecast = null
                    },
                    isImperial = isImperial
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "FavoriteSpots Light", showBackground = true)
@Composable
fun FavoriteSpotsScreenLightPreview() {
    FavoriteSpotsScreen(
        snackbarHostState = SnackbarHostState(),
        onAddSpotClick = {}
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "FavoriteSpots Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FavoriteSpotsScreenDarkPreview() {
    FavoriteSpotsScreen(
        snackbarHostState = SnackbarHostState(),
        onAddSpotClick = {}
    )
}
