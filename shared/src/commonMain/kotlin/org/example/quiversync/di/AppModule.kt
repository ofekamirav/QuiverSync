package org.example.quiversync.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.IO
import org.example.quiversync.QuiverSyncDatabase
import org.example.quiversync.data.local.dao.FavSpotDao
import org.example.quiversync.data.local.dao.GeminiPredictionDao
import org.example.quiversync.data.local.dao.OwnersDao
import org.example.quiversync.data.local.dao.QuiverDao
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.remote.api.GeminiApi
import org.example.quiversync.data.remote.api.StormGlassApi
import org.example.quiversync.data.remote.datasource.favSpot.FavSpotRemoteSource
import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSource
import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSourceService
import org.example.quiversync.data.remote.datasource.favSpot.FavSpotRemoteSourceService
import org.example.quiversync.data.repository.AuthRepositoryImpl
import org.example.quiversync.data.repository.FavSpotRepositoryImpl
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.data.repository.ForecastRepositoryImpl
import org.example.quiversync.data.repository.GeminiRepositoryImpl
import org.example.quiversync.data.repository.QuiverRepositoryImpl
import org.example.quiversync.data.repository.UserRepositoryImpl
import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.domain.repository.FavSpotRepository
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.domain.repository.GeminiRepository
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastByLocationUseCase
import org.example.quiversync.domain.usecase.forecast.GetWeeklyForecastBySpotUseCase
import org.example.quiversync.domain.usecase.register.RegisterUserUseCase
import org.example.quiversync.domain.usecase.register.UpdateUserProfileUseCase
import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.domain.usecase.favSpots.AddFavSpot
import org.example.quiversync.domain.usecase.favSpots.ClearAllFavSpots
import org.example.quiversync.domain.usecase.favSpots.GetAllFavUserSpots
import org.example.quiversync.domain.usecase.favSpots.RemoveFavSpot
import org.example.quiversync.domain.usecase.forecast.DeleteBySpot
import org.example.quiversync.domain.usecase.forecast.DeleteOutDateForecastUseCase
import org.example.quiversync.domain.usecase.forecast.GetDailyForecast
import org.example.quiversync.domain.usecase.gemini.DeleteAllPredictionsBySpotUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateAllTodayPredictionsUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateWeeklyPredictionsUseCase
import org.example.quiversync.domain.usecase.gemini.GenerateSingleDayMatchUseCase
import org.example.quiversync.domain.usecase.loginUseCases.LoginUserUseCase
import org.example.quiversync.domain.usecase.loginUseCases.SignInWithGoogleUseCase
import org.example.quiversync.domain.usecase.quiver.AddBoardUseCase
import org.example.quiversync.domain.usecase.quiver.DeleteSurfboardUseCase
import org.example.quiversync.domain.usecase.quiver.GetMyQuiverUseCase
import org.example.quiversync.domain.usecase.quiver.PublishSurfboardToRentalUseCase
import org.example.quiversync.domain.usecase.quiver.SetSurfboardAsAvailableForRental
import org.example.quiversync.domain.usecase.quiver.SetSurfboardAsUnavailableUseCase
import org.example.quiversync.domain.usecase.quiver.UnpublishSurfboardFromRentalUseCase
import org.example.quiversync.domain.usecase.user.CheckUserAuthMethodUseCase
import org.example.quiversync.domain.usecase.user.GetBoardsNumberUseCase
import org.example.quiversync.domain.usecase.user.GetSpotsNumberUseCase
import org.example.quiversync.features.home.HomeViewModel
import org.example.quiversync.features.login.LoginViewModel
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase
import org.example.quiversync.domain.usecase.user.IsImperialUnitsUseCase
import org.example.quiversync.domain.usecase.user.LogoutUseCase
import org.example.quiversync.domain.usecase.user.SendPasswordResetEmailUseCase
import org.example.quiversync.domain.usecase.user.UpdatePasswordUseCase
import org.example.quiversync.domain.usecase.user.UpdateProfileDetailsUseCase
import org.example.quiversync.features.home.HomeUseCases
import org.example.quiversync.features.login.forgot_password.ForgotPasswordViewModel
import org.example.quiversync.features.quiver.QuiverUseCases
import org.example.quiversync.features.quiver.QuiverViewModel
import org.example.quiversync.features.quiver.add_board.AddBoardViewModel
import org.example.quiversync.features.register.OnboardingViewModel
import org.example.quiversync.features.register.RegisterUseCases
import org.example.quiversync.features.register.RegisterViewModel
import org.example.quiversync.features.settings.SecurityAndPrivacyViewModel
import org.example.quiversync.features.settings.SettingsViewModel
import org.example.quiversync.features.spots.add_fav_spot.AddFavSpotViewModel
import org.example.quiversync.features.spots.fav_spot_main_page.FavSpotsViewModel
import org.example.quiversync.features.spots.FavSpotsUseCases
import org.example.quiversync.features.user.UserUseCases
import org.example.quiversync.features.user.UserViewModel
import org.example.quiversync.features.user.edit_user.EditProfileDetailsViewModel
import org.example.quiversync.data.remote.datasource.user.UserRemoteSource
import org.example.quiversync.data.remote.datasource.user.UserRemoteSourceService
import org.example.quiversync.data.repository.RentalsRepositoryImpl
import org.example.quiversync.domain.repository.RentalsRepository
import org.example.quiversync.domain.usecase.loginUseCases.SignInWithAppleUseCase
import org.example.quiversync.domain.usecase.rentals.FetchExplorePageUseCase
import org.example.quiversync.domain.usecase.rentals.ObserveExploreBoardsUseCase
import org.example.quiversync.domain.usecase.rentals.StartRemoteSyncUseCase
import org.example.quiversync.domain.usecase.rentals.StopRemoteSyncUseCase
import org.example.quiversync.domain.usecase.user.GetOwnerByIdUseCase
import org.example.quiversync.domain.usecase.user.GetRentalsNumberUseCase
import org.example.quiversync.domain.usecase.user.StartSyncsUseCase
import org.example.quiversync.features.login.LoginUseCases
import org.example.quiversync.features.rentals.RentalsUseCases
import org.example.quiversync.features.rentals.explore.ExploreViewModel


