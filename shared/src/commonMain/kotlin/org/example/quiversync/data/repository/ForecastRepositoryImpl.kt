package org.example.quiversync.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.QuiverSyncDatabase
import org.example.quiversync.data.remote.api.StormGlassApi
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.utils.Location
import org.example.quiversync.utils.extensions.isOutsideRadius
import org.example.quiversync.utils.extensions.distanceBetween
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
    ): Result<List<DailyForecast>, TMDBError> {
        println("🌍 getWeeklyForecast called with: lat=$latitude, lon=$longitude, inHomePage=$inHomePage")

        var howManyDaily = 0

        if(inHomePage){
            val forecasts = queries.selectAll().executeAsList()

            val nearbyForecast = forecasts.firstOrNull { forecast ->
                distanceBetween(
                    lat1 = latitude,
                    lon1 = longitude,
                    lat2 = forecast.latitude,
                    lon2 = forecast.longitude
                ) <= 7
            }

            if (nearbyForecast != null) {
                println("📦 Found nearby forecast in local DB for ($latitude, $longitude)")
                sessionManager.setLatitude(nearbyForecast.latitude)
                sessionManager.setLongitude(nearbyForecast.longitude)
                howManyDaily =  queries.howManyBySpot(nearbyForecast.latitude, nearbyForecast.longitude).executeAsOne().toInt()
                println("📦 Found $howManyDaily daily forecasts in local DB for nearby spot (${nearbyForecast.latitude}, ${nearbyForecast.longitude})")
            } else {
                println("❌ No nearby forecast found in local DB for ($latitude, $longitude)")
            }

        }
        else{
            howManyDaily = queries.howManyBySpot(latitude, longitude).executeAsOne().toInt()
            println("📦 Found $howManyDaily daily forecasts in local DB for ($latitude, $longitude)")
        }

        val userID = sessionManager.getUid()
        val lastLocation = sessionManager.getLastLocation()


        val currentDate = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            .date.toString()

        println("🧭 Last location: ${lastLocation?.latitude}, ${lastLocation?.longitude}")
        val isFar = lastLocation == null || isOutsideRadius(
            lastLocation.latitude, lastLocation.longitude, latitude, longitude
        )
        println("📏 isFar from last location? $isFar")

        // Check if we are on the homepage and the location hasn't changed much
        if (inHomePage) {
            val lastRefresh = sessionManager.getLastRefresh()
            println("🗓 Current date: $currentDate | Last refresh: $lastRefresh")

            if (lastRefresh == null || lastRefresh != currentDate) {
                println("🔄 Refreshing forecasts due to outdated or missing refresh date")
                sessionManager.setLastRefresh()
            } else {

                println("✅ Using cached forecasts from local DB (fresh for today)")

                if (lastLocation != null && !isFar && howManyDaily >= 6) {
                    val localList = queries.SELECT_BY_SPOT(lastLocation.latitude, lastLocation.longitude)
                        .executeAsList()
                        .map { it.toDailyForecast() }

                    println("📥 Returning ${localList.size} cached forecasts from local DB")
                    return Result.Success(localList)
                }else if( lastLocation == null && howManyDaily >= 6) {
                    // If we have no last location, but enough forecasts, we can still return them
                    val localList = queries.SELECT_BY_SPOT(latitude, longitude)
                        .executeAsList()
                        .map { it.toDailyForecast() }

                    sessionManager.setLastLocation(Location(latitude, longitude))
                    println("📥 Returning ${localList.size} cached forecasts from local DB for ($latitude, $longitude)")
                    return Result.Success(localList)
                }

            }
        }


        if (howManyDaily < 6) {
            if (howManyDaily > 0) {
                println("🗑 Clearing old or insufficient forecasts from local DB")
                queries.deleteBySpot(latitude, longitude)
            }

            if (userID != null && !inHomePage) {
                println("🗑 Deleting Gemini predictions for user $userID at ($latitude, $longitude)")
                geminiPredictionDao.deleteBySpotAndUser(latitude, longitude, userID)
            }
            else if (userID != null && inHomePage) {
                println("🗑 Deleting Gemini predictions for user $userID at last location (${lastLocation?.latitude}, ${lastLocation?.longitude})")
                if (lastLocation != null) {
                    geminiPredictionDao.deletePredictionByDateAndIDAndLocation(currentDate,userID,lastLocation.latitude, lastLocation.longitude)
                }
            }

            println("🌐 Fetching new forecast from API for ($latitude, $longitude)")
            val weeklyResult = api.getFiveDayForecast(latitude, longitude)

            if (weeklyResult.isFailure) {
                val errorMessage = weeklyResult.exceptionOrNull()?.message ?: "Unknown error"
                println("❌ Failed to fetch forecast from API: $errorMessage")
                return Result.Failure(TMDBError(errorMessage))
            }

            val weekly = weeklyResult.getOrNull()
            if (weekly == null) {
                println("❌ API returned null forecast list")
                return Result.Failure(TMDBError("Weekly forecast is null"))
            }

            if (weekly.list.isEmpty()) {
                println("❌ API returned empty forecast list")
                return Result.Failure(TMDBError("Empty forecast list"))
            }

            println("✅ Successfully fetched ${weekly.list.size} days of forecast. Inserting into DB...")
            queries.deleteBySpot(latitude, longitude)

            weekly.list.forEach { day ->
                println("📅 Inserting day: ${day.date} with waveHeight=${day.waveHeight}")
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
            println("📍 Updated last location to ($latitude, $longitude)")
        } else {
            println("📦 Using existing $howManyDaily forecasts from local DB")
        }

        val localList = queries.SELECT_BY_SPOT(latitude, longitude)
            .executeAsList()
            .map { it.toDailyForecast() }

        println("✅ Final forecast list size: ${localList.size}")
        return if (localList.isNotEmpty()) {
            Result.Success(localList)
        } else {
            println("❌ No forecasts found in DB after all steps")
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
//            println("Using local database for spot ($latitude, $longitude) with $howManyDaily days of forecast")
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
            val lastLocation = sessionManager.getLastLocation()
            if (lastLocation != null && lastLocation.latitude == latitude && lastLocation.longitude == longitude) {
                println("📍 Dont delete the forecast due to extra use in it in the home page($latitude, $longitude)")
                return Result.Success(Unit)
            }
            queries.deleteBySpot(latitude, longitude)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(TMDBError("Error deleting forecast: ${e.message ?: "Unknown error"}"))
        }
    }




}