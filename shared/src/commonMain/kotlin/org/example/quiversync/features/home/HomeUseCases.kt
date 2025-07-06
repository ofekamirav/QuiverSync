package org.example.quiversync.features.home

import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastByLocationUseCase

data class HomeUseCases(
    val getWeeklyForecastByLocationUseCase: GetWeeklyForecastByLocationUseCase
)