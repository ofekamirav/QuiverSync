package org.example.quiversync.data.session

expect class SessionManager {
    suspend fun clearAll()
    suspend fun hasSeenWelcome(): Boolean
    suspend fun setWelcomeSeen()

    suspend fun getUid(): String?
    suspend fun setUid(uid: String)

    suspend fun getLatitude(): Double?
    suspend fun setLatitude(latitude: Double)
    suspend fun getLongitude(): Double?
    suspend fun setLongitude(longitude: Double)
    suspend fun getLastLocation(): Location?
    suspend fun setLastLocation(location: Location)
}

