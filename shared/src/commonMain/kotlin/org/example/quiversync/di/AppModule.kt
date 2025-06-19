package org.example.quiversync.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json


fun initKoin(config: KoinAppDeclaration? = null) {
   startKoin {
      config?.invoke(this)
      modules(appModules())
   }
}

//IOS
fun initKoin() = initKoin { }

//Common App Definitions
fun appModules() = listOf(commonModule, platformModule)

expect val platformModule: Module

val commonModule= module {
   singleOf(::createJson)
   //singleOf(QuiverRepository).bind<QuiverRepository>()
   //add here all the repositories

   //add all the viewmodels
   //viewModelOf(::QuiverViewModel)
   single { createHttpClient(get(), get()) }
}

fun createJson(): Json = Json {
   ignoreUnknownKeys = true
   prettyPrint = true
   isLenient = true
}

fun createHttpClient(clientEngine: HttpClientEngine, json: Json) = HttpClient(clientEngine) {
   install(Logging) {
      level = LogLevel.ALL
      logger = Logger.DEFAULT
   }
   install(ContentNegotiation) {
      json(json)
   }
}