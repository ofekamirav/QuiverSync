package org.example.quiversync.domain.usecase

import org.example.quiversync.domain.repository.QuiverRepository

class GetMyQuiverUseCase(
    private val quiverRepository: QuiverRepository
) {
    suspend operator fun invoke() = quiverRepository.getMyQuiver()
}