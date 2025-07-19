package org.example.quiversync.domain.usecase.gemini

import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.domain.repository.GeminiRepository

class GenerateAllTodayPredictionsUseCase(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(user:User, surfboards:List<Surfboard>, forecasts:List<DailyForecast>) : Result<List<GeminiPrediction>, Error> {
        return repository.generatePredictionsForTodayAllSpots(
            user = user,
            surfboards = surfboards,
            dailyForecasts = forecasts
        )
    }
}