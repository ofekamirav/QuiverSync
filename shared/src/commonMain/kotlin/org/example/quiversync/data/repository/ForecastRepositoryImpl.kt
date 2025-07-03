package org.example.quiversync.data.repository

import org.example.quiversync.QuiverSyncDatabase
import org.example.quiversync.data.remote.api.StormGlassApi
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.utils.Location
import org.example.quiversync.utils.extensions.isOutsideRadius
import org.example.quiversync.utils.extensions.toDailyForecast

class ForecastRepositoryImpl(
    private val api: StormGlassApi,
    private val database: QuiverSyncDatabase,
    private val sessionManager: SessionManager,
): ForecastRepository {
    private val queries = database.dailyForecastQueries
    override suspend fun getWeeklyForecast(
        latitude: Double,
        longitude: Double,
    ): Result<WeeklyForecast> {

        val lastLocation = sessionManager.getLastLocation()
        val isFar = lastLocation == null || isOutsideRadius(
            lastLocation.latitude, lastLocation.longitude, latitude, longitude
        )

        if (isFar) {
            val weeklyResult = api.getFiveDayForecast(latitude, longitude)

            if (weeklyResult.isFailure) {
                return Result.failure(weeklyResult.exceptionOrNull() ?: Exception("Unknown error"))
            }

            val weekly = weeklyResult.getOrNull()
                ?: return Result.failure(Exception("No forecast returned"))

            if (weekly.list.isEmpty()) {
                return Result.failure(Exception("Empty forecast list"))
            }

            queries.deleteBySpot(latitude, longitude)

            weekly.list.forEach { day ->
                queries.insertOrReplace(
                    date = day.date,
                    latitude = latitude,
                    longitude = longitude,
                    waveHeight = day.waveHeight,
                    windSpeed = day.windSpeed,
                    windDirection = day.windDirection,
                    swellPeriod = day.swellPeriod,
                    swellDirection = day.swellDirection
                )
            }

            sessionManager.setLastLocation(Location(latitude, longitude))
        }

        val localList = queries.selectAll().executeAsList().map { it.toDailyForecast() }

        return if (localList.isNotEmpty()) {
            Result.success(WeeklyForecast(localList))
        } else {
            Result.failure(Exception("No local forecast found"))
        }
    }

    override suspend fun getDailyForecastByDateAndSpot(
        latitude: Double,
        longitude: Double,
        date: String,
    ): Result<DailyForecast?> {
        val local = queries.selectToday(date, latitude, longitude).executeAsOneOrNull()
        return Result.success(local?.toDailyForecast())    }

}