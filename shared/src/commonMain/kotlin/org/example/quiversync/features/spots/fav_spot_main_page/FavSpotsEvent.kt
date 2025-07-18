package org.example.quiversync.features.spots.fav_spot_main_page

import org.example.quiversync.domain.model.FavoriteSpot

interface FavSpotsEvent {
    data class DeleteSpot(val favoriteSpot: FavoriteSpot, val snackBarDurationMillis: Long) : FavSpotsEvent
    data class LoadWeekPredictions(val favoriteSpot: FavoriteSpot) : FavSpotsEvent
    data class ErrorOccurred(val message: String) : FavSpotsEvent
    object UndoDeleteSpot : FavSpotsEvent
    data class FetchWeeklyForecastForSpot(val latitude: Double, val longitude: Double) : FavSpotsEvent
}