package org.example.quiversync.utils.extensions

import kotlin.math.*


fun isOutsideRadius(
    oldLat: Double,
    oldLon: Double,
    newLat: Double,
    newLon: Double,
    radiusKm: Double = 50.0
): Boolean {
    val earthRadiusKm = 6371.0

    val dLat = (newLat - oldLat).toRadians()
    val dLon = (newLon - oldLon).toRadians()

    val a = sin(dLat / 2).pow(2) +
            cos(oldLat.toRadians()) *
            cos(newLat.toRadians()) *
            sin(dLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance = earthRadiusKm * c

    return distance > radiusKm
}

fun Double.toRadians() = this * PI / 180.0
