package org.example.quiversync.features.spots.add_fav_spot

data class FavoriteSpotForm(
    val name: String = "",
    val nameError: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationError: String? = null
)
