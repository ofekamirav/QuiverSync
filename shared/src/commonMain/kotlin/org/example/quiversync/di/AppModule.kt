package org.example.quiversync.di

import app.cash.sqldelight.db.SqlDriver
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
import org.example.quiversync.data.local.dao.DatabaseDriverFactory
import org.example.quiversync.data.local.dao.QuiverDao
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.remote.api.GeminiApi
import org.example.quiversync.data.remote.api.StormGlassApi
import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSource
import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSourceService
import org.example.quiversync.data.repository.AuthRepositoryImpl
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.data.repository.ForecastRepositoryImpl
import org.example.quiversync.data.repository.QuiverRepositoryImpl
import org.example.quiversync.data.repository.UserRepositoryImpl
import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.domain.usecase.GetWeeklyForecastByLocationUseCase
import org.example.quiversync.domain.usecase.GetWeeklyForecastBySpotUseCase
import org.example.quiversync.domain.usecase.register.RegisterUserUseCase
import org.example.quiversync.domain.usecase.register.UpdateUserProfileUseCase
import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.domain.usecase.loginUseCases.LoginUserUseCase
import org.example.quiversync.domain.usecase.quiver.AddBoardUseCase
import org.example.quiversync.domain.usecase.quiver.DeleteSurfboardUseCase
import org.example.quiversync.domain.usecase.quiver.GetMyQuiverUseCase
import org.example.quiversync.domain.usecase.quiver.PublishSurfboardToRentalUseCase
import org.example.quiversync.domain.usecase.quiver.SetSurfboardAsAvailableForRental
import org.example.quiversync.domain.usecase.quiver.SetSurfboardAsUnavailableUseCase
import org.example.quiversync.domain.usecase.quiver.UnpublishSurfboardFromRentalUseCase
import org.example.quiversync.domain.usecase.user.GetBoardsNumberUseCase
import org.example.quiversync.features.home.HomeViewModel
import org.example.quiversync.features.login.LoginViewModel
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase
import org.example.quiversync.domain.usecase.user.LogoutUseCase
import org.example.quiversync.features.home.HomeUseCases
import org.example.quiversync.features.quiver.BoardEventBus
import org.example.quiversync.features.quiver.QuiverUseCases
import org.example.quiversync.features.quiver.QuiverViewModel
import org.example.quiversync.features.quiver.add_board.AddBoardViewModel
import org.example.quiversync.features.register.OnboardingViewModel
import org.example.quiversync.features.register.RegisterUseCases
import org.example.quiversync.features.register.RegisterViewModel
import org.example.quiversync.features.user.UserUseCases
import org.example.quiversync.features.user.UserViewModel
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
   //Utils
   single{ GeminiApi(get()) }
   single{ StormGlassApi(get()) }

   single<SessionManager> { SessionManager(get()) }

   // SharedFlow / Event Bus
   single { BoardEventBus }


   //-----------------------------------------------------Repositories---------------------------------------------
   single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
   single<ForecastRepository> { ForecastRepositoryImpl(get(), get(), get()) }
   single<UserRepository> { UserRepositoryImpl(get(), get(),get()) }
   single<QuiverRepository> { QuiverRepositoryImpl(get(), get(), get()) }

   //----------------------------------------------------- Firebase- Remote Data Source----------------------------------
   single<QuiverRemoteDataSource>{ QuiverRemoteDataSourceService(get()) }

   //---------------------------------------------------SqlDelight + Dao----------------------------------------
   single { QuiverSyncDatabase(get()) }
   single { get<QuiverSyncDatabase>().dailyForecastQueries }
   single { get<QuiverSyncDatabase>().geminiMatchQueries }
   single { get<QuiverSyncDatabase>().userProfileQueries }
   single { get<QuiverSyncDatabase>().surfboardQueries }
   single { UserDao(get()) }
   single { QuiverDao(get()) }


   //---------------------------------------------------UseCases--------------------------------------------
   single { RegisterUserUseCase(get()) }
   single { UpdateUserProfileUseCase(get()) }
   single { UploadImageUseCase(get()) }
   single { GetWeeklyForecastByLocationUseCase(get(), get()) }
   single { GetWeeklyForecastBySpotUseCase(get()) }
   single { GetUserProfileUseCase(get()) }
   single { LogoutUseCase(get()) }
   single { LoginUserUseCase(get()) }
   //Quiver UseCases
   single { AddBoardUseCase(get()) }
   single { GetMyQuiverUseCase(get()) }
   single { SetSurfboardAsAvailableForRental(get()) }
   single { SetSurfboardAsUnavailableUseCase(get()) }
   single { PublishSurfboardToRentalUseCase(get(), get()) }
   single { DeleteSurfboardUseCase(get()) }
   single { UnpublishSurfboardFromRentalUseCase(get()) }
   single { GetBoardsNumberUseCase(get()) }
   //Data Classes for UseCases
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
            updateUserProfileUseCase = get(),
            logoutUseCase = get(),
            getBoardsNumberUseCase = get()
      )
   }
   single {
      QuiverUseCases(
          getMyQuiverUseCase = get(),
          deleteSurfboardUseCase = get(),
          addSurfboardUseCase = get(),
          publishSurfboardToRentalUseCase = get(),
          unpublishForRentalUseCase = get(),
      )
   }


   // --------------------------------------------ViewModels--------------------------------------------------
   single { RegisterViewModel(get()) }
   single { OnboardingViewModel(get(), get()) }
   single { UserViewModel(get(), get()) }
   single { HomeViewModel(get()) }
   single { LoginViewModel(get()) }
   single { OnboardingViewModel(get(), get()) }
   single { QuiverViewModel(get(),get()) }
   single { AddBoardViewModel(get(), get(), get()) }


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