package org.example.quiversync.features.spots

import org.example.quiversync.domain.model.FavoriteSpots

sealed class FavSpotsState{
    object Loading : FavSpotsState()
    data class Loaded(val spots: FavoriteSpots) : FavSpotsState()
    data class Error(val message: String) : FavSpotsState()
}
