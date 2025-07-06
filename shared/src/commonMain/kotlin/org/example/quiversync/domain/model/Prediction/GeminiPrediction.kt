package org.example.quiversync.domain.model.Prediction

data class GeminiPrediction(
    val forecastLatitude: Double,//forecast id && spotID
    val forecastLongitude: Double,//forecast id
    val forecastDate: String,
    val surfboardID: String,
    val score: Long,
    val description: String
)