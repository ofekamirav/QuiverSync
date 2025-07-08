package org.example.quiversync.domain.usecase.loginUseCases

import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error

class LoginUserUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit, Error> = authRepository.login(email, password)
}