package org.example.quiversync.domain.model.Forecast

data class WeeklyForecast (
    val list: List<DailyForecast>
)