package org.example.quiversync.domain.model.forecast

data class DailyForecast (
    val date : String,
    val waveHeight: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val swellPeriod: Double,
    val swellDirection: Double,
    )