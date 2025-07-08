package org.example.quiversync.features.spots.AddFavSpot

interface AddFavSpotEvent {
    data class NameChanged(val value: String) : AddFavSpotEvent
    data class LocationChanged(val longitude: Double, val latitude: Double) : AddFavSpotEvent
    data object SaveClicked : AddFavSpotEvent
}