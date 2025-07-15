package org.example.quiversync.features.home

import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastByLocationUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateSingleDayMatchUseCase
import org.example.quiversync.domain.usecase.quiver.GetMyQuiverUseCase
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase
import org.example.quiversync.domain.usecase.user.IsImperialUnitsUseCase

data class HomeUseCases(
    val getWeeklyForecastByLocationUseCase: GetWeeklyForecastByLocationUseCase,
    val getQuiverUseCase : GetMyQuiverUseCase,
    val getDailyPrediction: GenerateSingleDayMatchUseCase,
    val getUser: GetUserProfileUseCase,
    val isImperialUnitsUseCases: IsImperialUnitsUseCase
)