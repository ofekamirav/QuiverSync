package org.example.quiversync.di

import org.example.quiversync.features.login.LoginViewModel
import org.example.quiversync.features.quiver.QuiverViewModel
import org.example.quiversync.features.register.RegisterViewModel
import org.koin.mp.KoinPlatform

fun doInitKoin() = initKoin()


fun quiverViewModel() : QuiverViewModel = KoinPlatform.getKoin().get()

fun registerViewModel() : RegisterViewModel = KoinPlatform.getKoin().get()

fun loginViewModel() : LoginViewModel = KoinPlatform.getKoin().get()

