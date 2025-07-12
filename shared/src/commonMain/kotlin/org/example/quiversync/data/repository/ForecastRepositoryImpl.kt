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
        val userID = sessionManager.getUid()

        val isFar = lastLocation == null || isOutsideRadius(
            lastLocation.latitude, lastLocation.longitude, latitude, longitude
        )

        if (isFar && howManyDaily < 6) {
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
//        queries.deleteBySpot(latitude, longitude)
//        println("deleted old forecasts for spot ($latitude, $longitude)")
        val howManyDaily = queries.howManyBySpot(latitude, longitude).executeAsOne()
        if(howManyDaily < 4) {
            // If we have less than 4 days, we need to fetch the weekly forecast again
            val weeklyResult = getWeeklyForecast(latitude, longitude)
            if (weeklyResult is Result.Failure) {
                return Result.Failure(TMDBError("Unknown error"))
            }
        }
        else{
            println("Using local database for spot ($latitude, $longitude) with $howManyDaily days of forecast")
        }
//        val forecasts = queries.selectAll().executeAsList()
//
//        println("📊 Daily Forecasts:")
//        forecasts.forEachIndexed { index, it ->
//            println(
//                """
//        🔹 Forecast #${index + 1}
//        ─────────────────────
//        📅 Date         : ${it.date}
//        📍 Location     : (${it.latitude}, ${it.longitude})
//        🌊 Wave Height  : ${it.waveHeight} m
//        💨 Wind Speed   : ${it.windSpeed} m/s
//        🧭 Wind Dir     : ${it.windDirection}°
//        🌊 Swell Period : ${it.swellPeriod} s
//        🌊 Swell Dir    : ${it.swellDirection}°
//        """.trimIndent()
//            )
//        }
        val local = queries.selectToday(date, latitude, longitude).executeAsOneOrNull()
        println("📊 Creating Prompt With Daily Forecast From Local:")
        println(
            """
        🔹 Forecast 
        ─────────────────────
        📅 Date         : ${local?.date}
        📍 Location     : (${local?.latitude}, ${local?.longitude})
        🌊 Wave Height  : ${local?.waveHeight} m
        💨 Wind Speed   : ${local?.windSpeed} m/s
        🧭 Wind Dir     : ${local?.windDirection}°
        🌊 Swell Period : ${local?.swellPeriod} s
        🌊 Swell Dir    : ${local?.swellDirection}°
        """.trimIndent()
        )

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