package org.example.quiversync.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.UserRepository

class GetUserByIdUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Flow<Result<User, Error>> = userRepository.getUserById(userId)
}