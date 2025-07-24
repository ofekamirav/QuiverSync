package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.User
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.AuthResult

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<Unit,Error>
    suspend fun getCurrentUser(): User?
    suspend fun login(email: String, password: String): Result<Unit,Error>
    suspend fun logoutRemote()
    suspend fun clearLocalData()
    suspend fun updateUserProfile(user: User): Result<Unit,Error>
    suspend fun isUserSignedInWithPassword(): Boolean
    suspend fun reauthenticate(password: String): Result<Unit, Error>
    suspend fun updatePassword(newPassword: String): Result<Unit, Error>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit, Error>
    suspend fun signInWithGoogle(idToken: String): Result<AuthResult, Error>
    suspend fun signInWithApple(idToken: String): Result<AuthResult, Error>

}