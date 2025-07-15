package org.example.quiversync.features.spots

import org.example.quiversync.domain.usecase.favSpots.AddFavSpot
import org.example.quiversync.domain.usecase.favSpots.GetAllFavUserSpots
import org.example.quiversync.domain.usecase.favSpots.RemoveFavSpot
import org.example.quiversync.domain.usecase.forecast.DeleteBySpot
import org.example.quiversync.domain.usecase.forecast.DeleteOutDateForecastUseCase
import org.example.quiversync.domain.usecase.forecast.GetDailyForecast
import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastBySpotUseCase
import org.example.quiversync.domain.usecase.gemini.DeleteAllPredictionsBySpotUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateAllTodayPredictionsUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateWeeklyPredictionsUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateSingleDayMatchUseCase
import org.example.quiversync.domain.usecase.quiver.GetMyQuiverUseCase
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase

class FavSpotsUseCases(
    // Favorite Spots Use Cases
    val getAllFavUserSpotsUseCase: GetAllFavUserSpots,
    val addFavSpotUseCase: AddFavSpot,
    val removeFavSpotsUseCase: RemoveFavSpot,
    val deleteOutDateForecastUseCase: DeleteOutDateForecastUseCase,
    val getAllQuiverUseCase: GetMyQuiverUseCase,
    val getUserProfileUseCase: GetUserProfileUseCase,

    val getUser : GetUserProfileUseCase,

    // Forecast and Prediction Use Cases
    val getDailyForecast: GetDailyForecast,
    val getWeeklyForecastBySpotUseCase: GetWeeklyForecastBySpotUseCase,
    val deleteBySpotUseCase: DeleteBySpot,

    // Gemini Use Cases
    val generateBestBoardForSingleDayUseCase: GenerateSingleDayMatchUseCase,
    val generateWeeklyPredictions : GenerateWeeklyPredictionsUseCase,
    val deleteAllPredictionsBySpotUseCase: DeleteAllPredictionsBySpotUseCase,
    val generateAllTodayPredictionsUseCase : GenerateAllTodayPredictionsUseCase
)