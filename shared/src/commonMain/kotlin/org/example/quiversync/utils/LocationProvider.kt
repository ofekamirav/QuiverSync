package org.example.quiversync.utils


data class Location(
    val latitude: Double,
    val longitude: Double
)

interface LocationProvider {
    suspend fun getCurrentLocation(): Location?
}
