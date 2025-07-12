package org.example.quiversync.domain.repository

import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast

interface ForecastRepository {
    suspend fun getWeeklyForecast(
        latitude: Double,
        longitude: Double
    ): Result<List<DailyForecast>,TMDBError>

    suspend fun getDailyForecastByDateAndSpot(
        latitude: Double,
        longitude: Double
    ): Result<DailyForecast, TMDBError>

    suspend fun deleteOutDateForecast():
        Result<Unit, TMDBError>

    suspend fun deleteBySpot(
        latitude: Double,
        longitude: Double
    ): Result<Unit, TMDBError>
}