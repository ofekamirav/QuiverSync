package org.example.quiversync.features.spots.add_fav_spot


sealed class AddFavSpotState {
    data class Idle(val data: FavoriteSpotForm = FavoriteSpotForm()) : AddFavSpotState()
    data object Loading : AddFavSpotState()
    data object Loaded : AddFavSpotState()
    data class Error(val message: String) : AddFavSpotState()
}