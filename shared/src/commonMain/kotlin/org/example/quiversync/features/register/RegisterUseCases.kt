package org.example.quiversync.features.register

import org.example.quiversync.domain.usecase.register.RegisterUserUseCase
import org.example.quiversync.domain.usecase.register.UpdateUserProfileUseCase

data class RegisterUseCases(
    val registerUser: RegisterUserUseCase,
    val updateUserProfile : UpdateUserProfileUseCase
)