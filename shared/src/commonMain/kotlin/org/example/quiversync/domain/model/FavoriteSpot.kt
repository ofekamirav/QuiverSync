package org.example.quiversync.domain.model

import org.example.quiversync.domain.model.Prediction.WeeklyPrediction

data class FavoriteSpot(
    val id: String,
    val ownerId: String,
    val name: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val recommendedBoardId: String, //can remove?
    val confidence: Int, //can remove?
    val waveHeight: String, //can remove?
    val addedDate: String,
    val weeklyPrediction: WeeklyPrediction,
)

