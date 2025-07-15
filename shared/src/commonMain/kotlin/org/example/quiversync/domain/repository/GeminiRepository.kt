package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.domain.model.Surfboard

interface GeminiRepository {
    suspend fun generateSingleDayMatch( surfboards: List<Surfboard> , dailyForecast: DailyForecast , user : User): Result<GeminiPrediction,TMDBError>

    suspend fun generateAndStoreWeeklyBestMatches(surfboards: List<Surfboard> , weeklyForecast: List<DailyForecast> ): Result<List<GeminiPrediction>, TMDBError>

    suspend fun getBestBoardMatchForWeek(
        latitude: Double,
        longitude: Double
    ): Result<List<GeminiPrediction>, TMDBError>

    suspend fun getPredictionsForToday() : Result<List<GeminiPrediction>, TMDBError>

    suspend fun deleteAllPredictionsBySpot(
        latitude: Double,
        longitude: Double
    ): Result<Unit, TMDBError>

    suspend fun generatePredictionsForTodayAllSpots(
        user: User,
        surfboards: List<Surfboard>,
        dailyForecasts: List<DailyForecast>
    ): Result<List<GeminiPrediction>, TMDBError>
}