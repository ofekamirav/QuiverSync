package org.example.quiversync.data.session

expect class SessionManager {
    suspend fun isLoggedIn(): Boolean
    suspend fun getUserId(): String?
    suspend fun getToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun setSession(userId: String, token: String, refreshToken: String)
    suspend fun clearSession()
    suspend fun hasSeenWelcome(): Boolean
    suspend fun setWelcomeSeen()
}

