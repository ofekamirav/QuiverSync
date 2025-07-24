package org.example.quiversync.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.OwnersDao
import org.example.quiversync.domain.model.OwnerLocal
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.UserRepository

class GetOwnerByIdUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Flow<Result<OwnerLocal, Error>> = userRepository.getOwnerById(userId)
}