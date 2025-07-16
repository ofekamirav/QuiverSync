package org.example.quiversync.features.spots.fav_spot_main_page

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.DailyPrediction
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.spots.FavSpotsUseCases
import org.example.quiversync.features.spots.SpotAddedEvent
import org.example.quiversync.features.spots.SpotEventBus


class FavSpotsViewModel(
    private val favSpotsUseCases: FavSpotsUseCases,
    private val spotsEventBus : SpotEventBus
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
            _isImperialUnits.value = favSpotsUseCases.isImperialUnitsUseCase()
        }
        fetchEssentialData()
        scope.launch {
            spotsEventBus.events.collect{ event ->
                when(event) {
                    SpotAddedEvent -> {
                        println("FavSpotsViewModel: Spot added event received, refetching essential data.")
                        fetchEssentialData()
                    }
                }
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
                deleteSpot(event.favoriteSpot, event.snackbarDurationMillis)
            }
            is FavSpotsEvent.LoadWeekPredictions -> {
                weeklyPredictions(event.favoriteSpot)
            }
            is FavSpotsEvent.ErrorOccurred -> {
                scope.launch{
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
    private fun deleteSpot(favoriteSpot:FavoriteSpot,snackbarDurationMillis: Long) {
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
            delay(snackbarDurationMillis)
            if (lastDismissedSpot == favoriteSpot) {
                performPermanentDelete(favoriteSpot)
            }
            lastDismissedSpot = null
            deleteJob = null
        }
    }
    private fun performPermanentDelete(favoriteSpot: FavoriteSpot) {
        scope.launch {
            println("FavSpotsViewModel: Deleting Predictions (${favoriteSpot.spotLatitude}, ${favoriteSpot.spotLongitude})")
            val resPredictionDelete = favSpotsUseCases.deleteAllPredictionsBySpotUseCase(
                latitude = favoriteSpot.spotLatitude,
                longitude = favoriteSpot.spotLongitude
            )
            if (resPredictionDelete is Result.Failure) {
                _uiState.value = FavSpotsState.Error(
                    resPredictionDelete.error?.message ?: "Failed to delete predictions for spot"
                )
                return@launch
            }
            println( "FavSpotsViewModel: Deleting forecast for spot (${favoriteSpot.spotLatitude}, ${favoriteSpot.spotLongitude})")
            val resForecastDelete = favSpotsUseCases.deleteBySpotUseCase(
                latitude = favoriteSpot.spotLatitude,
                longitude = favoriteSpot.spotLongitude
            )
            if (resForecastDelete is Result.Failure) {
                _uiState.value = FavSpotsState.Error(
                    resForecastDelete.error?.message ?: "Failed to delete forecast for spot"
                )
                return@launch
            }
            println("FavSpotsViewModel: Deleting favorite spot ${favoriteSpot.name}")
            val result = favSpotsUseCases.removeFavSpotsUseCase(favSpot = favoriteSpot)
            if (result is Result.Success) {
                fetchEssentialData()
                lastDismissedSpot = null
            } else if (result is Result.Failure) {
                _uiState.value =
                    FavSpotsState.Error(result.error?.message ?: "Failed to delete spot")
            }
            fetchEssentialData()
        }
    }

    /**
     * Fetches all data needed for the UI:
     * 1. User's favorite spots
     * 2. Current forecast for each spot
     * 3. Board predictions for each forecast
     * 4. User's surfboard collection
     *
     * Updates UI state to Loaded with all data or Error if retrieval fails.
     */
    private fun fetchEssentialData() {
        scope.launch {
            var spots : List<FavoriteSpot> = emptyList()
            var prediction : List<GeminiPrediction> = emptyList()
            var currentForecastsForAllSpots : List<DailyForecast> = emptyList()
            val weeklyForecastForSpecificSpot : List<DailyForecast> = emptyList()
            val weeklyPredictionsForSpecificSpot : List<GeminiPrediction> = emptyList()

            // ==================== Load favorite spots  ========================================
            when (val favSpotsResult = favSpotsUseCases.getAllFavUserSpotsUseCase()) {
                is Result.Success -> {
                    val favSpots = favSpotsResult.data ?: emptyList()
                    if (favSpots.isEmpty()) {
                        println("FavSpotsViewModel: No favorite spots found, initializing empty state")
                        _uiState.emit(FavSpotsState.Loaded(
                            favSpotsData = (
                                    FavSpotsData(
                                        spots= spots,
                                        allSpotsDailyPredictions = prediction,
                                        boards = emptyList(),
                                        currentForecastsForAllSpots = currentForecastsForAllSpots,
                                        weeklyForecastForSpecificSpot = weeklyForecastForSpecificSpot,
                                        weeklyPredictionsForSpecificSpot = weeklyPredictionsForSpecificSpot,
                                        weeklyUiPredictions = emptyList()
                                    )
                                    )
                        ))
                        return@launch
                    } else {
                        favSpots.forEach { spot ->
                            println(" - Spot Name: ${spot.name}, Latitude: ${spot.spotLatitude}, Longitude: ${spot.spotLongitude}, User ID: ${spot.userID}")
                        }
                        spots = favSpots
                        println("FavSpotsViewModel: Favorite spots loaded successfully: ${spots.size}")
                    }
                }
                is Result.Failure -> {
                    _uiState.value = FavSpotsState.Error(
                        favSpotsResult.error?.message ?: "Failed to fetch favorite spots"
                    )
                }
            }

            // ==================== Fetch the predictions  ==================================
            val quiverResult = favSpotsUseCases.getAllQuiverUseCase()
            val userDetails = favSpotsUseCases.getUserProfileUseCase()

            if(quiverResult is Result.Success && userDetails is Result.Success) {
                val quiver = quiverResult.data
                if (quiver == null)  {
                    _uiState.value = FavSpotsState.Error(
                        "No surfboards found in your quiver. Please add some boards."
                    )
                    return@launch
                }

                for (spot in spots){
                    println("FavSpotsViewModel: Processing spot: ${spot.name}")

                    // ==================== Fetch the current forecast for each spot  ==================================
                    var dailyForecast = DailyForecast()
                    val dailyForecastResult = favSpotsUseCases.getDailyForecast(
                        spot = spot
                    )
                    if(dailyForecastResult is Result.Success){
                        dailyForecast = dailyForecastResult.data ?: DailyForecast()
                    } else {
                        println("FavSpotsViewModel: Daily forecast not found, trying weekly forecast")
                        val weeklyForecastResult = favSpotsUseCases.getWeeklyForecastBySpotUseCase(spot , false)
                        when(weeklyForecastResult){
                            is Result.Failure -> {
                                _uiState.value = FavSpotsState.Error(
                                    weeklyForecastResult.error?.message ?: "Failed to fetch weekly forecast"
                                )
                            }
                            is Result.Success -> {
                                val weeklyForecast = weeklyForecastResult.data ?: emptyList()
                                if (weeklyForecast.isNotEmpty()) {
                                    dailyForecast = weeklyForecast.firstOrNull() ?: DailyForecast()
                                }
                            }
                        }
                    }
                    currentForecastsForAllSpots = currentForecastsForAllSpots + dailyForecast

                }

                // ==================== Generate predictions for each spot  ==================================
                if(userDetails.data == null || quiver.isEmpty() || currentForecastsForAllSpots.isEmpty()) {
                    println( "user details = ${userDetails.data}")
                    println( "quiver details = ${quiver.size}")
                    println( "currentForecastsForAllSpots details = ${currentForecastsForAllSpots.size}")


                    _uiState.value = FavSpotsState.Error("User details not found. Please log in.")
                    return@launch
                }
                val predictions = favSpotsUseCases.generateAllTodayPredictionsUseCase(
                    user = userDetails.data ,
                    surfboards = quiver,
                    forecasts = currentForecastsForAllSpots
                )

                when(predictions) {
                    is Result.Failure -> {
                        _uiState.value = FavSpotsState.Error(
                            predictions.error?.message ?: "Failed to fetch predictions"
                        )
                    }
                    is Result.Success -> {
                        prediction = predictions.data ?: emptyList()
                        if (prediction.isEmpty()) {
                            _uiState.value = FavSpotsState.Error("No predictions found for favorite spots.")
                        }
                    }
                }

                _uiState.value = FavSpotsState.Loaded(FavSpotsData(
                    spots,
                    prediction,
                    quiver,
                    currentForecastsForAllSpots,
                    weeklyForecastForSpecificSpot,
                    weeklyPredictionsForSpecificSpot,
                    weeklyUiPredictions = emptyList()
                ))
            }
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
            if(currentState !is FavSpotsState.Loaded) {
                _uiState.emit(FavSpotsState.Error("Cannot load weekly predictions: data not loaded yet"))
                return@launch
            }
            val favSpotDataWeekly = currentState.favSpotsData.weeklyForecastForSpecificSpot
            if(favSpotDataWeekly.isNotEmpty()){
                if (favSpotDataWeekly.first().latitude == spot.spotLatitude &&
                    favSpotDataWeekly.first().longitude == spot.spotLongitude) {
                    println("FavSpotsViewModel: Weekly forecast already loaded for this spot: ${spot.name}")
                    // Already loaded for this spot, no need to fetch again
                    _uiState.emit(FavSpotsState.Loaded(currentState.favSpotsData))
                    return@launch
                }
            }
            else{
                print("FavSpotsViewModel: No weekly forecast data available for this spot, fetching new data")
                updateUiState {
                    it.copy(
                        weeklyForecastForSpecificSpot = emptyList(),
                        weeklyPredictionsForSpecificSpot = emptyList(),
                        weeklyUiPredictions = emptyList()
                    )
                }
            }


            val finalWeeklyPrediction = mutableListOf<DailyPrediction>()

            val weeklyForecastResult = favSpotsUseCases.getWeeklyForecastBySpotUseCase(spot , false)
            when(weeklyForecastResult) {
                is Result.Failure -> {
                    _uiState.emit(FavSpotsState.Error(
                        weeklyForecastResult.error?.message ?: "Failed to fetch weekly forecast"
                    ))
                }
                is Result.Success -> {
                    val weeklyForecast = weeklyForecastResult.data ?: emptyList()
                    if (weeklyForecast.isEmpty()) {
                        _uiState.emit(FavSpotsState.Error("No weekly forecast found for this spot."))
                    } else {
                        // Get predictions for each forecast day
                        val quiverResult = favSpotsUseCases.getAllQuiverUseCase()

                        if (quiverResult is Result.Success) {
                            val quiver = quiverResult.data ?: emptyList()
                            var weeklyPredictions = emptyList<GeminiPrediction>()
                            val predictions =
                                favSpotsUseCases.generateWeeklyPredictions(quiver,weeklyForecast)

                            when(predictions) {
                                is Result.Failure -> {
                                    _uiState.emit(FavSpotsState.Error(
                                        predictions.error?.message ?: "Failed to fetch weekly predictions"
                                    ))
                                }
                                is Result.Success -> {
                                    if (predictions.data == null)
                                        _uiState.emit(FavSpotsState.Error("No predictions found for this spot."))
                                    else{
                                        weeklyPredictions = predictions.data

                                        weeklyPredictions.forEach { prediction ->
                                            val bestBoard = prediction.surfboardID.let { surfboardId ->
                                                quiver.find { it.id == surfboardId }
                                            }
                                            val dailyForecast = prediction.forecastLongitude.let { lng ->
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
                                                println("FavSpotsViewModel: Skipping prediction for ${prediction.forecastLatitude}, ${prediction.forecastLongitude} - " +
                                                        "Board or forecast not found")
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
    private fun updateUiState(update : (FavSpotsData) -> FavSpotsData) {
        val currentState = _uiState.value
        if (currentState is FavSpotsState.Loaded) {
            _uiState.update { FavSpotsState.Loaded(update(currentState.favSpotsData)) }
        }
    }


    private fun undoDeleteSpot() {
        lastDismissedSpot?.let { spotToRestore ->
            fetchEssentialData()
            lastDismissedSpot = null
        }
    }


}