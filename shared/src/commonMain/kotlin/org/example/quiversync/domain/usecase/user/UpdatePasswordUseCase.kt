package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error

class UpdatePasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String, newPassword: String): Result<Unit, Error> {
        val reauthResult = authRepository.reauthenticate(currentPassword)
        if (reauthResult is Result.Failure) {
            return reauthResult
        }
        return authRepository.updatePassword(newPassword)

    }
}