package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.User

interface UserRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun updateUserProfile(user: User):Result<Unit>
    suspend fun deleteProfileLocal(uid:String)
}