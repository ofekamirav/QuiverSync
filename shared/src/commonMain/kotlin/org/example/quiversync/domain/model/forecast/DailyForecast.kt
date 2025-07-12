package org.example.quiversync.domain.model.forecast

data class DailyForecast(
    val date: String = "",
    val waveHeight: Double = 0.0,
    val windSpeed: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val windDirection: Double = 0.0,
    val swellPeriod: Double = 0.0,
    val swellDirection: Double = 0.0,
)