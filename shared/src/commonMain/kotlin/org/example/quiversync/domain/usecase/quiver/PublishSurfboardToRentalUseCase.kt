package org.example.quiversync.domain.usecase.quiver

import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.SurfboardError
import org.example.quiversync.utils.LocationProvider
import org.example.quiversync.utils.extensions.platformLogger

class PublishSurfboardToRentalUseCase(
    private val quiverRepository: QuiverRepository,
    private val locationProvider: LocationProvider
) {
    suspend operator fun invoke(surfboardId: String, rentalDetails: RentalPublishDetails): Result<Boolean, Error> {
        val currentLocation = locationProvider.getCurrentLocation()

        if (currentLocation == null) {
            platformLogger("PublishSurfboardToRentalUseCase", "Unable to retrieve current location for surfboard $surfboardId")
            return Result.Failure(SurfboardError("Unable to retrieve current location"))
        }

        val updatedRentalDetails = rentalDetails.copy(latitude = currentLocation.latitude, longitude = currentLocation.longitude)

        return quiverRepository.publishForRental(surfboardId, updatedRentalDetails)
    }
}