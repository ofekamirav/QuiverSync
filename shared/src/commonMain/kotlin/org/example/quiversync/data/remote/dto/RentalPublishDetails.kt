package org.example.quiversync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RentalPublishDetails(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isRentalPublished: Boolean? = null,
    val isRentalAvailable: Boolean? = null,
    val pricePerDay: Double?= null
)
