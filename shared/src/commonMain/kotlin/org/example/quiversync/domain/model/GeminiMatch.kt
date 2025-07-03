package org.example.quiversync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GeminiMatch(
    val boardId: String,
    val userId: String,
    val forecastDate: String,
    val forecastLatitude: Double,
    val forecastLongitude: Double,
    val score: Int
)
