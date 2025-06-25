package org.example.quiversync.features.register

import org.example.quiversync.domain.usecase.RegisterUserUseCase
import org.example.quiversync.domain.usecase.UpdateUserProfileUseCase

data class RegisterUseCases(
    val registerUser: RegisterUserUseCase,
    val updateUserProfile : UpdateUserProfileUseCase
)