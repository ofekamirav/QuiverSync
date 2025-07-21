package org.example.quiversync.domain.usecase.rentals.exploreRentals

import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.RentalOffer
import org.example.quiversync.domain.repository.RentalRepository

class GetRentalsBoardsUseCase(
    private val rentalRepository: RentalRepository
) {
    suspend operator fun invoke (): Result<List<RentalOffer>, Error> {
        return rentalRepository.getRentalsBoards()
    }
}