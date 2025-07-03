package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast

interface ForecastRepository {
    suspend fun getWeeklyForecast(
        latitude: Double,
        longitude: Double
    ): Result<WeeklyForecast>

    suspend fun getDailyForecastByDateAndSpot(
        latitude: Double,
        longitude: Double,
        date: String
    ): Result<DailyForecast?>
}