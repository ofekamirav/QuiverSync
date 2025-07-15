package org.example.quiversync.features.home

import org.example.quiversync.domain.model.prediction.DailyPrediction
import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastByLocationUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateAllTodayPredictionsUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateSingleDayMatchUseCase
import org.example.quiversync.domain.usecase.quiver.GetMyQuiverUseCase
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase

data class HomeUseCases(
    val getWeeklyForecastByLocationUseCase: GetWeeklyForecastByLocationUseCase,
    val getQuiverUseCase : GetMyQuiverUseCase,
    val getDailyPrediction: GenerateSingleDayMatchUseCase,
    val getUser: GetUserProfileUseCase
)