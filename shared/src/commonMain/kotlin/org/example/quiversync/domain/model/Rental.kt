package org.example.quiversync.domain.model

data class Rental(
    val id: String,
    val ownerId: String,
    val renterId: String,
    val surfboardId: String,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: RentalStatus,
    val addedDate: String
)
