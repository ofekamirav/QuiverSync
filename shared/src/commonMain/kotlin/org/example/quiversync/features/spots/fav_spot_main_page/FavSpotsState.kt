package org.example.quiversync.features.spots.fav_spot_main_page

sealed class FavSpotsState{
    object Loading : FavSpotsState()
    data class Loaded(val favSpotsData: FavSpotsData) : FavSpotsState()
    data class Error(val message: String) : FavSpotsState()
}
