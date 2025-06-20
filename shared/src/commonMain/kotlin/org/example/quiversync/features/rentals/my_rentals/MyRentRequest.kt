package org.example.quiversync.features.rentals.my_rentals

import org.example.quiversync.domain.model.RentalStatus

data class MyRentRequest(
    val requestId: String,
    val boardModel: String,
    val ownerName: String,
    val ownerImageUrl: String,
    val renterName: String,
    val renterImageUrl: String,
    val startDate: String,
    val endDate: String,
    val status: RentalStatus,
    val createdDate: String
)
