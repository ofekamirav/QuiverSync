package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.domain.model.Surfboard

interface GeminiRepository {
    suspend fun generateSingleDayMatch( surfboards: List<Surfboard> , dailyForecast: DailyForecast , user : User): Result<GeminiPrediction, Error>

    suspend fun generateAndStoreWeeklyBestMatches(surfboards: List<Surfboard> , weeklyForecast: List<DailyForecast> ): Result<List<GeminiPrediction>, Error>

    suspend fun getBestBoardMatchForWeek(
        latitude: Double,
        longitude: Double
    ): Result<List<GeminiPrediction>, Error>

    suspend fun getPredictionsForToday() : Result<List<GeminiPrediction>, Error>

    suspend fun deleteAllPredictionsBySpot(
        latitude: Double,
        longitude: Double
    ): Result<Unit, Error>

    suspend fun generatePredictionsForTodayAllSpots(
        user: User,
        surfboards: List<Surfboard>,
        dailyForecasts: List<DailyForecast>
    ): Result<List<GeminiPrediction>, Error>
}