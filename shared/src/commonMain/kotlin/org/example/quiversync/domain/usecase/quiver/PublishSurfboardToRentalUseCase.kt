package org.example.quiversync.domain.usecase.quiver

import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.utils.LocationProvider

class PublishSurfboardToRentalUseCase(
    private val quiverRepository: QuiverRepository,
    private val locationProvider: LocationProvider
) {
    suspend operator fun invoke(surfboardId: String, rentalDetails: RentalPublishDetails): Result<Boolean, Error> {
        val currentLocation = locationProvider.getCurrentLocation()

        val updatedRentalDetails = rentalDetails.copy(latitude = currentLocation?.latitude, longitude = currentLocation?.longitude)

        return quiverRepository.publishForRental(surfboardId, updatedRentalDetails)
    }
}