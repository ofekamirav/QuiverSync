package org.example.quiversync.domain.usecase.quiver

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.repository.QuiverRepository

class AddBoardUseCase(
    private val quiverRepository: QuiverRepository,
) {
    suspend operator fun invoke(board: Surfboard) : Result<Boolean, Error> = quiverRepository.addSurfboard(board)
}