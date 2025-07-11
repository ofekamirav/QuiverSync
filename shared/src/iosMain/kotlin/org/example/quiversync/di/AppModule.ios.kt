package org.example.quiversync.di

import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.example.quiversync.data.remote.cloudinary.ActualImageUploader
import org.example.quiversync.data.remote.cloudinary.ImageUploader
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.utils.IOSLocationProvider
import org.example.quiversync.utils.LocationProvider
import org.example.quiversync.data.local.dao.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    single<LocationProvider> { IOSLocationProvider() }
    single<ImageUploader> { ActualImageUploader(get()) }
    single { SessionManager(null) }
    single<SqlDriver> { DatabaseDriverFactory().createDriver() }



    //add all the viewmodels
    //viewModelOf(::QuiverViewModel)
}