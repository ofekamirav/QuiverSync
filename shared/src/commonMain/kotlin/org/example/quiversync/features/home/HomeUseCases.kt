package org.example.quiversync.features.home

import org.example.quiversync.domain.usecase.GetWeeklyForecastByLocationUseCase

data class HomeUseCases(
    val getWeeklyForecastByLocationUseCase: GetWeeklyForecastByLocationUseCase
)