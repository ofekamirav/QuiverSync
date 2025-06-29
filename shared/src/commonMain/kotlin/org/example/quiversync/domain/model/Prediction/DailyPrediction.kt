package org.example.quiversync.domain.model.Prediction

import org.example.quiversync.domain.model.Forecast.DailyForecast
import org.example.quiversync.domain.model.Surfboard

data class DailyPrediction (
    val DailyForecast: DailyForecast,
    val Surfboard: Surfboard,
    val confidence: Int
    )