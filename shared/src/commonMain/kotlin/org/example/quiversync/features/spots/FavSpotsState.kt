package org.example.quiversync.features.spots

import org.example.quiversync.domain.model.FavoriteSpot

sealed class FavSpotsState{
    object Loading : FavSpotsState()
    data class Success(val spots: List<FavoriteSpot>) : FavSpotsState()
    data class Error(val message: String) : FavSpotsState()
}
