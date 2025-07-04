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
import org.example.quiversync.QuiverSyncDatabase
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.remote.api.GeminiApi
import org.example.quiversync.data.remote.api.StormGlassApi
import org.example.quiversync.data.repository.AuthRepositoryImpl
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.data.repository.ForecastRepositoryImpl
import org.example.quiversync.data.repository.UserRepositoryImpl
import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.domain.usecase.GetWeeklyForecastByLocationUseCase
import org.example.quiversync.domain.usecase.GetWeeklyForecastBySpotUseCase
import org.example.quiversync.domain.usecase.register.RegisterUserUseCase
import org.example.quiversync.domain.usecase.register.UpdateUserProfileUseCase
import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.features.home.HomeViewModel
import org.example.quiversync.features.login.LoginViewModel
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase
import org.example.quiversync.features.home.HomeUseCases
import org.example.quiversync.features.register.OnboardingViewModel
import org.example.quiversync.features.register.RegisterUseCases
import org.example.quiversync.features.register.RegisterViewModel
import org.example.quiversync.features.user.UserUseCases
import org.example.quiversync.features.user.UserViewModel


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
   //Utils
   single{ GeminiApi(get()) }
   single{ StormGlassApi(get()) }

   single<SessionManager> { SessionManager(get()) }


   //Repositories
   single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
   single<ForecastRepository> { ForecastRepositoryImpl(get(), get(), get()) }
   single<UserRepository> { UserRepositoryImpl(get(), get(),get()) }

   // Cloudinary

   //SqlDelight + Dao
   single { QuiverSyncDatabase(get()) }
   single { get<QuiverSyncDatabase>().dailyForecastQueries }
   single { get<QuiverSyncDatabase>().geminiMatchQueries }
   single { get<QuiverSyncDatabase>().userProfileQueries }
   single{ UserDao(get()) }




   //UseCases
   single { RegisterUserUseCase(get()) }
   single { UpdateUserProfileUseCase(get()) }
   single { UploadImageUseCase(get()) }
   single { GetWeeklyForecastByLocationUseCase(get(), get()) }
   single { GetWeeklyForecastBySpotUseCase(get()) }
   single { GetUserProfileUseCase(get()) }
   single{
      HomeUseCases(
          getWeeklyForecastByLocationUseCase = get()
      )
   }
   single {
      RegisterUseCases(
         registerUser = get(),
         updateUserProfile = get()
      )
   }
   single{
      UserUseCases(
            getUserProfileUseCase = get(),
            updateUserProfileUseCase = get()
      )
   }


   // ViewModels
   single { RegisterViewModel(get()) }
   single { OnboardingViewModel(get(), get()) }
   single { UserViewModel(get()) }
   single { HomeViewModel(get()) }
   single { LoginViewModel(get()) }

   single { OnboardingViewModel(get(), get()) }
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