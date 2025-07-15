package org.example.quiversync.data.repository

import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.GeminiPredictionDao
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.remote.api.GeminiApi
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.repository.GeminiRepository
import org.example.quiversync.utils.extensions.platformLogger

class GeminiRepositoryImpl(
    private val api: GeminiApi,
    private val dao: GeminiPredictionDao,
    private val userDao : UserDao,
    private val sessionManager: SessionManager
    ) : GeminiRepository {

    /**
     * Generates and stores a prediction matching surfboards to a daily forecast
     * for a specific user.
     */
    override suspend fun generateSingleDayMatch(
        surfboards: List<Surfboard>,
        dailyForecast: DailyForecast,
        user : User
    ): Result<GeminiPrediction, TMDBError> {
        return try {

            val uid = sessionManager.getUid()
                ?: return Result.Failure(TMDBError("User UID is null, cannot generate match"))
            val user = userDao.getUserProfile(uid)
                ?: return Result.Failure(TMDBError("User not found for UID: $uid"))
            val existingPrediction = dao.getMatch(
                dailyForecast.date,
                dailyForecast.latitude,
                dailyForecast.longitude,
                user.uid
            )
            if (existingPrediction != null) {
                println("GeminiRepositoryImpl : generateSingleDayMatch() - Found existing prediction with score ${existingPrediction.score} ")
                return Result.Success(existingPrediction)
            }else{
                platformLogger("GeminiRepositoryImpl", "generateSingleDayMatch() - No existing prediction found, generating new match")
            }
            when (val result = api.predictionForOneDay(surfboards, dailyForecast, user)) {
                is Result.Failure -> Result.Failure(result.error)
                is Result.Success -> {
                    val prediction = result.data
                    println("GeminiRepo : " +
                            "this is the prediction generated for user ${user.uid}" +
                            "${result.data}")
                    if (prediction != null) {
//                        println("GeminiRepositoryImpl : generateSingleDayMatch() - Prediction generated :  $prediction on date ${dailyForecast.date}")
                        dao.insert(prediction, user.uid)
                        Result.Success(prediction)
                    } else {
                        Result.Failure(TMDBError("Prediction returned null"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }






    /**
     * Generates predictions for each day in a weekly forecast and returns
     * the complete list of predictions.
     */
    override suspend fun generateAndStoreWeeklyBestMatches(
        surfboards: List<Surfboard>,
        weeklyForecast: List<DailyForecast>
    ): Result<List<GeminiPrediction>, TMDBError> {
        return try {
            val userUid = sessionManager.getUid()
                ?: return Result.Failure(TMDBError("User UID is null, cannot generate weekly matches"))
            // clear all predictions for the spot before generating new ones
            val localPredictions = dao.getPredictionsBySpot(
                userUid,
                weeklyForecast.first().latitude,
                weeklyForecast.first().longitude
            )
            if (localPredictions.size == 6){
                println("GeminiRepositoryImpl : generateAndStoreWeeklyBestMatches() - Found ${localPredictions.size} predictions for user $userUid")
                return Result.Success(localPredictions)
            }
            println("Local predictions for spot (${weeklyForecast.first().latitude}, ${weeklyForecast.first().longitude}) " +
                    "are not complete, generating new predictions for user $userUid")
            dao.deleteBySpotAndUser(
                weeklyForecast.first().latitude,
                weeklyForecast.first().longitude,
                userUid
            )
            val user = userDao.getUserProfile(userUid)
                ?: return Result.Failure(TMDBError("User not found for UID: $userUid"))
            val predictions = mutableListOf<GeminiPrediction>()
            when (val result = api.predictionForWeek( surfboards, weeklyForecast, user)) {
                is Result.Failure -> return Result.Failure(result.error)
                is Result.Success -> {
                    // Store each prediction in the database
                    result.data?.forEach { prediction ->
                        dao.insert(prediction, userUid)
                        predictions.add(prediction)
                    }
                }
            }
            Result.Success(predictions)
        } catch (e: Exception) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }


    /**
     * Retrieves all prediction matches for a location spanning a week.
     * Currently unimplemented.
     */
    override suspend fun getBestBoardMatchForWeek(
        latitude: Double,
        longitude: Double
    ): Result<List<GeminiPrediction>, TMDBError> {
        // Optional: You can implement logic to query matches by spot for the week
        TODO("Implement weekly fetch using DAO if needed")
    }

    /**
     * Deletes all stored predictions for a specific location.
     */
    override suspend fun deleteAllPredictionsBySpot(
        latitude: Double,
        longitude: Double
    ): Result<Unit, TMDBError> {
        return try {
            // Clear all predictions for the specified latitude and longitude
            val userUid = sessionManager.getUid()
            if (userUid != null) {
                dao.deleteBySpotAndUser(latitude, latitude, userUid)
            }else{
                return Result.Failure(TMDBError("User UID is null, cannot clear predictions"))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }

    override suspend fun generatePredictionsForTodayAllSpots(
        user: User,
        surfboards: List<Surfboard>,
        dailyForecasts: List<DailyForecast>
    ): Result<List<GeminiPrediction>, TMDBError> {
        return try {
            println("🌀 Starting prediction generation for all spots (User: ${user.uid})")
            println("📆 Received ${dailyForecasts.size} forecasts for today:\n" +
                    dailyForecasts.joinToString("\n") {
                        "• Date: ${it.date}, Location: (${it.latitude}, ${it.longitude}), WaveHeight: ${it.waveHeight}m"
                    }
            )

            val predictions = mutableListOf<GeminiPrediction>()
            val forecastsForProcess = mutableListOf<DailyForecast>()
            val userUid = sessionManager.getUid()
                ?: return Result.Failure(TMDBError("User UID is null, cannot generate predictions"))

            dailyForecasts.forEach { forecast ->
                val existingPrediction = dao.getMatch(
                    forecast.date,
                    forecast.latitude,
                    forecast.longitude,
                    userUid
                )

                if (existingPrediction != null) {
                    println("✅ Existing prediction found for ${forecast.date} at (${forecast.latitude}, ${forecast.longitude}) → Skipping")
                    predictions.add(existingPrediction)
                } else {
                    println("🆕 No prediction for ${forecast.date} at (${forecast.latitude}, ${forecast.longitude}) → Will generate")
                    forecastsForProcess.add(forecast)
                }
            }

            if (forecastsForProcess.isEmpty()) {
                println("🎉 All predictions already exist — returning ${predictions.size} predictions")
                return Result.Success(predictions)
            }

            println("🚀 Sending ${forecastsForProcess.size} forecasts to Gemini API for prediction:")
            forecastsForProcess.forEachIndexed { i, f ->
                println("   🔹 [$i] ${f.date} @ (${f.latitude}, ${f.longitude}) - Wave: ${f.waveHeight}m, Wind: ${f.windSpeed}m/s")
            }

            when (val result = api.predictionForMultipleSpots(surfboards, forecastsForProcess, user)) {
                is Result.Failure -> {
                    println("❌ Gemini API returned failure: ${result.error?.message}")
                    return Result.Failure(result.error)
                }

                is Result.Success -> {
                    val received = result.data?.size ?: 0
                    println("🎯 Gemini API returned $received new predictions:")
                    result.data?.forEachIndexed { i, prediction ->
                        println("   📦 [$i] Date: ${prediction.date}, Spot: (${prediction.forecastLatitude}, ${prediction.forecastLongitude}), Score: ${prediction.score}")
                        dao.insert(prediction, userUid)
                        predictions.add(prediction)
                    }
                }
            }

            println("✅ Finished generating predictions → Returning total of ${predictions.size} predictions")
            Result.Success(predictions)

        } catch (e: Exception) {
            println("🔥 Exception during prediction generation: ${e.message}")
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }


    /**
     * Retrieves all predictions stored for the current date.
     */
    override suspend fun getPredictionsForToday(): Result<List<GeminiPrediction>, TMDBError> {
        return try {
            val userUid = sessionManager.getUid()
            val currentDate = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date
                .toString()
            if( userUid == null) {
                return Result.Failure(TMDBError("User UID is null, cannot fetch predictions"))
            }
            val predictions = dao.getPredictionsForToday(currentDate,userUid)
            if (predictions.isNotEmpty()) Result.Success(predictions)
            else Result.Failure(TMDBError("No predictions found for today"))
        } catch (e: Exception) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }
}
