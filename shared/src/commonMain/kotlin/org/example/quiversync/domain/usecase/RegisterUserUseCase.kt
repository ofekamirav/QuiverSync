package org.example.quiversync.domain.usecase
import org.example.quiversync.domain.repository.AuthRepository

class RegisterUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Unit> {
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters long."))
        }
        return authRepository.register(name = name, email = email, password = password)
    }
}