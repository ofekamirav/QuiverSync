package org.example.quiversync.data.local.dao

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.example.quiversync.QuiverSyncDatabase


actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = QuiverSyncDatabase.Schema,
            name = "quiver_sync.db"
        )
    }
}