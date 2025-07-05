package org.example.quiversync.data.session

import org.example.quiversync.utils.Location
import platform.Foundation.NSUserDefaults

actual class SessionManager actual constructor(context: Any?) {

    private val defaults = NSUserDefaults.standardUserDefaults

    actual suspend fun hasSeenWelcome(): Boolean {
        return defaults.boolForKey("has_seen_welcome")
    }

    actual suspend fun clearUserData(){
        defaults.removeObjectForKey("uid")
        defaults.removeObjectForKey("latitude")
        defaults.removeObjectForKey("longitude")
    }

    actual suspend fun setWelcomeSeen() {
        defaults.setBool(true, forKey = "has_seen_welcome")
    }

    actual suspend fun clearAll() {
        defaults.removeObjectForKey("has_seen_welcome")
    }

    actual suspend fun getUid(): String? {
        return defaults.stringForKey("uid")
    }

    actual suspend fun setUid(uid: String) {
        defaults.setObject(uid, forKey = "uid")
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
}