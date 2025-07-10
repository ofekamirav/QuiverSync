package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.User
import org.example.quiversync.utils.Location
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<Unit,Error>
    suspend fun getCurrentUser(): User?
    suspend fun login(email: String, password: String): Result<Unit,Error>
    suspend fun logout()
    suspend fun updateUserProfile(user: User): Result<Unit,Error>
    suspend fun isUserSignedInWithPassword(): Boolean
    suspend fun reauthenticate(password: String): Result<Unit, Error>
    suspend fun updatePassword(newPassword: String): Result<Unit, Error>
}