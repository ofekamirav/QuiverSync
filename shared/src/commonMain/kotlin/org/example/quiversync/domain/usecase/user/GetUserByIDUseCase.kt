package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.UserRepository

class GetUserByIDUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String) = userRepository.getUserById(userId)
}