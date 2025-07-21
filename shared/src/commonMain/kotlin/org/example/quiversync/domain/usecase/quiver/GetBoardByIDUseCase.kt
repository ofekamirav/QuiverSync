package org.example.quiversync.domain.usecase.quiver

import org.example.quiversync.domain.repository.QuiverRepository

class GetBoardByIDUseCase (
    private val quiverRepository: QuiverRepository
){
    suspend operator fun invoke(boardId: String) = quiverRepository.getBoardById(boardId)
}