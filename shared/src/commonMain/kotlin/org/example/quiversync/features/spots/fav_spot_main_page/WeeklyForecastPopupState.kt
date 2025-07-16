package org.example.quiversync.features.spots.fav_spot_main_page

sealed class WeeklyForecastPopupState {
    object Hidden : WeeklyForecastPopupState()
    object Loading : WeeklyForecastPopupState()
    data class Loaded(val data: FavSpotsData) : WeeklyForecastPopupState()
    data class Error(val message: String) : WeeklyForecastPopupState()
}