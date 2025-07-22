package org.example.quiversync.data.remote.datasource.user

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.User

interface UserRemoteSource {
    fun observeUserProfile(uid: String): Flow<User?>
    suspend fun updateUserProfile(user: User): Result<Unit, Error>
    suspend fun getUserById(userId: String): Result<User, Error>
}