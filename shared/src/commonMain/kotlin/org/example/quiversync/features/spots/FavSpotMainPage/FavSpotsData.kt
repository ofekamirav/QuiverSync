package org.example.quiversync.features.spots.FavSpotMainPage

import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.DailyPrediction
import org.example.quiversync.domain.model.prediction.GeminiPrediction

data class FavSpotsData (
    val spots : List<FavoriteSpot>,
    val allSpotsDailyPredictions : List<GeminiPrediction>,
    val boards: List<Surfboard>,
    val currentForecastsForAllSpots: List<DailyForecast>,
    val weeklyForecastForSpecificSpot: List<DailyForecast>,
    val weeklyPredictionsForSpecificSpot: List<GeminiPrediction>,
    val weeklyUiPredictions: List<DailyPrediction>
)