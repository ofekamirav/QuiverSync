package org.example.quiversync.domain.usecase.loginUseCases

import org.example.quiversync.data.remote.dto.AuthResult
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.repository.AuthRepository

class SignInWithGoogleUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<AuthResult, Error> = authRepository.signInWithGoogle(idToken)
}