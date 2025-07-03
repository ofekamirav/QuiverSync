package org.example.quiversync.utils.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.DailyForecastEntity
import org.example.quiversync.domain.model.forecast.DailyForecast

fun DailyForecastEntity.toDailyForecast() = DailyForecast(
    date = date,
    latitude = latitude,
    longitude = longitude,
    waveHeight = waveHeight ?: 0.0,
    windSpeed = windSpeed ?: 0.0,
    windDirection = windDirection ?: 0.0,
    swellPeriod = swellPeriod ?: 0.0,
    swellDirection = swellDirection ?: 0.0
)

fun getTodayDateString(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return now.date.toString() // yyyy-MM-dd
}