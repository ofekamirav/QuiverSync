package org.example.quiversync.domain.usecase.gemini

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.domain.repository.GeminiRepository

data class GetBestBoardForSingleDayUseCase(
    private val repository: GeminiRepository,
) {
    suspend operator fun invoke(surfboards: List<Surfboard>, dailyForecast: DailyForecast, user: User): Result<GeminiPrediction,TMDBError> {
        return repository.generateSingleDayMatch(
            surfboards = surfboards,
            dailyForecast = dailyForecast,
            user = user
        )
    }
}