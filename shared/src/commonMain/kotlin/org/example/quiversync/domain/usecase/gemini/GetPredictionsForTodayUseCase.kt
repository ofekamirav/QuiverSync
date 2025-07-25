package org.example.quiversync.domain.usecase.gemini

import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.domain.repository.GeminiRepository

class GetPredictionsForTodayUseCase(
    private val geminiRepository: GeminiRepository
) {
    suspend operator fun invoke(): Result<List<GeminiPrediction>, Error> {
        return geminiRepository.getPredictionsForToday()
    }
}