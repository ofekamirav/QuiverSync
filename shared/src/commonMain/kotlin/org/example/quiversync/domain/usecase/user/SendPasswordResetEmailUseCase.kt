package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result

class SendPasswordResetEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit, Error> {
        return authRepository.sendPasswordResetEmail(email)
    }
}