package org.example.quiversync.features.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.FinsSetup
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardType
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.features.home.HomeState.*
import org.example.quiversync.utils.extensions.platformLogger

class HomeViewModel(
    private val homeUseCases: HomeUseCases,
): BaseViewModel() {
    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> get() = _uiState

    private val _isImperial =  MutableStateFlow<Boolean>(false)
    val isImperial: StateFlow<Boolean> get() = _isImperial

    init {
        fetchForecastAndBoardMatch()
        scope.launch {
         if(homeUseCases.isImperialUnitsUseCases()){
            _isImperial.value = true
         }
            else {
                _isImperial.value = false
            }
        }
    }

    fun refreshWithLocation() {
        fetchForecastAndBoardMatch()
    }


    private fun fetchForecastAndBoardMatch() {
        scope.launch {
            platformLogger("HomeViewModel", "fetching forecast and best board match")
            _uiState.value = HomeState.Loading
            platformLogger("HomeViewModel", "Fetching weekly forecast and best board match")
            val forecastResult = homeUseCases.getWeeklyForecastByLocationUseCase()
            platformLogger("HomeViewModel", "forecast for the week by your location: $forecastResult")
            val userResult = homeUseCases.getUser().firstOrNull()
            val forecast: List<DailyForecast>
            val user : User
            when (userResult){
                is Result.Success ->{
                    if (userResult.data != null){
                        user = userResult.data
                    }else{
                        _uiState.value = Error("User not found")
                        return@launch
                    }
                }

                is Result.Failure -> {
                    _uiState.value = Error("User not found")
                    return@launch
                }

                null -> {
                    _uiState.value = Error("User not found")
                    return@launch
                }
            }

            when (forecastResult) {
                is Result.Success -> {
                    forecast = forecastResult.data ?: emptyList<DailyForecast>()
                    if (forecastResult.data == null) {
                        _uiState.value = HomeState.Error("No forecast data available")
                        return@launch
                    }

                }
                is Result.Failure -> {
                    _uiState.value = HomeState.Error(forecastResult.error?.message ?: "Unknown error")
                    return@launch
                }
            }
            val quiverResult = homeUseCases.getQuiverUseCase().firstOrNull()
            platformLogger("HomeViewModel", "quiver result: $quiverResult")
            var quiver  = emptyList<Surfboard>()
            when(quiverResult){
                is Result.Failure -> {
                }

                is Result.Success->{
                    if (quiverResult.data != null ){
                        quiver = quiverResult.data
                    }
                }
                else -> {
                    platformLogger("HomeViewModel", "Quiver result is null or empty")
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
            platformLogger("HomeViewModel", "best board match: $bestBoardMatch")


            when (bestBoardMatch) {
                is Result.Failure -> {
                    platformLogger("HomeViewModel", "Error fetching best board match: ${bestBoardMatch.error?.message}")
                    _uiState.value = HomeState.Error(bestBoardMatch.error?.message ?: "Failed to retrieve best board match")
                    return@launch
                }
                is Result.Success -> {
                    val prediction = bestBoardMatch.data ?: return@launch
                    val surfboard = quiver.find { it.id ==  prediction.surfboardID }
                    if (surfboard == null) {
                        platformLogger("HomeViewModel", "Surfboard not found for the best match: ${prediction.surfboardID}")
                        _uiState.value = HomeState.Error("Surfboard not found for the best match")
                        return@launch
                    }
                    platformLogger("HomeViewModel", "Best board match: ${prediction.surfboardID} with score ${prediction.score}")
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