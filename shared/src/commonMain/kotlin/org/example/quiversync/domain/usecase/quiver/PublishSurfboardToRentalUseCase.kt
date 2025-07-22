package org.example.quiversync.domain.usecase.quiver

import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.SurfboardError
import org.example.quiversync.utils.extensions.platformLogger

class PublishSurfboardToRentalUseCase(
    private val quiverRepository: QuiverRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(surfboardId: String, rentalDetails: RentalPublishDetails): Result<Boolean, Error> {
        val currentLatitude = sessionManager.getLatitude()
        val currentLongitude = sessionManager.getLongitude()

        if (currentLatitude == null || currentLongitude == null) {
            platformLogger("PublishSurfboardToRentalUseCase", "Unable to retrieve current location for surfboard $surfboardId")
            return Result.Failure(SurfboardError("Unable to retrieve current location"))
        }

        val updatedRentalDetails = rentalDetails.copy(latitude = currentLatitude, longitude = currentLongitude)

        return quiverRepository.publishForRental(surfboardId, updatedRentalDetails)
    }
}