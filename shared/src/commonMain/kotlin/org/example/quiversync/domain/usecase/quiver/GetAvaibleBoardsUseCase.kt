package org.example.quiversync.domain.usecase.quiver

import org.example.quiversync.domain.repository.QuiverRepository

class GetAvaibleBoardsUseCase(
    private val quiverRepository: QuiverRepository
) {
    suspend operator fun invoke(userId: String) = quiverRepository.getAvailableSurfboards(userId)
}