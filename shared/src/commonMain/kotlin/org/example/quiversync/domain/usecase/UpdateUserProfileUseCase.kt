package org.example.quiversync.domain.usecase

import org.example.quiversync.domain.model.OnboardingProfileDetails
import org.example.quiversync.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class UpdateUserProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(details: OnboardingProfileDetails): Result<Unit> {

        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            return Result.failure(Exception("Cannot update profile: No user is logged in."))
        }
        val updatedUser = currentUser.copy(
            dateOfBirth = details.dateOfBirth,
            heightCm = details.heightCm.toIntOrNull(),
            weightKg = details.weightKg.toIntOrNull(),
            surfLevel = details.surfLevel
        )
        return authRepository.updateUserProfile(updatedUser)
    }
}