package org.example.quiversync.features.rentals.explore

data class BoardForRent(
    val surfboardId: String,
    val ownerName: String,
    val ownerPic: String,
    val surfboardPic: String,
    val model: String,
    val type: String,
    val height: String,
    val width: String,
    val volume: String,
    val pricePerDay: Double
)
