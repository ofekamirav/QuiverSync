package org.example.quiversync.domain.repository


import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.forecast.DailyForecast


interface ForecastRepository {
    suspend fun getWeeklyForecast(
        latitude: Double,
        longitude: Double,
        isHomePage: Boolean
    ): Result<List<DailyForecast>, Error>

    suspend fun getDailyForecastByDateAndSpot(
        latitude: Double,
        longitude: Double
    ): Result<DailyForecast, Error>

    suspend fun deleteOutDateForecast():
        Result<Unit, Error>

    suspend fun deleteBySpot(
        latitude: Double,
        longitude: Double
    ): Result<Unit, Error>
}