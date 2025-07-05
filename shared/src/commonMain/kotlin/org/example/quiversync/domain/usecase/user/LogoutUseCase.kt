package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}