package org.example.quiversync.data.session

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("quiver_sync_prefs")

actual class SessionManager(private val context: Context) {

    private val uidKey = stringPreferencesKey("uid")
    private val welcomeKey = booleanPreferencesKey("has_seen_welcome")

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
}
