package org.example.quiversync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RentalOffer(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerProfilePicture: String? = null,
    val surfboardId: String = "",
    val surfboardName: String = "",
    val surfboardImageUrl: String? = null,
    val pricePerDay: Double = 0.0,
    val latitude: Double,
    val longitude: Double,
    val description: String = "",
    val isAvailable: Boolean = true
)
