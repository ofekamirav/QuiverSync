package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.User
import org.example.quiversync.utils.Location

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun updateUserProfile(user: User): Result<Unit>
}