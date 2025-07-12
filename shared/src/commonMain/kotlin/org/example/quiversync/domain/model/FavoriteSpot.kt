package org.example.quiversync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteSpot(
    val spotID : String, // prediction id && forecast id
    val userID: String, // prediction id && forecast id
    val name: String,
    val spotLatitude: Double,// prediction id && forecast id
    val spotLongitude: Double, // prediction id && forecast id
)

