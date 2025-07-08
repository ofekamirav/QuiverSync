package org.example.quiversync.features.spots.FavSpotMainPage

import org.example.quiversync.domain.usecase.favSpots.AddFavSpot
import org.example.quiversync.domain.usecase.favSpots.ClearAllFavSpots
import org.example.quiversync.domain.usecase.favSpots.GetAllFavUserSpots
import org.example.quiversync.domain.usecase.favSpots.RemoveFavSpot
import org.example.quiversync.domain.usecase.forecast.DeleteOutDateForecastUseCase
import org.example.quiversync.domain.usecase.forecast.GetDailyForecast
import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastBySpotUseCase
import org.example.quiversync.domain.usecase.gemini.GetBestBoardForSingleDayUseCase
import org.example.quiversync.domain.usecase.gemini.GetBestMatchForTodayUseCase
import org.example.quiversync.domain.usecase.gemini.GetPredictionsForTodayUseCase
import org.example.quiversync.domain.usecase.quiver.GetMyQuiverUseCase
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase

class FavSpotsUseCases(
    val clearAllFavSpotsUseCase: ClearAllFavSpots,
    val getAllFavUserSpotsUseCase: GetAllFavUserSpots,
    val addFavSpotUseCase: AddFavSpot,
    val removeFavSpotsUseCase: RemoveFavSpot,
    val deleteOutDateForecastUseCase: DeleteOutDateForecastUseCase,
    val getBestBoardForSingleDayUseCase: GetBestBoardForSingleDayUseCase,
    val getAllQuiverUseCase: GetMyQuiverUseCase,
    val getUserProfileUseCase: GetUserProfileUseCase,
    val getDailyForecast: GetDailyForecast,
    val getWeeklyForecastBySpotUseCase: GetWeeklyForecastBySpotUseCase,
    val getTodayPredictionBySpot : GetBestMatchForTodayUseCase,
    val GetPredictionsForTodayUseCase : GetPredictionsForTodayUseCase
)