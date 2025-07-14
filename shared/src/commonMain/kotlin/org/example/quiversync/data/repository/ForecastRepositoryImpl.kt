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
        inHomePage: Boolean
    ): Result<List<DailyForecast>,TMDBError> {
        val howManyDaily = queries.howManyBySpot(latitude, longitude).executeAsOne()
        val userID = sessionManager.getUid()
        val lastLocation = sessionManager.getLastLocation()
        val isFar = lastLocation == null || isOutsideRadius(
            lastLocation.latitude, lastLocation.longitude, latitude, longitude
        )

        //only if you are on the home page and the spot is not far from the last location i need to check if i need to refresh the forecasts
        //if not far , i will check in the local or will load weekly from the API
        if(inHomePage && !isFar) {
                val currentDate = kotlinx.datetime.Clock.System.now()
                    .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                    .date
                    .toString()

                val lastRefresh = sessionManager.getLastRefresh()
                if (lastRefresh == null || lastRefresh != currentDate) {
                    println("Refreshing forecasts for spot ($latitude, $longitude) because last refresh is outdated")
                    sessionManager.setLastRefresh()
                } else {
                    println("Using local database for spot ($latitude, $longitude) with $howManyDaily days of forecast")
                    if (lastLocation != null) {
                        return Result.Success(
                            queries.SELECT_BY_SPOT(lastLocation.latitude, lastLocation.longitude)
                                .executeAsList()
                                .map { it.toDailyForecast() }
                        )
                    }
                }
        }


        if (howManyDaily < 6) {
            if(howManyDaily > 0){
                println("Deleting old forecasts for spot ($latitude, $longitude) because they are outdated or insufficient")
                queries.deleteBySpot(latitude, longitude)
            }
            if (userID != null) {
                println("Deleting old Gemini predictions for spot ($latitude, $longitude) for user $userID")
                geminiPredictionDao.deleteBySpotAndUser(latitude, longitude,userID)
            }
            println("Fetching new weekly forecast from API for spot ($latitude, $longitude)")
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
        else{
            println( "Using local database for spot ($latitude, $longitude) with $howManyDaily days of forecast")
        }

        val localList = queries.SELECT_BY_SPOT( latitude, longitude)
            .executeAsList()
            .map { it.toDailyForecast() }

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
            val weeklyResult = getWeeklyForecast(latitude, longitude , false)
            if (weeklyResult is Result.Failure) {
                return Result.Failure(TMDBError("Unknown error"))
            }
        }
        else{
            println("Using local database for spot ($latitude, $longitude) with $howManyDaily days of forecast")
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

    override suspend fun deleteBySpot(
        latitude: Double,
        longitude: Double
    ): Result<Unit, TMDBError> {

        try {
            queries.deleteBySpot(latitude, longitude)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(TMDBError("Error deleting forecast: ${e.message ?: "Unknown error"}"))
        }
    }


}