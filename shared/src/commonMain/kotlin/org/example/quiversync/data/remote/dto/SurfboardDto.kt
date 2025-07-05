package org.example.quiversync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SurfboardDto(
    val id: String? = null,
    val ownerId: String? = null,
    val model: String? = null,
    val company: String? = null,
    val type: String? = null,
    val height: String? = null,
    val width: String? = null,
    val volume: String? = null,
    val finSetup: String? = null,
    val imageRes: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val addedDate: String? = null,
    val isRentalPublished: Boolean? = null,
    val isRentalAvailable: Boolean? = null,
    val pricePerDay: Double? = null
)
