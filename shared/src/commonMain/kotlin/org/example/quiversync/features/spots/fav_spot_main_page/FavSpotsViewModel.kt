package org.example.quiversync.features.spots.fav_spot_main_page

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.DailyPrediction
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.spots.FavSpotsUseCases
import org.example.quiversync.utils.extensions.platformLogger
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.example.quiversync.data.session.SessionManager


class FavSpotsViewModel(
    private val favSpotsUseCases: FavSpotsUseCases,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<FavSpotsState>(FavSpotsState.Loading)
    val uiState: StateFlow<FavSpotsState> get() = _uiState

    private val _isImperialUnits = MutableStateFlow(false)
    val isImperialUnits: StateFlow<Boolean> get() = _isImperialUnits

    private var lastDismissedSpot: FavoriteSpot? = null
    private var deleteJob: Job? = null //

    /**
     * Initializes the ViewModel by deleting outdated forecasts and fetching essential data
     * to populate the initial UI state.
     */
    init {
        scope.launch {
            favSpotsUseCases.deleteOutDateForecastUseCase()
            sessionManager.getUnitsPreferenceFlow()
                .map { it == "imperial" }
                .collect { isImperial ->
                    _isImperialUnits.value = isImperial
                    platformLogger("FavSpotsViewModel", "isImperialUnits updated: $isImperial")
                }
        }

        observeFavoriteSpots()
    }

    fun refreshFavSpots() {
        scope.launch {
            _uiState.value = FavSpotsState.Loading
            observeFavoriteSpots()
        }
    }

    private fun observeFavoriteSpots() {
        scope.launch {
            favSpotsUseCases
                .getAllFavUserSpotsUseCase()
                .filterIsInstance<Result.Success<List<FavoriteSpot>>>()
                .map { it.data.orEmpty() }
                .distinctUntilChanged()
                .collect { spots ->
                    platformLogger("FavSpotsViewModel", "observeFavoriteSpots ▶️ spots.size=${spots.size}")
                    processSpotsAndFetchDependencies(spots)
                }
        }
    }

    private suspend fun processSpotsAndFetchDependencies(spots: List<FavoriteSpot>) {
        platformLogger("FavSpotsViewModel", "processSpotsAndFetchDependencies ▶️ got ${spots.size} spots")
        if (spots.isEmpty()) {
            _uiState.value = FavSpotsState.Loaded(
                FavSpotsData(
                    spots = emptyList(),
                    allSpotsDailyPredictions = emptyList(),
                    boards = emptyList(),
                    currentForecastsForAllSpots = emptyList(),
                    weeklyForecastForSpecificSpot = emptyList(),
                    weeklyPredictionsForSpecificSpot = emptyList(),
                    weeklyUiPredictions = emptyList()
                )
            )
            return
        }

        val userDetails =
            (favSpotsUseCases.getUserProfileUseCase().firstOrNull() as? Result.Success)?.data
        val quiver =
            (favSpotsUseCases.getAllQuiverUseCase().firstOrNull() as? Result.Success)?.data ?: emptyList()

        if (userDetails == null) {
            _uiState.value = FavSpotsState.Error("User details not found. Please log in.")
            return
        }


        val forecasts = spots.mapNotNull { spot ->
            (favSpotsUseCases.getDailyForecast(spot) as? Result.Success)?.data
        }

        if(quiver.isEmpty()){
            _uiState.value = FavSpotsState.Loaded(
                FavSpotsData(
                    spots = spots,
                    allSpotsDailyPredictions = emptyList(),
                    boards = quiver,
                    currentForecastsForAllSpots = forecasts,
                    weeklyForecastForSpecificSpot = emptyList(),
                    weeklyPredictionsForSpecificSpot = emptyList(),
                    weeklyUiPredictions = emptyList()
                )
            )
            return
        }
//        platformLogger("FavSpotsViewModel", "Forecasts loaded: ${forecasts.size}")

        val predictionsResult = favSpotsUseCases.generateAllTodayPredictionsUseCase(
            user = userDetails,
            surfboards = quiver,
            forecasts = forecasts
        )
        when (predictionsResult) {
            is Result.Success -> {
                val preds = predictionsResult.data.orEmpty()
                platformLogger("FavSpotsViewModel", "generateAllTodayPredictionsUseCase returned ${preds.size} predictions")
                _uiState.value = FavSpotsState.Loaded(
                    FavSpotsData(
                        spots = spots,
                        allSpotsDailyPredictions = preds,
                        boards = quiver,
                        currentForecastsForAllSpots = forecasts,
                        weeklyForecastForSpecificSpot = emptyList(),
                        weeklyPredictionsForSpecificSpot = emptyList(),
                        weeklyUiPredictions = emptyList()
                    )
                )
            }
            is Result.Failure -> {
                platformLogger("FavSpotsViewModel", "generateAllTodayPredictionsUseCase failed: ${predictionsResult.error?.message}")
                _uiState.value = FavSpotsState.Error(predictionsResult.error?.message ?: "Unknown error")
            }
        }
    }

    /**
     * Handles user interaction events by delegating to appropriate handler methods.
     * @param event The UI event to process
     */
    fun onEvent(event: FavSpotsEvent) {
        when (event) {
            is FavSpotsEvent.DeleteSpot -> {
                deleteSpot(event.favoriteSpot, event.snackBarDurationMillis)
            }

            is FavSpotsEvent.LoadWeekPredictions -> {
                weeklyPredictions(event.favoriteSpot)
            }

            is FavSpotsEvent.ErrorOccurred -> {
                scope.launch {
                    _uiState.emit(FavSpotsState.Error(event.message))
                }
            }

            is FavSpotsEvent.UndoDeleteSpot -> {
                undoDeleteSpot()
            }
        }
    }

    /**
     * Removes a favorite spot from storage and refreshes UI data if successful.
     * Updates UI state with error message if deletion fails.
     * @param favoriteSpot The spot to delete
     */
    private fun deleteSpot(favoriteSpot: FavoriteSpot, snackBarDurationMillis: Long) {
        lastDismissedSpot = favoriteSpot
        val currentLoadedState = _uiState.value as? FavSpotsState.Loaded ?: return
        val currentData = currentLoadedState.favSpotsData

        val updatedSpots = currentData.spots.filter { it.spotID != favoriteSpot.spotID }
        val updatedPredictions = currentData.allSpotsDailyPredictions.filter {
            it.forecastLatitude != favoriteSpot.spotLatitude || it.forecastLongitude != favoriteSpot.spotLongitude
        }
        val updatedForecasts = currentData.currentForecastsForAllSpots.filter {
            it.latitude != favoriteSpot.spotLatitude || it.longitude != favoriteSpot.spotLongitude
        }

        _uiState.update {
            FavSpotsState.Loaded(
                currentData.copy(
                    spots = updatedSpots,
                    allSpotsDailyPredictions = updatedPredictions,
                    currentForecastsForAllSpots = updatedForecasts
                )
            )
        }

        deleteJob?.cancel()
        deleteJob?.cancel()
        deleteJob = scope.launch {
            delay(snackBarDurationMillis)
            if (lastDismissedSpot == favoriteSpot) {
                performPermanentDelete(favoriteSpot)
            }
            lastDismissedSpot = null
            deleteJob = null
        }
    }

    private fun performPermanentDelete(favoriteSpot: FavoriteSpot) {
        scope.launch {
            favSpotsUseCases.deleteAllPredictionsBySpotUseCase(
                favoriteSpot.spotLatitude,
                favoriteSpot.spotLongitude
            )
            favSpotsUseCases.deleteBySpotUseCase(
                favoriteSpot.spotLatitude,
                favoriteSpot.spotLongitude
            )

            favSpotsUseCases.removeFavSpotsUseCase(favoriteSpot)
            platformLogger(
                "FavSpotsViewModel",
                "Spot deleted: ${favoriteSpot.name} (${favoriteSpot.spotLatitude}, ${favoriteSpot.spotLongitude})"
            )
        }
    }

    /**
     * Fetches weekly forecasts and board predictions for a specific spot.
     * Updates only the weekly forecast and prediction fields in the UI state,
     * preserving other data.
     *
     * @param spot The favorite spot to get weekly predictions for
     */
    private fun weeklyPredictions(spot: FavoriteSpot) {
        scope.launch {

            val currentState = _uiState.value
            if (currentState !is FavSpotsState.Loaded) {
                _uiState.emit(FavSpotsState.Error("Cannot load weekly predictions: data not loaded yet"))
                return@launch
            }
            val favSpotDataWeekly = currentState.favSpotsData.weeklyForecastForSpecificSpot
            if (favSpotDataWeekly.isNotEmpty()) {
                if (favSpotDataWeekly.first().latitude == spot.spotLatitude &&
                    favSpotDataWeekly.first().longitude == spot.spotLongitude
                ) {
//                    println("FavSpotsViewModel: Weekly forecast already loaded for this spot: ${spot.name}")
                    // Already loaded for this spot, no need to fetch again
                    _uiState.emit(FavSpotsState.Loaded(currentState.favSpotsData))
                    return@launch
                }
            } else {
//                print("FavSpotsViewModel: No weekly forecast data available for this spot, fetching new data")
                updateUiState {
                    it.copy(
                        weeklyForecastForSpecificSpot = emptyList(),
                        weeklyPredictionsForSpecificSpot = emptyList(),
                        weeklyUiPredictions = emptyList()
                    )
                }
            }


            val finalWeeklyPrediction = mutableListOf<DailyPrediction>()

            val weeklyForecastResult = favSpotsUseCases.getWeeklyForecastBySpotUseCase(spot, false)
            when (weeklyForecastResult) {
                is Result.Failure -> {
                    _uiState.emit(
                        FavSpotsState.Error(
                            weeklyForecastResult.error?.message ?: "Failed to fetch weekly forecast"
                        )
                    )
                }

                is Result.Success -> {
                    val weeklyForecast = weeklyForecastResult.data ?: emptyList()
                    if (weeklyForecast.isEmpty()) {
                        _uiState.emit(FavSpotsState.Error("No weekly forecast found for this spot."))
                    } else {
                        // Get predictions for each forecast day
                        val quiverResult = favSpotsUseCases.getAllQuiverUseCase().firstOrNull()

                        if (quiverResult is Result.Success) {
                            val quiver = quiverResult.data ?: emptyList()
                            var weeklyPredictions = emptyList<GeminiPrediction>()
                            val predictions =
                                favSpotsUseCases.generateWeeklyPredictions(quiver, weeklyForecast)

                            when (predictions) {
                                is Result.Failure -> {
                                    _uiState.emit(
                                        FavSpotsState.Error(
                                            predictions.error?.message
                                                ?: "Failed to fetch weekly predictions"
                                        )
                                    )
                                }

                                is Result.Success -> {
                                    if (predictions.data == null)
                                        _uiState.emit(FavSpotsState.Error("No predictions found for this spot."))
                                    else {
                                        weeklyPredictions = predictions.data

                                        weeklyPredictions.forEach { prediction ->
                                            val bestBoard =
                                                prediction.surfboardID.let { surfboardId ->
                                                    quiver.find { it.id == surfboardId }
                                                }
                                            val dailyForecast =
                                                prediction.forecastLongitude.let { lng ->
                                                    prediction.forecastLatitude.let { lat ->
                                                        prediction.date.let { date ->
                                                            weeklyForecast.find { it.latitude == lat && it.longitude == lng && it.date == date }
                                                        }
                                                    }
                                                }
                                            if (bestBoard != null && dailyForecast != null) {
                                                finalWeeklyPrediction.add(
                                                    DailyPrediction(
                                                        prediction = prediction,
                                                        surfboard = bestBoard,
                                                        dailyForecast = dailyForecast
                                                    )
                                                )
                                            } else {
//                                                println(
//                                                    "FavSpotsViewModel: Skipping prediction for ${prediction.forecastLatitude}, ${prediction.forecastLongitude} - " +
//                                                            "Board or forecast not found"
//                                                )
                                            }

                                        }


                                    }
                                }

                            }


                            updateUiState {
                                it.copy(
                                    weeklyForecastForSpecificSpot = weeklyForecast,
                                    weeklyPredictionsForSpecificSpot = weeklyPredictions,
                                    weeklyUiPredictions = finalWeeklyPrediction
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper method to safely update UI state while preserving existing data.
     * Only applies updates if the current state is Loaded, otherwise falls back to Loading.
     *
     * @param update Lambda function that transforms the current state to a new state
     */
    private fun updateUiState(update: (FavSpotsData) -> FavSpotsData) {
        val currentState = _uiState.value
        if (currentState is FavSpotsState.Loaded) {
            _uiState.update { FavSpotsState.Loaded(update(currentState.favSpotsData)) }
        }
    }


    private fun undoDeleteSpot() {
        deleteJob?.cancel()
        lastDismissedSpot?.let { spotToRestore ->
            scope.launch {
                val currentState = _uiState.value as? FavSpotsState.Loaded ?: return@launch
                processSpotsAndFetchDependencies(currentState.favSpotsData.spots + spotToRestore)
            }
        }
    }

}