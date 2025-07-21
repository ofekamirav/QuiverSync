package org.example.quiversync.domain.usecase.rentals.exploreRentals

import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.RentalRepository

class BuildRentalOfferUseCase(
    private val rentalRepository: RentalRepository
) {
    suspend operator fun invoke (surfboard: Surfboard, user: User) = rentalRepository.buildRentalOffer(surfboard, user)
}