fun initKoin(config: KoinAppDeclaration? = null) {
   startKoin {
      config?.invoke(this)
      modules(appModules())
   }
}

//IOS
fun initKoin() = initKoin { }

//Common App Definitions
fun appModules() = listOf(commonModule, platformModule, coroutineModule)

expect val platformModule: Module

val coroutineModule = module {
    single<CoroutineScope> {
        CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO)
    }
}

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


   //-----------------------------------------------------Repositories---------------------------------------------
   single<AuthRepository> { AuthRepositoryImpl(get(), get(), get() , get(), get(), get(), get()) }
   single<FavSpotRepository>{FavSpotRepositoryImpl(get(), get(), get(), get())}
   single<GeminiRepository>{GeminiRepositoryImpl(get(), get(), get() , get())}
   single<ForecastRepository> { ForecastRepositoryImpl(get(), get(), get() , get()) }
   single<UserRepository> { UserRepositoryImpl(get(), get(),get(), get(), get()) }
   single<QuiverRepository> { QuiverRepositoryImpl(get(), get(), get(), get()) }
   single<RentalsRepository> { RentalsRepositoryImpl(get(), get(), get(), get()) }

   //----------------------------------------------------- Firebase- Remote Data Source----------------------------------
   single<QuiverRemoteDataSource>{ QuiverRemoteDataSourceService(get()) }
   single<FavSpotRemoteSource> { FavSpotRemoteSourceService(get()) }
   single<UserRemoteSource> { UserRemoteSourceService(get()) }


   //---------------------------------------------------SqlDelight + Dao----------------------------------------
   single { QuiverSyncDatabase(get()) }
   single { get<QuiverSyncDatabase>().dailyForecastQueries }
   single { get<QuiverSyncDatabase>().geminiPredictionQueries }
   single { get<QuiverSyncDatabase>().userProfileQueries }
   single { get<QuiverSyncDatabase>().surfboardQueries }
   single { get<QuiverSyncDatabase>().favSpotQueries }
   single { get<QuiverSyncDatabase>().ownersQueries }
   single { UserDao(queries = get()) }
   single { QuiverDao(queries  = get()) }
   single {GeminiPredictionDao(queries  = get())}
   single { FavSpotDao(queries = get()) }
   single { OwnersDao(queries = get()) }


   //---------------------------------------------------UseCases--------------------------------------------
   //User UseCases
    single { RegisterUserUseCase(get()) }
    single { UpdateUserProfileUseCase(get()) }
    single { UploadImageUseCase(get()) }
    single { UpdateProfileDetailsUseCase(get()) }
    single { GetWeeklyForecastByLocationUseCase(get(),get(), get()) }
    single { GetWeeklyForecastBySpotUseCase(get()) }
    single { GetUserProfileUseCase(get()) }
    single { LogoutUseCase(get(), get(), get(), get(), get()) }
    single { LoginUserUseCase(get()) }
    single { CheckUserAuthMethodUseCase(get()) }
    single { UpdatePasswordUseCase(get()) }
    single { SendPasswordResetEmailUseCase(get()) }
    single { SignInWithGoogleUseCase(get()) }
    single { GetSpotsNumberUseCase(get()) }
    single { IsImperialUnitsUseCase(get()) }
    single { GetOwnerByIdUseCase(get()) }
    single { SignInWithAppleUseCase(get()) }
    single { GetRentalsNumberUseCase(get()) }
    single { StartSyncsUseCase(get(),get(),get()) }

   //Quiver UseCases
   single { AddBoardUseCase(get()) }
   single { GetMyQuiverUseCase(get()) }
   single { SetSurfboardAsAvailableForRental(get()) }
   single { SetSurfboardAsUnavailableUseCase(get()) }
   single { PublishSurfboardToRentalUseCase(get(), get()) }
   single { DeleteSurfboardUseCase(get()) }
   single { UnpublishSurfboardFromRentalUseCase(get()) }
   single { GetBoardsNumberUseCase(get()) }

   // FavSpot UseCases
   single { AddFavSpot(get()) }
   single { ClearAllFavSpots(get()) }
   single { GetAllFavUserSpots(get()) }
   single { RemoveFavSpot(get()) }

   // Forecast UseCases
   single { DeleteOutDateForecastUseCase(get()) }
   single { GetDailyForecast(get()) }
   single { DeleteBySpot(get()) }

   // Gemini UseCases
   single { GenerateWeeklyPredictionsUseCase(get()) }
   single { GenerateSingleDayMatchUseCase(get()) }
   single { DeleteAllPredictionsBySpotUseCase(get()) }
   single { GenerateAllTodayPredictionsUseCase(get()) }
