package org.example.quiversync.domain.usecase.register

import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.OnboardingProfileDetails
import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.AuthError


class UpdateUserProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(details: OnboardingProfileDetails): Result<Unit, Error> {

        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            return Result.Failure(AuthError("Cannot update profile: No user is logged in."))
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