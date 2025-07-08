package org.example.quiversync.features.spots.FavSpotMainPage

import org.example.quiversync.domain.model.FavoriteSpot

interface FavSpotsEvent {
    data class DeleteSpot(val favoriteSpot: FavoriteSpot) : FavSpotsEvent
    data object LoadFavSpots : FavSpotsEvent
}