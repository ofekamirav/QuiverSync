package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.Prediction.GeminiPrediction
import org.example.quiversync.domain.model.Surfboard

interface GeminiRepository {
    suspend fun generateAndStoreBestBoardMatch( surfboards: List<Surfboard> , dailyForecast: DailyForecast , user: User): Result<GeminiPrediction,TMDBError>
    suspend fun generateAndStoreWeeklyBestMatches(surfboards: List<Surfboard> , weeklyForecast: List<DailyForecast> , user: User ): Result<List<GeminiPrediction>, TMDBError>
    suspend fun getBestBoardMatchForToday(
        date: String,
        latitude: Double,
        longitude: Double
    ): Result<GeminiPrediction, TMDBError>
    suspend fun getBestBoardMatchForWeek(
        latitude: Double,
        longitude: Double
    ): Result<List<GeminiPrediction>, TMDBError>
    suspend fun clearAllPredictions(favoriteSpot: FavoriteSpot): Result<Unit, TMDBError>
}