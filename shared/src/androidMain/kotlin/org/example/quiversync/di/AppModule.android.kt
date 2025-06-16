package org.example.quiversync.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    //GPS Sensor maybe also here
    //Dao and ROOM here

    //add all the viewmodels
    //viewModelOf(::QuiverViewModel)
}