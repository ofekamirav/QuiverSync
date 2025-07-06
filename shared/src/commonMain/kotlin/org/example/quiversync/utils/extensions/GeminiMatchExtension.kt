package org.example.quiversync.utils.extensions

import org.example.quiversync.GeminiPredictionEntity
import org.example.quiversync.domain.model.Prediction.GeminiPrediction

fun GeminiPredictionEntity.toGeminiMatch(): GeminiPrediction {
    return GeminiPrediction(
        surfboardID = surfboardID,
        forecastDate = forecastDate,
        forecastLatitude = forecastLatitude,
        forecastLongitude = forecastLongitude,
        score = score.toInt()
    )
}