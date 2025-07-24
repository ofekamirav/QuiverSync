package org.example.quiversync.domain.usecase.rentals

import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.repository.RentalsRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error

class FetchExplorePageUseCase(
    private val repo: RentalsRepository
) {
    suspend operator fun invoke(pageSize: Int, lastId: String?): Result<List<Surfboard>, Error> =
        repo.fetchExplorePage(pageSize, lastId)
}