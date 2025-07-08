package org.example.quiversync.features.spots.FavSpotMainPage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.features.BaseViewModel

class FavSpotsViewModel(
    private val favSpotsUseCases: FavSpotsUseCases,

    ) : BaseViewModel() {

    private val _uiState = MutableStateFlow<FavSpotsState>(FavSpotsState.Loading)
    val uiState: StateFlow<FavSpotsState> get() = _uiState

    init {
        scope.launch {
            favSpotsUseCases.deleteOutDateForecastUseCase()
        }
        fetchEssentialData()
    }

    fun onEvent(event: FavSpotsEvent) {
        when (event) {
            is FavSpotsEvent.DeleteSpot -> {
                deleteSpot(event.favoriteSpot)
            }
        }
    }

    private fun del.eteSpot(favoriteSpot:FavoriteSpot) {
        scope.launch {
            val result = favSpotsUseCases.removeFavSpotsUseCase(favSpot = favoriteSpot)
            if (result is Result.Success) {
                fetchEssentialData()
            } else if (result is Result.Failure) {
                _uiState.value =
                    FavSpotsState.Error(result.error?.message ?: "Failed to delete spot")
            }
        }
    }

    private fun fetchEssentialData() {
        scope.launch {
            var spots : List<FavoriteSpot> = emptyList()
            var prediction : List<GeminiPrediction> = emptyList()
            when (val favSpotsResult = favSpotsUseCases.getAllFavUserSpotsUseCase()) {
                is Result.Success -> {
                    val favSpots = favSpotsResult.data ?: emptyList()
                    if (favSpots.isEmpty()) {
                        _uiState.value = FavSpotsState.Error(
                            "No favorite spots found. Please add some spots to your favorites."
                        )
                    } else {
                        spots = favSpots
                    }
                }
                is Result.Failure -> {
                    _uiState.emit(FavSpotsState.Error(
                        favSpotsResult.error?.message ?: "Failed to fetch favorite spots"
                    ))
                }
            }

            val quiverResult = favSpotsUseCases.getAllQuiverUseCase()
            val userDetails = favSpotsUseCases.getUserProfileUseCase()
            if(quiverResult is Result.Success && userDetails is Result.Success) {
                val quiver = quiverResult.data ?: emptyList()
                val user = userDetails.data
                for (spot in spots){
                    var dailyForecast: DailyForecast = DailyForecast()
                    val dailyForecastResult = favSpotsUseCases.getDailyForecast(
                        spot = spot
                    )
                    if(dailyForecastResult is Result.Success){
                        dailyForecast = dailyForecastResult.data ?: DailyForecast()
                    }else{
                        val weeklyForecastResult = favSpotsUseCases.getWeeklyForecastBySpotUseCase(spot)
                        when(weeklyForecastResult){
                            is Result.Failure -> {
                                _uiState.emit(FavSpotsState.Error(
                                    weeklyForecastResult.error?.message ?: "Failed to fetch weekly forecast"
                                ))
                            }
                            is Result.Success -> {
                                val weeklyForecast = weeklyForecastResult.data ?: emptyList()
                                if (weeklyForecast.isNotEmpty()) {
                                    dailyForecast = weeklyForecast.firstOrNull() ?: DailyForecast()
                                }
                            }
                        }
                    }
                    val predictionResult = user?.let {
                        favSpotsUseCases.getBestBoardForSingleDayUseCase(
                            surfboards = quiver,
                            dailyForecast = dailyForecast,
                            user = it
                        )
                    }
                    when(predictionResult){
                        is Result.Failure -> {
                            _uiState.emit(FavSpotsState.Error(
                                predictionResult.error?.message ?: "Failed to fetch prediction"
                            ))
                        }

                        is Result.Success -> {
                            if (predictionResult.data != null) {
                                prediction = prediction + predictionResult.data
                            }
                        }

                        null -> {
                            _uiState.emit(FavSpotsState.Error(
                                "User details not found. Please ensure you are logged in."
                            ))
                        }
                    }
                }

                _uiState.emit(FavSpotsState.Loaded(FavSpotsData(spots,prediction)))
            }

        }
    }

}

