package org.example.quiversync.data.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.utils.Location
import platform.Foundation.NSUserDefaults

actual class SessionManager actual constructor(context: Any?) {

    private val defaults = NSUserDefaults.standardUserDefaults

    private val _unitsPreferenceFlow = MutableStateFlow(
        defaults.stringForKey("units") ?: "metric"
    )

    private val _uidFlow = MutableStateFlow<String?>(defaults.stringForKey("uid"))

    actual fun observeUid(): Flow<String?> {
        return _uidFlow.asStateFlow()
    }

    actual suspend fun setUid(uid: String) {
        defaults.setObject(uid, forKey = "uid")
        _uidFlow.value = uid // ✅ FIX: Update the flow to notify observers
    }

    actual suspend fun clearUserData(){
        defaults.removeObjectForKey("uid")
        defaults.removeObjectForKey("latitude")
        defaults.removeObjectForKey("longitude")
        defaults.removeObjectForKey("lastRefresh")
        _uidFlow.value = null // ✅ FIX: Notify observers of logout
    }

    actual suspend fun getUid(): String? {
        // The value from the flow is the most current source of truth
        return _uidFlow.value
    }

    actual suspend fun getLatitude(): Double? {
        return defaults.doubleForKey("latitude").takeIf { it != 0.0 }
    }

    actual suspend fun setLatitude(latitude: Double) {
        defaults.setDouble(latitude, forKey = "latitude")
    }

    actual suspend fun getLongitude(): Double? {
        return defaults.doubleForKey("longitude").takeIf { it != 0.0 }
    }

    actual suspend fun setLongitude(longitude: Double) {
        defaults.setDouble(longitude, forKey = "longitude")
    }

    actual suspend fun getLastLocation(): Location? {
        val latitude = getLatitude() ?: return null
        val longitude = getLongitude() ?: return null
        return Location(latitude, longitude)
    }

    actual suspend fun setLastLocation(location: Location) {
        setLatitude(location.latitude)
        setLongitude(location.longitude)
    }

    actual suspend fun setLastRefresh() {
        val date = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            .date
            .toString()
        return defaults.setObject(date, forKey = "lastRefresh")
    }

    actual suspend fun getLastRefresh(): String? {
        return defaults.stringForKey("lastRefresh")
    }

    actual suspend fun getUnitsPreference(): String? {
        return defaults.stringForKey("units") ?: "metric"
    }

    actual suspend fun setUnitsPreference(units: String) {
        defaults.setObject(units, forKey = "units")
        _unitsPreferenceFlow.value = units
    }

    actual suspend fun isOnboardingComplete(): Boolean {
        return defaults.boolForKey("onboardingProcessComplete")
    }

    actual suspend fun setOnboardingComplete(complete: Boolean) {
        defaults.setObject(complete, forKey = "onboardingProcessComplete")
    }

    actual fun getUnitsPreferenceFlow(): Flow<String> {
        return _unitsPreferenceFlow.asStateFlow()
    }


}