package org.example.quiversync.features.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.FinsSetup
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardType
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.features.home.HomeState.*
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.data.local.Result


class HomeViewModel(
    private val homeUseCases: HomeUseCases,
    private val sessionManager: SessionManager,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> get() = _uiState

    private val _isImperial = MutableStateFlow<Boolean>(false)
    val isImperial: StateFlow<Boolean> get() = _isImperial

    private val refreshTrigger = MutableStateFlow(0)

    private val _hasLocationPermission = MutableStateFlow(false)

    init {
        scope.launch {
            sessionManager.getUnitsPreferenceFlow()
                .map { it == "imperial" }
                .collect { isImperial ->
                    _isImperial.value = isImperial
                }
        }

        scope.launch {
            combine(
                homeUseCases.getQuiverUseCase(),
                homeUseCases.getUser(),
                _hasLocationPermission,
                refreshTrigger
            ) { quiverResult, userResult, hasPermission, _ ->
                if (!hasPermission) {
                    platformLogger("HomeViewModel", "Permissions not granted yet. Waiting.")
                    return@combine HomeState.Loading
                }
                platformLogger("HomeViewModel", "Data stream triggered. Fetching all data.")
                processData(quiverResult, userResult)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onPermissionResult(isGranted: Boolean) {
        _hasLocationPermission.value = isGranted
        if (!isGranted) {
            _uiState.value = HomeState.Error("Location permission is required to show weather forecast.")
        }
    }

    private suspend fun processData(
        quiverResult: Result<List<Surfboard>, Error>,
        userResult: Result<User, Error>
    ): HomeState {
        val user = (userResult as? Result.Success)?.data
        if (user == null) {
            platformLogger("HomeViewModel", ">>> [DEBUG] 🛑 FAILURE: User is null. Result was: $userResult")
            return HomeState.Error("User not authenticated.")
        }
        platformLogger("HomeViewModel", ">>> [DEBUG] ✅ SUCCESS: User loaded: ${user.uid}")

        val quiver = (quiverResult as? Result.Success)?.data ?: emptyList()
        platformLogger("HomeViewModel", ">>> [DEBUG] 📊 Quiver status: Contains ${quiver.size} surfboard(s).")
        if (quiver.isNotEmpty()) {
            quiver.forEach { platformLogger("HomeViewModel", ">>> [DEBUG]    - Board: ${it.model} (ID: ${it.id})") }
        }

        val forecastResult = homeUseCases.getWeeklyForecastByLocationUseCase()
        val forecast = (forecastResult as? Result.Success)?.data
        if (forecast.isNullOrEmpty()) {
            platformLogger("HomeViewModel", ">>> [DEBUG] 🛑 FAILURE: Forecast is null or empty. Result was: $forecastResult")
            return HomeState.Error("Could not fetch forecast data for your location.")
        }
        platformLogger("HomeViewModel", ">>> [DEBUG] ✅ SUCCESS: Forecast loaded with ${forecast.size} days. Today's wave height: ${forecast.first().waveHeight}")


        if (quiver.isEmpty()) {
            platformLogger("HomeViewModel", ">>> [DEBUG] 🚪 EXIT: Quiver is empty. Skipping prediction.")
            return HomeState.Loaded(
                HomePageData(weeklyForecast = forecast, predictionForToday = null, surfboard = null)
            )
        }

        platformLogger("HomeViewModel", ">>> [DEBUG] 🧠 Calling getDailyPrediction with ${quiver.size} boards and today's forecast...")
        val predictionResult = homeUseCases.getDailyPrediction(quiver, forecast.first(), user)

        return when (predictionResult) {
            is Result.Success -> {
                val prediction = predictionResult.data
                if (prediction == null) {
                    platformLogger("HomeViewModel", ">>> [DEBUG] ⚠️ WARNING: Prediction result was Success, but data is NULL.")
                    return HomeState.Loaded(HomePageData(weeklyForecast = forecast, predictionForToday = null, surfboard = null))
                }

                platformLogger("HomeViewModel", ">>> [DEBUG] ✅ SUCCESS: Prediction received. BoardID: ${prediction.surfboardID}, Score: ${prediction.score}")

                val surfboard = quiver.find { it.id == prediction.surfboardID }
                if (surfboard == null) {
                    platformLogger("HomeViewModel", ">>> [DEBUG] 🛑 FAILURE: Mismatch! Predicted surfboard ID '${prediction.surfboardID}' not found in quiver.")
                    return HomeState.Loaded(HomePageData(weeklyForecast = forecast, predictionForToday = prediction, surfboard = null))
                }

                platformLogger("HomeViewModel", ">>> [DEBUG] ✅ FINAL SUCCESS: Found matching surfboard '${surfboard.model}'. Updating UI.")
                HomeState.Loaded(
                    HomePageData(
                        weeklyForecast = forecast,
                        predictionForToday = prediction,
                        surfboard = surfboard
                    )
                )
            }
            is Result.Failure -> {
                platformLogger("HomeViewModel", ">>> [DEBUG] 🛑 FAILURE: getDailyPrediction returned a failure: ${predictionResult.error?.message}")
                HomeState.Loaded(HomePageData(weeklyForecast = forecast, predictionForToday = null, surfboard = null))
            }
        }
    }

    fun refresh() {
        platformLogger("HomeViewModel", "Manual refresh triggered by UI.")
        refreshTrigger.value++
    }
}