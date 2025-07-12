package org.example.quiversync.features.spots.AddFavSpot

data class FavoriteSpotForm(
    val name: String = "",
    val nameError: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationError: String? = null
)
