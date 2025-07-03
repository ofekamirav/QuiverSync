package org.example.quiversync.domain.model.forecast

data class DailyForecast (
    val date : String,
    val waveHeight: Double,
    val windSpeed: Double,
    val latitude: Double,
    val longitude: Double,
    val windDirection: Double,
    val swellPeriod: Double,
    val swellDirection: Double,
    )                                                                                                    