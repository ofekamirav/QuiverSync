package org.example.quiversync.domain.model

data class FavoriteSpot(
    val id: String,
    val ownerId: String,
    val name: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val recommendedBoardId: String,
    val confidence: Int,
    val waveHeight: String,
    val addedDate: String,
)

