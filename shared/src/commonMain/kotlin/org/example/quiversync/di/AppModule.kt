package org.example.quiversync.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
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
import org.example.quiversync.data.repository.AuthRepositoryImpl
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.domain.usecase.UpdateUserLocationUseCase
import org.example.quiversync.features.quiver.QuiverViewModel
import org.example.quiversync.features.register.OnboardingViewModel
import org.example.quiversync.features.register.RegisterUseCases
import org.example.quiversync.features.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf


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
   // Core
   singleOf(::createJson)
   single { createHttpClient(get(), get()) }
   //Firebase
   single<FirebaseAuth> { Firebase.auth }
   single<FirebaseFirestore> { Firebase.firestore }

   //Repositories
   single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }

   //UseCases
   single {
      RegisterUseCases(
         registerUser = get(),
         updateUserProfile = get()
      )
   }
   single { UpdateUserLocationUseCase(get(), get()) }

   // ViewModels
   single { RegisterViewModel(get()) }
   single { OnboardingViewModel(get()) }
   //single { QuiverViewModel(get()) }

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