package org.example.quiversync.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import org.example.quiversync.data.local.dao.DatabaseDriverFactory
import org.example.quiversync.data.remote.cloudinary.ActualImageUploader
import org.example.quiversync.data.remote.cloudinary.ImageUploader
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.utils.AndroidLocationProvider
import org.example.quiversync.utils.LocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module


actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<ImageUploader> { ActualImageUploader(androidContext()) }
    single<LocationProvider> { AndroidLocationProvider(get<Context>()) }
    single { SessionManager(androidContext()) }
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }

    //GPS Sensor maybe also here
    //Dao and ROOM here

    //add all the viewmodels
    //viewModelOf(::QuiverViewModel)
}