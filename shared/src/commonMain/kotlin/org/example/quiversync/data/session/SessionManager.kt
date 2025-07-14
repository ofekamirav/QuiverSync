package org.example.quiversync.data.session

import org.example.quiversync.utils.Location

expect class SessionManager(context: Any?) {
    suspend fun clearUserData();

    suspend fun getUid(): String?
    suspend fun setUid(uid: String)

    suspend fun getLatitude(): Double?
    suspend fun setLatitude(latitude: Double)
    suspend fun getLongitude(): Double?
    suspend fun setLongitude(longitude: Double)
    suspend fun getLastLocation(): Location?
    suspend fun setLastLocation(location: Location)
    suspend fun setLastRefresh()
    suspend fun getLastRefresh(): String?

}

