package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.AuthRepository

class CheckUserAuthMethodUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = authRepository.isUserSignedInWithPassword()
}