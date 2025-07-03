package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.GeminiMatch
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast

interface GeminiRepository {
    suspend fun generateAndStoreBestBoardMatch(boards: List<Surfboard>, forecast: DailyForecast, user: User): Result<GeminiMatch>
    suspend fun generateAndStoreWeeklyBestMatches(boards: List<Surfboard>, weeklyForecast: WeeklyForecast, user: User): Result<List<GeminiMatch>>
    suspend fun getBestBoardMatchForToday(
        userId: String,
        date: String,
        latitude: Double,
        longitude: Double
    ): GeminiMatch?
}