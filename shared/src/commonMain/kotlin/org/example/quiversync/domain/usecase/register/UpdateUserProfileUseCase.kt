package org.example.quiversync.domain.usecase.register

import org.example.quiversync.domain.model.OnboardingProfileDetails
import org.example.quiversync.domain.repository.AuthRepository

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
            heightCm = details.heightCm,
            weightKg = details.weightKg,
            surfLevel = details.surfLevel,
            profilePicture = details.profilePicture
        )
        return authRepository.updateUserProfile(updatedUser)
    }
}