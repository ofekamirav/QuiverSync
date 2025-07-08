package org.example.quiversync.domain.usecase.register

import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error

class RegisterUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Unit, Error> {
        return authRepository.register(name, email, password)
    }
}