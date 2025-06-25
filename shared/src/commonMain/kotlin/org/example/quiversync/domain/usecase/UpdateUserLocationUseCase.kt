package org.example.quiversync.domain.usecase

import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.utils.LocationProvider

class UpdateUserLocationUseCase(
    private val authRepository: AuthRepository,
    private val locationProvider: LocationProvider
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            val currentLocation = locationProvider.getCurrentLocation()

            if (currentLocation != null) {
                authRepository.updateUserLocation(currentLocation)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}