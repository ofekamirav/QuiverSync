package org.example.quiversync.features.spots.FavSpotMainPage

import org.example.quiversync.domain.model.FavoriteSpot

sealed class FavSpotsState{
    object Loading : FavSpotsState()
    data class Loaded(val favSpotsData: FavSpotsData) : FavSpotsState()
    data class Error(val message: String) : FavSpotsState()
}
