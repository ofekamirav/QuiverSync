package org.example.quiversync.domain.repository

import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.User
import org.example.quiversync.data.local.Result

interface UserRepository {
    suspend fun getUserProfile(): Result<User, Error>
    suspend fun updateUserProfile(user: User):Result<Unit, Error>
    suspend fun deleteProfileLocal(uid:String)
}