package org.example.quiversync.data.repository

import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.QuiverSyncDatabase
import org.example.quiversync.data.remote.api.StormGlassApi
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.utils.Location
import org.example.quiversync.utils.extensions.isOutsideRadius
import org.example.quiversync.utils.extensions.toDailyForecast
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.GeminiPredictionDao

class ForecastRepositoryImpl(
    private val api: StormGlassApi,
    private val database: QuiverSyncDatabase,
    private val sessionManager: SessionManager,
    private val geminiPredictionDao: GeminiPredictionDao,

): ForecastRepository {
    private val queries = database.dailyForecastQueries
    override suspend fun getWeeklyForecast(
        latitude: Double,
        longitude: Double,
    ): Result<List<DailyForecast>,TMDBError> {
        val howManyDaily = queries.howManyBySpot(latitude, longitude).executeAsOne()
        val lastLocation = sessionManager.getLastLocation()

        val isFar = lastLocation == null || isOutsideRadius(
            lastLocation.latitude, lastLocation.longitude, latitude, longitude
        )

        if (isFar && howManyDaily < 7) {
            queries.deleteBySpot(latitude, longitude)
            geminiPredictionDao.deleteBySpot(latitude, longitude)
            val weeklyResult = api.getFiveDayForecast(latitude, longitude)

            if (weeklyResult.isFailure) {
                return Result.Failure(TMDBError(weeklyResult.exceptionOrNull()?.message ?: "Unknown error"))
            }

            val weekly = weeklyResult.getOrNull()
                ?: return Result.Failure(TMDBError("Weekly forecast is null"))

            if (weekly.list.isEmpty()) {
                return Result.Failure(TMDBError("Empty forecast list"))
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
            Result.Success(localList)
        } else {
            Result.Failure(TMDBError("No local forecast found"))
        }
    }

    override suspend fun getDailyForecastByDateAndSpot(
        latitude: Double,
        longitude: Double
    ): Result<DailyForecast, TMDBError> {
        val date = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            .date
            .toString()
        val howManyDaily = queries.howManyBySpot(latitude, longitude).executeAsOne()
        if(howManyDaily < 4) {
            // If we have less than 4 days, we need to fetch the weekly forecast again
            val weeklyResult = getWeeklyForecast(latitude, longitude)
            if (weeklyResult is Result.Failure) {
                return Result.Failure(TMDBError("Unknown error"))
            }
        }
        val local = queries.selectToday(date, latitude, longitude).executeAsOneOrNull()
        return Result.Success(local?.toDailyForecast())
    }

    override suspend fun deleteOutDateForecast(): Result<Unit, TMDBError> {
        try {
            val currentDate = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date
                .toString()
            queries.deleteForecastsOlderThan(currentDate)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(TMDBError("Error updating forecast: ${e.message ?: "Unknown error"}"))
        }
    }


}