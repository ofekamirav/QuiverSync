package org.example.quiversync.features.spots.fav_spot_main_page

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.DailyPrediction
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.spots.FavSpotsUseCases
import org.example.quiversync.utils.extensions.platformLogger

class FavSpotsViewModel(
    private val favSpotsUseCases: FavSpotsUseCases,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<FavSpotsState>(FavSpotsState.Loading)
    val uiState: StateFlow<FavSpotsState> get() = _uiState

    private val _isImperialUnits = MutableStateFlow(false)
    val isImperialUnits: StateFlow<Boolean> get() = _isImperialUnits

    private val refreshTrigger = MutableStateFlow(0)
    private var lastDismissedSpot: FavoriteSpot? = null
    private var deleteJob: Job? = null

    init {
        // משימות ראשוניות שרצות פעם אחת
        scope.launch {
            favSpotsUseCases.deleteOutDateForecastUseCase()
            sessionManager.getUnitsPreferenceFlow()
                .map { it == "imperial" }
                .collect { isImperial ->
                    _isImperialUnits.value = isImperial
                    platformLogger("FavSpotsViewModel", "isImperialUnits updated: $isImperial")
                }
        }

        // מתחילים להאזין לשינויים בנתונים
        observeSpotsAndDependencies()
    }

    private fun observeSpotsAndDependencies() {
        scope.launch {
            combine(
                favSpotsUseCases.getAllFavUserSpotsUseCase(),
                favSpotsUseCases.getAllQuiverUseCase(),
                favSpotsUseCases.getUserProfileUseCase(),
                refreshTrigger
            ) { spotsResult, quiverResult, userResult, _ ->
                platformLogger("FavSpotsViewModel", "Data stream triggered...")

                // ================== שלב 1: ולידציה של הנתונים ==================
                if (userResult is Result.Failure || (userResult as? Result.Success)?.data == null) {
                    return@combine FavSpotsState.Error("User profile not available. Please log in again.")
                }
                if (spotsResult is Result.Failure) {
                    return@combine FavSpotsState.Error(spotsResult.error?.message ?: "Failed to load favorite spots.")
                }
                if (quiverResult is Result.Failure) {
                    return@combine FavSpotsState.Error(quiverResult.error?.message ?: "Failed to load your quiver.")
                }

                // אם הגענו לכאן, כל הנתונים הבסיסיים קיימים
                val user = (userResult as Result.Success).data!!
                val spots = (spotsResult as Result.Success).data ?: emptyList()
                val quiver = (quiverResult as Result.Success).data ?: emptyList()

                // ================== שלב 2: עיבוד נתונים ולוגיקה עסקית ==================

                if (spots.isEmpty()) {
                    platformLogger("FavSpotsViewModel", "No favorite spots found.")
                    return@combine FavSpotsState.Loaded(FavSpotsData(
                        boards = quiver, spots = emptyList(),
                        allSpotsDailyPredictions = emptyList(),
                        currentForecastsForAllSpots = emptyList(),
                        weeklyForecastForSpecificSpot = emptyList(),
                        weeklyPredictionsForSpecificSpot = emptyList(),
                        weeklyUiPredictions = emptyList()
                    ))
                }

                // הבאת כל התחזיות היומיות עבור הספוטים
                val forecasts = spots.mapNotNull { spot ->
                    (favSpotsUseCases.getDailyForecast(spot) as? Result.Success)?.data
                }

                // אם אין גלשנים, נציג רק את הספוטים והתחזיות ללא המלצות
                if (quiver.isEmpty()) {
                    platformLogger("FavSpotsViewModel", "Quiver is empty, showing spots without predictions.")
                    return@combine FavSpotsState.Loaded(
                        FavSpotsData(spots = spots, boards = quiver, currentForecastsForAllSpots = forecasts,
                            allSpotsDailyPredictions = emptyList(),
                            weeklyForecastForSpecificSpot = emptyList(),
                            weeklyPredictionsForSpecificSpot = emptyList(),
                            weeklyUiPredictions = emptyList()
                        )
                    )
                }

                // יש גלשנים, נחשב המלצות
                platformLogger("FavSpotsViewModel", "Generating predictions for ${spots.size} spots and ${quiver.size} boards.")
                when (val predictionsResult = favSpotsUseCases.generateAllTodayPredictionsUseCase(user, quiver, forecasts)) {
                    is Result.Success -> {
                        platformLogger("FavSpotsViewModel", "Successfully generated predictions.")
                        FavSpotsState.Loaded(
                            FavSpotsData(
                                spots = spots,
                                boards = quiver,
                                currentForecastsForAllSpots = forecasts,
                                allSpotsDailyPredictions = predictionsResult.data ?: emptyList(),
                                weeklyForecastForSpecificSpot = emptyList(),
                                weeklyPredictionsForSpecificSpot = emptyList(),
                                weeklyUiPredictions = emptyList()
                            )
                        )
                    }
                    is Result.Failure -> {
                        platformLogger("FavSpotsViewModel", "Failed to generate predictions, showing forecast only. Error: ${predictionsResult.error?.message}")
                        // אם ההמלצה נכשלה, לפחות נציג את הספוטים והתחזיות
                        FavSpotsState.Loaded(
                            FavSpotsData(spots = spots, boards = quiver, currentForecastsForAllSpots = forecasts,
                                allSpotsDailyPredictions = emptyList(),
                                weeklyForecastForSpecificSpot = emptyList(),
                                weeklyPredictionsForSpecificSpot = emptyList(),
                                weeklyUiPredictions = emptyList()
                            )
                        )
                    }
                }
            }.collect { state ->
                // ה-collect פשוט מעדכן את ה-UI עם המצב שחושב ב-combine
                _uiState.value = state
            }
        }
    }

    fun onEvent(event: FavSpotsEvent) {
        when (event) {
            is FavSpotsEvent.DeleteSpot -> deleteSpot(event.favoriteSpot, event.snackBarDurationMillis)
            is FavSpotsEvent.LoadWeekPredictions -> weeklyPredictions(event.favoriteSpot)
            is FavSpotsEvent.ErrorOccurred -> scope.launch { _uiState.emit(FavSpotsState.Error(event.message)) }
            is FavSpotsEvent.UndoDeleteSpot -> undoDeleteSpot()
        }
    }

    private fun deleteSpot(favoriteSpot: FavoriteSpot, snackBarDurationMillis: Long) {
        lastDismissedSpot = favoriteSpot

        // עדכון אופטימי של ה-UI: מסירים את הפריט מיידית מהתצוגה
        val currentState = _uiState.value
        if (currentState is FavSpotsState.Loaded) {
            val updatedSpots = currentState.favSpotsData.spots.filter { it.spotID != favoriteSpot.spotID }
            val updatedData = currentState.favSpotsData.copy(spots = updatedSpots)
            _uiState.value = currentState.copy(favSpotsData = updatedData)
        }

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
            favSpotsUseCases.deleteAllPredictionsBySpotUseCase(favoriteSpot.spotLatitude, favoriteSpot.spotLongitude)
            favSpotsUseCases.deleteBySpotUseCase(favoriteSpot.spotLatitude, favoriteSpot.spotLongitude)
            favSpotsUseCases.removeFavSpotsUseCase(favoriteSpot)
            // אין צורך לרענן ידנית, ה-Flow מה-Repository יזהה את השינוי ויפעיל את ה-combine
            platformLogger("FavSpotsViewModel", "Spot permanently deleted: ${favoriteSpot.name}")
        }
    }

    private fun undoDeleteSpot() {
        deleteJob?.cancel()
        lastDismissedSpot = null
        // מפעילים את טריגר הרענון. ה-combine ירוץ מחדש ויקרא את המצב העדכני
        // מה-DB, שם הספוט עדיין לא נמחק סופית.
        refreshTrigger.value++
        platformLogger("FavSpotsViewModel", "Undo delete triggered. Refreshing state.")
    }

    private fun weeklyPredictions(spot: FavoriteSpot) {
        scope.launch {
            val currentState = _uiState.value
            if (currentState !is FavSpotsState.Loaded) {
                _uiState.emit(FavSpotsState.Error("Cannot load weekly predictions: data not loaded yet"))
                return@launch
            }

            // בדיקה אם המידע כבר טעון לאותו ספוט
            val weeklyData = currentState.favSpotsData.weeklyForecastForSpecificSpot
            if (weeklyData.isNotEmpty() && weeklyData.first().latitude == spot.spotLatitude) {
                return@launch // המידע כבר קיים, אין צורך לטעון שוב
            }

            // איפוס המידע השבועי הקודם ועדכון ה-UI למצב טעינה חלקי
            updateUiState { it.copy(weeklyForecastForSpecificSpot = emptyList(), weeklyPredictionsForSpecificSpot = emptyList(), weeklyUiPredictions = emptyList()) }

            val weeklyForecastResult = favSpotsUseCases.getWeeklyForecastBySpotUseCase(spot, false)
            when (weeklyForecastResult) {
                is Result.Failure -> _uiState.emit(FavSpotsState.Error(weeklyForecastResult.error?.message ?: "Failed to fetch weekly forecast"))
                is Result.Success -> {
                    val weeklyForecast = weeklyForecastResult.data ?: emptyList()
                    if (weeklyForecast.isEmpty()) {
                        _uiState.emit(FavSpotsState.Error("No weekly forecast found for this spot."))
                        return@launch
                    }

                    val quiverResult = favSpotsUseCases.getAllQuiverUseCase().firstOrNull()
                    if (quiverResult is Result.Success) {
                        val quiver = quiverResult.data ?: emptyList()
                        val predictionsResult = favSpotsUseCases.generateWeeklyPredictions(quiver, weeklyForecast)

                        if (predictionsResult is Result.Success) {
                            val weeklyPredictions = predictionsResult.data ?: emptyList()
                            val finalWeeklyPrediction = mutableListOf<DailyPrediction>()

                            weeklyPredictions.forEach { prediction ->
                                val bestBoard = quiver.find { it.id == prediction.surfboardID }
                                val dailyForecast = weeklyForecast.find { it.date == prediction.date }
                                if (bestBoard != null && dailyForecast != null) {
                                    finalWeeklyPrediction.add(DailyPrediction(prediction, bestBoard, dailyForecast))
                                }
                            }

                            updateUiState {
                                it.copy(
                                    weeklyForecastForSpecificSpot = weeklyForecast,
                                    weeklyPredictionsForSpecificSpot = weeklyPredictions,
                                    weeklyUiPredictions = finalWeeklyPrediction
                                )
                            }
                        } else if (predictionsResult is Result.Failure) {
                            _uiState.emit(FavSpotsState.Error(predictionsResult.error?.message ?: "Failed to fetch weekly predictions"))
                        }
                    }
                }
            }
        }
    }

    private fun updateUiState(update: (FavSpotsData) -> FavSpotsData) {
        val currentState = _uiState.value
        if (currentState is FavSpotsState.Loaded) {
            _uiState.update { FavSpotsState.Loaded(update(currentState.favSpotsData)) }
        }
    }
}