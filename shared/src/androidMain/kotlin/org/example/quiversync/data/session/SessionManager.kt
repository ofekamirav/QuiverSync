package org.example.quiversync.data.session

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.utils.Location

private val Context.dataStore by preferencesDataStore("quiver_sync_prefs")

actual class SessionManager actual constructor(context: Any?) {

    private val context = context as Context
    private val uidKey = stringPreferencesKey("uid")
    private val latitudeKey = doublePreferencesKey("latitude")
    private val longitudeKey = doublePreferencesKey("longitude")
    private val lastRefreshKey = stringPreferencesKey("lastRefresh")


    actual suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(uidKey)
            preferences.remove(latitudeKey)
            preferences.remove(longitudeKey)
        }
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

    actual suspend fun setLastRefresh() {
        val date = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            .date
            .toString()
        context.dataStore.edit { it[lastRefreshKey] = date }
    }

    actual suspend fun getLastRefresh(): String? {
        return context.dataStore.data.first()[lastRefreshKey]
    }

}
