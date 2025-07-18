package org.example.quiversync.di

import org.example.quiversync.features.home.HomeViewModel
import org.example.quiversync.features.login.LoginViewModel
import org.example.quiversync.features.login.forgot_password.ForgotPasswordViewModel
import org.example.quiversync.features.quiver.QuiverViewModel
import org.example.quiversync.features.quiver.add_board.AddBoardViewModel
import org.example.quiversync.features.register.OnboardingViewModel
import org.example.quiversync.features.register.RegisterViewModel
import org.example.quiversync.features.settings.SecurityAndPrivacyViewModel
import org.example.quiversync.features.settings.SettingsViewModel
import org.example.quiversync.features.spots.add_fav_spot.AddFavSpotViewModel
import org.example.quiversync.features.spots.fav_spot_main_page.FavSpotsViewModel
import org.example.quiversync.features.user.UserViewModel
import org.example.quiversync.features.user.edit_user.EditProfileDetailsViewModel
import org.koin.mp.KoinPlatform

fun doInitKoin() = initKoin()


fun quiverViewModel() : QuiverViewModel = KoinPlatform.getKoin().get()

fun registerViewModel() : RegisterViewModel = KoinPlatform.getKoin().get()

fun loginViewModel() : LoginViewModel = KoinPlatform.getKoin().get()

fun userViewModel() : UserViewModel = KoinPlatform.getKoin().get()

fun editProfileDetailsViewModel() : EditProfileDetailsViewModel = KoinPlatform.getKoin().get()

fun favSpotsViewModel() : FavSpotsViewModel = KoinPlatform.getKoin().get()

fun addFavSpotViewModel() : AddFavSpotViewModel = KoinPlatform.getKoin().get()

fun addBoardViewModel() : AddBoardViewModel = KoinPlatform.getKoin().get()

fun homePageViewModel() : HomeViewModel = KoinPlatform.getKoin().get()

fun settingsViewModel() : SettingsViewModel = KoinPlatform.getKoin().get()

fun securityAndPrivacyViewModel() : SecurityAndPrivacyViewModel = KoinPlatform.getKoin().get()

fun forgotPasswordViewModel() : ForgotPasswordViewModel = KoinPlatform.getKoin().get()

fun onboardingViewModel() : OnboardingViewModel = KoinPlatform.getKoin().get()