//   single { GetPredictionsForTodayUseCase(get()) }
    //Rentals UseCases
    single { FetchExplorePageUseCase(get()) }
    single { ObserveExploreBoardsUseCase(get()) }
    single { StopRemoteSyncUseCase(get()) }
    single { StartRemoteSyncUseCase(get()) }


   //Data Classes for UseCases
   single{
      HomeUseCases(
            getWeeklyForecastByLocationUseCase = get(),
            getQuiverUseCase = get(),
            getDailyPrediction = get(),
            getUser = get(),
            isImperialUnitsUseCases = get()
      )
   }
   single {
      RegisterUseCases(
         registerUser = get(),
         updateUserProfile = get()
      )
   }

   single{
       LoginUseCases(
           loginUser = get(),
           signInWithGoogle = get(),
           signInWithApple = get()
       )

   }
    single {
        RentalsUseCases(
            observeExploreBoards = get(),
            fetchExplorePage = get(),
            stopRemoteSync = get(),
            startRemoteSync = get()
        )
    }
    single{
      UserUseCases(
          getUserProfileUseCase = get(),
          updateUserProfileUseCase = get(),
          logoutUseCase = get(),
          getBoardsNumberUseCase = get(),
          uploadImageUseCase = get(),
          updateProfileDetailsUseCase = get(),
          checkUserAuthMethod = get(),
          updatePasswordUseCase = get(),
          sendPasswordResetEmailUseCase = get(),
          getSpotsNumberUseCase = get(),
          getRentalsNumberUseCase = get()
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

   // FavSpots UseCases Collection
   single {
      FavSpotsUseCases(

            // FavSpots UseCases
            deleteOutDateForecastUseCase = get(),
            getAllFavUserSpotsUseCase = get(),
            addFavSpotUseCase = get(),
            removeFavSpotsUseCase = get(),

            // Quiver&User UseCases
            getAllQuiverUseCase = get(),
            getUserProfileUseCase = get(),
            isImperialUnitsUseCase = get(),


            // Forecast UseCases
            getDailyForecast = get(),
            getWeeklyForecastBySpotUseCase = get(),
            deleteBySpotUseCase = get(),
            getUser = get(),

            // Gemini UseCases
            generateBestBoardForSingleDayUseCase = get(),
            generateWeeklyPredictions = get(),
            deleteAllPredictionsBySpotUseCase = get(),
            generateAllTodayPredictionsUseCase = get(),
      )
   }



   // --------------------------------------------ViewModels--------------------------------------------------
    factory { RegisterViewModel(get()) }
    factory { OnboardingViewModel(get(), get(),get()) }
    factory { UserViewModel(get()) }
    factory { HomeViewModel(get(),get()) }
    factory { LoginViewModel(get(),get()) }
    factory { OnboardingViewModel(get(), get(), get()) }
    factory { QuiverViewModel(get()) }
    factory { AddBoardViewModel(get(), get()) }
    factory { SettingsViewModel(get(), get()) }
    factory { EditProfileDetailsViewModel(get()) }
    factory { SecurityAndPrivacyViewModel(get()) }
    factory { FavSpotsViewModel(get(),get()) }
    factory { AddFavSpotViewModel(get())}
    factory { ForgotPasswordViewModel(get()) }
    factory { ExploreViewModel(get(), get()) }

}

fun createJson(): Json = Json {
   ignoreUnknownKeys = true
   prettyPrint = true
   isLenient = true
}

fun createHttpClient(clientEngine: HttpClientEngine, json: Json) = HttpClient(clientEngine) {
    install(HttpTimeout) {
        connectTimeoutMillis = 30_000
        socketTimeoutMillis  = 60_000
        requestTimeoutMillis = 60_000
    }
   install(Logging) {
      level = LogLevel.ALL
      logger = Logger.DEFAULT
   }
   install(ContentNegotiation) {
      json(json)
   }
}
