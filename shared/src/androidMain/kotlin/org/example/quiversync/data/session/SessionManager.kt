package org.example.quiversync.data.session

import android.content.Context
import android.content.SharedPreferences

actual class SessionManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("quiverSync_session", Context.MODE_PRIVATE)

    actual suspend fun isLoggedIn(): Boolean {
        return prefs.getString("token", null) != null
    }

    actual suspend fun getUserId(): String? {
        return prefs.getString("user_id", null)
    }

    actual suspend fun getToken(): String? {
        return prefs.getString("token", null)
    }

    actual suspend fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    actual suspend fun setSession(userId: String, token: String, refreshToken: String) {
        prefs.edit()
            .putString("user_id", userId)
            .putString("token", token)
            .putString("refresh_token", refreshToken)
            .apply()
    }

    actual suspend fun clearSession() {
        prefs.edit().clear().apply()
    }

    actual suspend fun hasSeenWelcome(): Boolean {
        return prefs.getBoolean("has_seen_welcome", false)
    }

    actual suspend fun setWelcomeSeen() {
        prefs.edit().putBoolean("has_seen_welcome", true).apply()
    }
}