package org.example.quiversync.data.session

import platform.Foundation.NSUserDefaults

actual class SessionManager {

    private val defaults = NSUserDefaults.standardUserDefaults

    actual suspend fun hasSeenWelcome(): Boolean {
        return defaults.boolForKey("has_seen_welcome")
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
}