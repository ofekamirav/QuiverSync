package org.example.quiversync.features.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.remote.dto.BoardMatchUi
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.FinsSetup
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardType
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.GeminiPrediction

class HomeViewModel(
    private val homeUseCases: HomeUseCases,
): BaseViewModel() {
    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> get() = _uiState


    init {
        fetchForecastAndBoardMatch()
    }

    private fun fetchForecastAndBoardMatch() {
        scope.launch {
            _uiState.value = HomeState.Loading

            val forecastResult = homeUseCases.getWeeklyForecastByLocationUseCase()
            val forecast : List<DailyForecast>
            val userResult = homeUseCases.getUser()
            val user : User
            when (userResult){
                is Result.Success ->{
                    if (userResult.data != null){
                        user = userResult.data
                    }else{
                        _uiState.value = HomeState.Error("User not found")
                        return@launch
                    }
                }

                is Result.Failure<*> -> {
                    _uiState.value = HomeState.Error("User not found")
                    return@launch
                }
            }

            when (forecastResult) {
                is Result.Success -> {
                    forecast = forecastResult.data ?: emptyList<DailyForecast>()
                }
                is Result.Failure -> {
                    _uiState.value = HomeState.Error(forecastResult.error?.message ?: "Unknown error")
                    return@launch
                }
            }
            val quiverResult = homeUseCases.getQuiverUseCase()
            var quiver  = emptyList<Surfboard>()
            when(quiverResult){
                is Result.Failure -> {
                }

                is Result.Success->{
                    if (quiverResult.data != null ){
                        quiver = quiverResult.data
                    }
                }
            }
            if (quiver.isEmpty()) {
                _uiState.value = HomeState.Loaded(
                    HomePageData(
                        weeklyForecast = forecast,
                        predictionForToday = GeminiPrediction()
                        , surfboard = Surfboard(
                            id = "default",
                            ownerId = "default",
                            model = "Default Model",
                            company = "Default Company",
                            type = SurfboardType.SHORTBOARD,
                            height = "6'0\"",
                            width = "18.5\"",
                            volume = "30L",
                            finSetup = FinsSetup.THRUSTER,
                            imageRes = "default_image.png",
                            addedDate = "2023-01-01",
                            latitude = 0.0,
                            longitude = 0.0,
                            isRentalPublished = false,
                            isRentalAvailable = false,
                            pricePerDay = 0.0
                        )
                    )
                )
                return@launch
            }
            println( "Quiver size: ${quiver.size}, Forecast size: ${forecast.size} this is the user profile " +
            " ${user.name} with id ${user.uid} and email ${user.email}")
            val bestBoardMatch = forecast.firstOrNull()
                ?.let { homeUseCases.getDailyPrediction(quiver , it , user) }
                ?: return@launch


            when (bestBoardMatch) {
                is Result.Failure -> {
                    _uiState.value = HomeState.Error(bestBoardMatch.error?.message ?: "Failed to retrieve best board match")
                    return@launch
                }
                is Result.Success -> {
                    val prediction = bestBoardMatch.data ?: return@launch
                    val surfboard = quiver.find { it.id ==  prediction.surfboardID }
                    if (surfboard == null) {
                        _uiState.value = HomeState.Error("Surfboard not found for the best match")
                        return@launch
                    }
                    _uiState.value = HomeState.Loaded(
                        HomePageData(
                            weeklyForecast = forecast,
                            predictionForToday = prediction,
                            surfboard = surfboard
                        )
                    )
                }
            }
        }
    }
}
