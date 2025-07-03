package org.example.quiversync.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.data.remote.dto.StormglassResponse
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast

class StormGlassApi(
    private val client: HttpClient,
    //private val apiKey: String
) {
    suspend fun getFiveDayForecast(lat: Double, lng: Double): Result<WeeklyForecast> {
        val now = Clock.System.now()
        val end = now.plus(5 * 86_400, kotlinx.datetime.DateTimeUnit.SECOND)

        return try {
            val res = client.get("https://api.stormglass.io/v2/weather/point") {
               // header(HttpHeaders.Authorization, apiKey)
                parameter("lat", lat)
                parameter("lng", lng)
                parameter("start", now.toString())
                parameter("end", end.toString())
                parameter("params", PARAMS)
                parameter("source", "noaa")
            }.body<StormglassResponse>()

            val grouped = res.hours.groupBy {
                Instant.parse(it.time).toLocalDateTime(TimeZone.UTC).date.toString()
            }

            val days = grouped.map { (date, hours) ->
                DailyForecast(
                    date = date,
                    waveHeight = avg(hours.map { it.waveHeight?.noaa }),
                    windSpeed = avg(hours.map { it.windSpeed?.noaa }),
                    windDirection = avg(hours.map { it.windDirection?.noaa }),
                    swellPeriod = avg(hours.map { it.swellPeriod?.noaa }),
                    swellDirection = avg(hours.map { it.swellDirection?.noaa }),
                    latitude = lat,
                    longitude = lng,
                )
            }

            Result.success(WeeklyForecast(days))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun avg(list: List<Double?>): Double {
        val valid = list.filterNotNull()
        return if (valid.isEmpty()) 0.0 else valid.average()
    }

    companion object {
        private const val PARAMS =
            "waveHeight,windSpeed,windDirection,swellPeriod,swellDirection"
    }
}