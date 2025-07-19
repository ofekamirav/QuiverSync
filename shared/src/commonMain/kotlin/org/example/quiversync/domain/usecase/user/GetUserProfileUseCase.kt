package org.example.quiversync.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.data.local.Result

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Result<User, Error>> = userRepository.getUserProfile()
}