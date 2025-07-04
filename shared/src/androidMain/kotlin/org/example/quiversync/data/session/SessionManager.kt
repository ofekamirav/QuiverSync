package org.example.quiversync.data.session

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.example.quiversync.utils.Location

private val Context.dataStore by preferencesDataStore("quiver_sync_prefs")

actual class SessionManager actual constructor(context: Any?) {

    private val context = context as Context
    private val uidKey = stringPreferencesKey("uid")
    private val welcomeKey = booleanPreferencesKey("has_seen_welcome")
    private val latitudeKey = doublePreferencesKey("latitude")
    private val longitudeKey = doublePreferencesKey("longitude")

    private val context = context as Context

    actual suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    actual suspend fun hasSeenWelcome(): Boolean {
        return context.dataStore.data.first()[welcomeKey] ?: false
    }

    actual suspend fun setWelcomeSeen() {
        context.dataStore.edit { it[welcomeKey] = true }
    }

    actual suspend fun getUid(): String? {
        return context.dataStore.data.first()[uidKey]
    }

    actual suspend fun setUid(uid: String) {
        context.dataStore.edit { it[uidKey] = uid }
    }

    actual suspend fun getLatitude(): Double? {
        return context.dataStore.data.first()[latitudeKey]
    }

    actual suspend fun setLatitude(latitude: Double) {
        context.dataStore.edit { it[latitudeKey] = latitude }
    }

    actual suspend fun getLongitude(): Double? {
        return context.dataStore.data.first()[longitudeKey]
    }

    actual suspend fun setLongitude(longitude: Double) {
        context.dataStore.edit { it[longitudeKey] = longitude }
    }

    actual suspend fun getLastLocation(): Location? {
        val lat = getLatitude()
        val lon = getLongitude()
        return if (lat != null && lon != null) Location(lat, lon) else null
    }


    actual suspend fun setLastLocation(location: Location) {
        context.dataStore.edit {
            it[latitudeKey] = location.latitude
            it[longitudeKey] = location.longitude
        }
    }

}
