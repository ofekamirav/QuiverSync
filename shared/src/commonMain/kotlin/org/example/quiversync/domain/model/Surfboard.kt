package org.example.quiversync.domain.model

data class Surfboard(
    val id: String,
    val ownerId: String,
    val model: String,
    val company: String,
    val type: String,
    val height: String,
    val width: String,
    val volume: String,
    val imageRes: String,
    val addedDate: String,
    val isRentalPublished: Boolean,
    val isRentalAvailable: Boolean,
    val pricePerDay: Double?= null
)
