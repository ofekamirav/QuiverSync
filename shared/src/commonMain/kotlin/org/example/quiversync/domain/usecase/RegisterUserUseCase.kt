package org.example.quiversync.domain.usecase
import org.example.quiversync.domain.repository.AuthRepository

class RegisterUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Unit> {
        return authRepository.register(name = name, email = email, password = password)
    }
}