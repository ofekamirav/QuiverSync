package org.example.quiversync.domain.model.Prediction

import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.forecast.DailyForecast

data class DailyPrediction (
    val DailyForecast: DailyForecast,
    val Surfboard: Surfboard,
    val confidence: Int
    )