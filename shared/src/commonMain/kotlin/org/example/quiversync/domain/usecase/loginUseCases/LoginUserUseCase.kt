package org.example.quiversync.domain.usecase.loginUseCases

import org.example.quiversync.domain.repository.AuthRepository

class LoginUserUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.login(email = email, password = password)
    }
}