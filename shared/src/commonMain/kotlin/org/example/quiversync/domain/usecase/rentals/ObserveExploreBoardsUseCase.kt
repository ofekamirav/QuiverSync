package org.example.quiversync.domain.usecase.rentals

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.repository.RentalsRepository

class ObserveExploreBoardsUseCase(
    private val repo: RentalsRepository
) {
    operator fun invoke(): Flow<List<Surfboard>> = repo.observeExploreBoards()
}