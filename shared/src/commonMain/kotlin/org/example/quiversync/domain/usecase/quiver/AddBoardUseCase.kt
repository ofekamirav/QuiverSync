package org.example.quiversync.domain.usecase.quiver

import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.repository.QuiverRepository

class AddBoardUseCase(
    private val quiverRepository: QuiverRepository,
) {
    suspend operator fun invoke(board: Surfboard) = quiverRepository.addSurfboard(board)
}