package org.example.quiversync.features.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.remote.dto.BoardMatchUi
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.forecast.DailyForecast

class HomeViewModel(
    private val homeUseCases: HomeUseCases,
): BaseViewModel() {
    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> get() = _uiState


    init {
        fetchForecastAndBoardMatch()
    }

    fun fetchForecastAndBoardMatch() {
        scope.launch {
            _uiState.value = HomeState.Loading

            val forecastResult = homeUseCases.getWeeklyForecastByLocationUseCase()
            val forecast : List<DailyForecast>
            when (forecastResult) {
                is Result.Success -> {
                    forecast = forecastResult.data ?: emptyList<DailyForecast>()
                }
                is Result.Failure -> {
                    _uiState.value = HomeState.Error(forecastResult.error?.message ?: "Unknown error")
                    return@launch
                }
            }

            val bestBoardMatch = BoardMatchUi(
                surfboardId = "best_board_match",
                brand = "Best Brand",
                model = "Best Model",
                imageUrl = "https://example.com/best_board_image.jpg",
                score = 95
            )
//                homeUseCases.getBestBoardMatchUseCase(
//                date = forecast.list.first().date,
//                latitude = forecast.list.first().latitude,
//                longitude = forecast.list.first().longitude
//            ).getOrNull()

            _uiState.value = HomeState.Loaded(
                forecast = forecast,
                todayMatchBoard = bestBoardMatch
            )
        }
    }
}
