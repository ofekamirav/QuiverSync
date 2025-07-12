package org.example.quiversync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavSpotDto(
    val name: String,
    val spotLatitude: Double,
    val spotLongitude: Double,
    val userID: String,
)
