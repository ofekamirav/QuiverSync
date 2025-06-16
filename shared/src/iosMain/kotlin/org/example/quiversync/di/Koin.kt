package org.example.quiversync.di

import org.example.quiversync.features.quiver.QuiverViewModel
import org.koin.mp.KoinPlatform

fun quiverViewModel() : QuiverViewModel = KoinPlatform.getKoin().get()

fun doInitKoin() = initKoin()
