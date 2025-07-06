package org.example.quiversync.features.spots

import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastByLocationUseCase
import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastBySpotUseCase

data class ForecastUseCases (
    val getWeeklyForecastBySpotUseCase: GetWeeklyForecastBySpotUseCase,
    val getWeeklyForecastByLocationUseCase: GetWeeklyForecastByLocationUseCase
)