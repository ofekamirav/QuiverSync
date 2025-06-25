package org.example.quiversync.data.session

expect class SessionManager {
    suspend fun clearAll()
    suspend fun hasSeenWelcome(): Boolean
    suspend fun setWelcomeSeen()

    suspend fun getUid(): String?
    suspend fun setUid(uid: String)
}

