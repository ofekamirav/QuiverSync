package org.example.quiversync.data.session

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.utils.Location

expect class SessionManager(context: Any?) {
    suspend fun clearUserData();

    suspend fun getUid(): String?
    suspend fun setUid(uid: String)
    fun observeUid(): Flow<String?>

    suspend fun getLatitude(): Double?
    suspend fun setLatitude(latitude: Double)
    suspend fun getLongitude(): Double?
    suspend fun setLongitude(longitude: Double)
    suspend fun getLastLocation(): Location?
    suspend fun setLastLocation(location: Location)
    suspend fun setLastRefresh()
    suspend fun getLastRefresh(): String?
    suspend fun getUnitsPreference(): String?
    suspend fun setUnitsPreference(units: String)

    suspend fun isOnboardingComplete(): Boolean
    suspend fun setOnboardingComplete(complete: Boolean)
    fun getUnitsPreferenceFlow(): Flow<String>
}

