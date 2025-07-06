package org.example.quiversync.data.local.dao

import org.example.quiversync.GeminiPredictionEntity
import org.example.quiversync.QuiverSyncDatabase
import org.example.quiversync.domain.model.Prediction.GeminiPrediction

class GeminiPredictionDao(private val db: QuiverSyncDatabase) {

    private val queries = db.geminiPredictionQueries

    fun insert(prediction: GeminiPrediction) {
        queries.INSERT_BEST_MATCH(
            surfboardID = prediction.surfboardID,
            forecastDate = prediction.forecastDate,
            forecastLatitude = prediction.forecastLatitude,
            forecastLongitude = prediction.forecastLongitude,
            score = prediction.score,
            description = prediction.description
        )
    }

    fun deleteBySpot(latitude: Double, longitude: Double) {
        queries.deleteMatchesBySpot(latitude, longitude)
    }

    fun getMatch(date: String, lat: Double, lng: Double): GeminiPrediction? {
        return queries.GET_BEST_MATCH(date, lat, lng).executeAsOneOrNull()?.toDomain()
    }

    fun getTopMatch(date: String, lat: Double, lng: Double): GeminiPrediction? {
        return queries.GET_TOP_MATCH_BY_USER_SPOT_AND_DATE(date, lat, lng).executeAsOneOrNull()?.toDomain()
    }

    fun deleteByDateAndLocation(date: String, lat: Double, lng: Double) {
        queries.DELETE_BEST_MATCH_FOR_DATE(date, lat, lng)
    }

    private fun GeminiPredictionEntity.toDomain() = GeminiPrediction(
        forecastLatitude = this.forecastLatitude,
        forecastLongitude = this.forecastLongitude,
        forecastDate = this.forecastDate,
        surfboardID = this.surfboardID,
        score = this.score,
        description = this.description
    )
}
