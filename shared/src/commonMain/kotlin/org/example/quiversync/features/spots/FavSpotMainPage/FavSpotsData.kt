package org.example.quiversync.features.spots.FavSpotMainPage

import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.prediction.GeminiPrediction

data class FavSpotsData (
    val spots : List<FavoriteSpot>,
    val predictions : List<GeminiPrediction>
)