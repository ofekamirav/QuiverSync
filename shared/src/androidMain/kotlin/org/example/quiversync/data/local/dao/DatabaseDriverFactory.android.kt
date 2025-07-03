package org.example.quiversync.data.local.dao

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.example.quiversync.QuiverSyncDatabase

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = QuiverSyncDatabase.Schema,
            context = context,
            name = "quiver_sync.db"
        )
    }
}