package org.example.quiversync.features.forecast

import org.example.quiversync.domain.model.Forecast.WeeklyForecast

sealed class ForecastState {
    object Loading : ForecastState()
    data class Loaded(val forecast: WeeklyForecast) : ForecastState()
    data class Error(val message: String) : ForecastState()
}