package org.example.quiversync.domain.usecase.rentals.exploreRentals

import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.RentalOffer
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.RentalRepository

class AddRentalOfferUseCase(
    private val rentalRepository: RentalRepository
) {
    suspend operator fun invoke (surfboard: Surfboard, rentalDetails: RentalPublishDetails, user : User): Result<RentalOffer, Error> {
        return rentalRepository.addRentalOffer(surfboard, rentalDetails, user)
    }
}