package org.example.quiversync.model

data class FavoriteSpot(
    val name: String,
    val location: String,
    val recommendedBoard: String,
    val confidence: Int,
    val waveHeight: String,
)

