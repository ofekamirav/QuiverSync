package org.example.quiversync.utils.extensions

import kotlin.math.*

fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Radius of Earth in km

    val dLat = (lat2 - lat1) * PI / 180
    val dLon = (lon2 - lon1) * PI / 180

    val lat1Rad = lat1 * PI / 180
    val lat2Rad = lat2 * PI / 180

    val a = sin(dLat / 2).pow(2.0) +
            cos(lat1Rad) * cos(lat2Rad) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c // Distance in kilometers
}
