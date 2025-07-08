package org.example.quiversync.data.repository

import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.GeminiPredictionDao
import org.example.quiversync.data.remote.api.GeminiApi
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.repository.GeminiRepository

class GeminiRepositoryImpl(
    private val api: GeminiApi,
    private val dao: GeminiPredictionDao
) : GeminiRepository {

    override suspend fun generateSingleDayMatch(
        surfboards: List<Surfboard>,
        dailyForecast: DailyForecast,
        user: User
    ): Result<GeminiPrediction, TMDBError> {
        return try {
            when (val result = api.predictionForOneDay(surfboards, dailyForecast, user)) {
                is Result.Failure -> Result.Failure(result.error)
                is Result.Success -> {
                    val prediction = result.data
                    if (prediction != null) {
                        dao.insert(prediction)
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

    override suspend fun generateAndStoreWeeklyBestMatches(
        surfboards: List<Surfboard>,
        weeklyForecast: List<DailyForecast>,
        user: User
    ): Result<List<GeminiPrediction>, TMDBError> {
        return try {
            val predictions = mutableListOf<GeminiPrediction>()
            for (forecast in weeklyForecast) {
                when (val result = generateSingleDayMatch(surfboards, forecast, user)) {
                    is Result.Failure -> return Result.Failure(result.error)
                    is Result.Success -> result.data?.let { predictions.add(it) }
                }
            }
            Result.Success(predictions)
        } catch (e: Exception) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getBestBoardMatchForToday(
        date: String,
        latitude: Double,
        longitude: Double
    ): Result<GeminiPrediction, TMDBError> {
        return try {
            val match = dao.getMatch(date, latitude, longitude)
            if (match != null) Result.Success(match)
            else Result.Failure(TMDBError("No match found"))
        } catch (e: Exception) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getBestBoardMatchForToday(spot: FavoriteSpot): Result<GeminiPrediction, TMDBError> {
            val currentDate = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date
                .toString()
            val match = dao.getTopMatch(currentDate, spot.spotLatitude, spot.spotLongitude)
                ?: return Result.Failure(TMDBError("No match found for today at this spot"))
            return Result.Success(match)
    }

    override suspend fun getBestBoardMatchForWeek(
        latitude: Double,
        longitude: Double
    ): Result<List<GeminiPrediction>, TMDBError> {
        // Optional: You can implement logic to query matches by spot for the week
        TODO("Implement weekly fetch using DAO if needed")
    }

    override suspend fun clearAllPredictions(
        latitude: Double,
        longitude: Double
    ): Result<Unit, TMDBError> {
        return try {
            dao.deleteBySpot(latitude, latitude)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getPredictionsForToday(): Result<List<GeminiPrediction>, TMDBError> {
        return try {
            val currentDate = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date
                .toString()
            val predictions = dao.getPredictionsForToday(currentDate)
            if (predictions.isNotEmpty()) Result.Success(predictions)
            else Result.Failure(TMDBError("No predictions found for today"))
        } catch (e: Exception) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }
}
