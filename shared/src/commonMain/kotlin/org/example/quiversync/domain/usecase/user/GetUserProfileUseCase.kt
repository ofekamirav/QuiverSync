package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.UserRepository

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> = userRepository.getUserProfile()
}