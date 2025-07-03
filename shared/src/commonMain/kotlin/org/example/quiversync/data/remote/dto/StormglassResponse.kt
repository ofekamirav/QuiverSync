package org.example.quiversync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StormglassResponse(
    val hours: List<HourEntry>
)

@Serializable
data class HourEntry(
    val time: String,
    val waveHeight: SourceVal? = null,
    val windSpeed: SourceVal? = null,
    val windDirection: SourceVal? = null,
    val swellPeriod: SourceVal? = null,
    val swellDirection: SourceVal? = null,
    val waterTemperature: SourceVal? = null
)

@Serializable
data class SourceVal(
    val noaa: Double? = null
)