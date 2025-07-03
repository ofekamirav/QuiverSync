package org.example.quiversync.features.home

import org.example.quiversync.data.remote.dto.BoardMatchUi
import org.example.quiversync.domain.model.forecast.WeeklyForecast

sealed class HomeState {
    object Loading : HomeState()
    data class Loaded(
        val forecast: WeeklyForecast,
        val todayMatchBoard: BoardMatchUi
        ) : HomeState()
    data class Error(val message: String) : HomeState()
}