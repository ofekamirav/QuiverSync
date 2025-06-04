package org.example.quiversync.data.session

import platform.Foundation.NSUserDefaults

actual class SessionManager {

    private val defaults = NSUserDefaults.standardUserDefaults

    actual suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    actual suspend fun getUserId(): String? {
        return defaults.stringForKey("user_id")
    }

    actual suspend fun getToken(): String? {
        return defaults.stringForKey("token")
    }

    actual suspend fun getRefreshToken(): String? {
        return defaults.stringForKey("refresh_token")
    }

    actual suspend fun setSession(userId: String, token: String, refreshToken: String) {
        defaults.setObject(userId, forKey = "user_id")
        defaults.setObject(token, forKey = "token")
        defaults.setObject(refreshToken, forKey = "refresh_token")
    }

    actual suspend fun clearSession() {
        defaults.removeObjectForKey("user_id")
        defaults.removeObjectForKey("token")
        defaults.removeObjectForKey("refresh_token")
        defaults.removeObjectForKey("has_seen_welcome")
    }

    actual suspend fun hasSeenWelcome(): Boolean {
        return defaults.boolForKey("has_seen_welcome")
    }

    actual suspend fun setWelcomeSeen() {
        defaults.setBool(true, forKey = "has_seen_welcome")
    }
}