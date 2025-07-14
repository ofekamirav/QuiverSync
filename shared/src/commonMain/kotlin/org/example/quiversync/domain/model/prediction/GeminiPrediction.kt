package org.example.quiversync.domain.model.prediction

data class GeminiPrediction(
    val predictionID: Long = 0L,
    val userID: String = "",
    val forecastLatitude: Double = 0.0,
    val forecastLongitude: Double = 0.0,
    val date: String = "",
    val surfboardID: String = "",
    val score: Int = 0,
    val description: String = ""
)