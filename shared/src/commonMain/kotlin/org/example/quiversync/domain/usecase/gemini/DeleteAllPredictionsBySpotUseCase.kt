package org.example.quiversync.domain.usecase.gemini

import org.example.quiversync.domain.repository.GeminiRepository

data class DeleteAllPredictionsBySpotUseCase(
    private val repository: GeminiRepository,
) {
    suspend operator fun invoke(latitude : Double , longitude : Double) = repository.deleteAllPredictionsBySpot(
        latitude =latitude,
        longitude = longitude
    )
}