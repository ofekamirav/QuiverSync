package org.example.quiversync.domain.model.forecast

data class WeeklyForecast (
    val list: List<DailyForecast>
)