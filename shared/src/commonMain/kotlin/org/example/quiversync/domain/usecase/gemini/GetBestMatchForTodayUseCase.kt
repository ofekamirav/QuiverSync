package org.example.quiversync.domain.usecase.gemini

import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.repository.GeminiRepository

class GetBestMatchForTodayUseCase(
    private val geminiRepository: GeminiRepository
) {
    suspend operator fun invoke(
        spot : FavoriteSpot
    ) = geminiRepository.getBestBoardMatchForToday(spot)
}