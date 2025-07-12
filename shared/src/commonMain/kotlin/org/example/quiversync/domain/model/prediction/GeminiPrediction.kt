package org.example.quiversync.domain.model.prediction

data class GeminiPrediction(
    val predictionID: Long,
    val userID: String,//user id
    val forecastLatitude: Double,//forecast id && spotID
    val forecastLongitude: Double,//forecast id
    val date: String,
    val surfboardID: String,
    val score: Int,
    val description: String
)