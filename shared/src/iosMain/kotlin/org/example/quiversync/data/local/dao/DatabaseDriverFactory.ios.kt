package org.example.quiversync.data.local.dao

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import org.example.quiversync.QuiverSyncDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // Create database configuration with schema creation and version management
        val schema = QuiverSyncDatabase.Schema

        println("📁 Creating new quiversync.db with schema version ${schema.version}")


        val configuration = DatabaseConfiguration(
            name = "quiversync.db",
            version = schema.version.toInt(),
            create = { connection ->
                wrapConnection(connection) { schema.create(it) }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { schema.migrate(it, oldVersion.toLong(), newVersion.toLong()) }
            }
        )

        return NativeSqliteDriver(configuration)
    }
}