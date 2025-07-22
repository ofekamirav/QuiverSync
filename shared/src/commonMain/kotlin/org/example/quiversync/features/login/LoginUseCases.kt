package org.example.quiversync.features.login

import org.example.quiversync.domain.usecase.loginUseCases.LoginUserUseCase
import org.example.quiversync.domain.usecase.loginUseCases.SignInWithAppleUseCase
import org.example.quiversync.domain.usecase.loginUseCases.SignInWithGoogleUseCase

data class LoginUseCases (
    val loginUser: LoginUserUseCase,
    val signInWithGoogle: SignInWithGoogleUseCase,
    val signInWithApple: SignInWithAppleUseCase
)