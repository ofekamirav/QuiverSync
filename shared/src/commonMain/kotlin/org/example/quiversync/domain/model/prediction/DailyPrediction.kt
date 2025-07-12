package org.example.quiversync.domain.model.prediction

import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.forecast.DailyForecast

data class DailyPrediction (
    val prediction : GeminiPrediction,
    val surfboard: Surfboard,
    val dailyForecast: DailyForecast
)