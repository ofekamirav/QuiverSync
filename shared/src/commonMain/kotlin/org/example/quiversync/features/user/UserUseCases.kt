package org.example.quiversync.features.user

import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase
import org.example.quiversync.domain.usecase.register.RegisterUserUseCase
import org.example.quiversync.domain.usecase.register.UpdateUserProfileUseCase

data class UserUseCases(
    val getUserProfileUseCase: GetUserProfileUseCase,
    val updateUserProfileUseCase: UpdateUserProfileUseCase,
)
