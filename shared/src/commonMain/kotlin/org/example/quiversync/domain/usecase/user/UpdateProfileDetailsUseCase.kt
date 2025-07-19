package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.features.user.edit_user.EditUserFormData
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.AuthError


class UpdateProfileDetailsUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(details: EditUserFormData): Result<Unit, Error> {

        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            return Result.Failure(AuthError("Cannot update profile: No user is logged in."))
        }
        val updatedUser = currentUser.copy(
            name = details.name ?: currentUser.name,
            heightCm = details.height,
            weightKg = details.weight,
            surfLevel = details.surfLevel,
            profilePicture = details.profilePicture ?: currentUser.profilePicture,
            phoneNumber = details.phoneNumber ?: currentUser.phoneNumber,
        )
        return authRepository.updateUserProfile(updatedUser)
    }
}