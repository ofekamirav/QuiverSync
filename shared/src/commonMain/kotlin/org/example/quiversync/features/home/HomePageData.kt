package org.example.quiversync.features.home

import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.GeminiPrediction

data class HomePageData(
    val weeklyForecast: List<DailyForecast>,
    val predictionForToday : GeminiPrediction?,
    val surfboard: Surfboard?
)
