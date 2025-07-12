package org.example.quiversync.data.local.dao

import org.example.quiversync.GeminiPredictionEntity
import org.example.quiversync.GeminiPredictionQueries
import org.example.quiversync.domain.model.prediction.GeminiPrediction

class GeminiPredictionDao(private val queries: GeminiPredictionQueries) {


    fun insert(prediction: GeminiPrediction , userId: String) {
        // Check if a prediction already exists for this date and location
        println("GeminiPredictionDao: Iiiiiiiiinsrrtion $userId, prediction:" +
                "$prediction")
        val existingPrediction = queries.GET_BEST_MATCH(userId, prediction.date, prediction.forecastLatitude, prediction.forecastLongitude).executeAsOneOrNull()
        if ( existingPrediction != null) {
            // If a prediction already exists, update it instead of inserting a new one
            println("GeminiPredictionDao: Updating existing prediction for user $userId on date ${prediction.date} at location (${prediction.forecastLatitude}, ${prediction.forecastLongitude})")
            queries.UPDATE_PREDICTION(
                userID = userId,
                surfboardID = prediction.surfboardID,
                forecastDate = prediction.date,
                forecastLatitude = prediction.forecastLatitude,
                forecastLongitude = prediction.forecastLongitude,
                score = prediction.score.toLong(),
                description = prediction.description,
                // WHERE clause values
                userID_ = userId,
                forecastDate_ = prediction.date,
                forecastLatitude_ = prediction.forecastLatitude,
                forecastLongitude_ = prediction.forecastLongitude
            )

            return
        }
        queries.INSERT_BEST_MATCH(
            userID = userId,
            surfboardID = prediction.surfboardID,
            forecastDate = prediction.date,
            forecastLatitude = prediction.forecastLatitude,
            forecastLongitude = prediction.forecastLongitude,
            score = prediction.score.toLong(),
            description = prediction.description
        )
    }

    fun getPredictionsBySpot(userId: String,latitude: Double, longitude: Double): List<GeminiPrediction> {
        return queries.GET_PREDICTIONS_BY_SPOT_AND_ID(userId,latitude, longitude).executeAsList().map { it.toDomain() }
    }

    fun deleteBySpotAndUser(latitude: Double, longitude: Double ,userId: String) {
        queries.deleteMatchesBySpot(userId , latitude, longitude )
    }

    fun getMatch(date: String, lat: Double, lng: Double , userId: String): GeminiPrediction? {
        return queries.GET_BEST_MATCH(userId , date, lat, lng).executeAsOneOrNull()?.toDomain()
    }

    fun getPredictionsForToday(date: String , userId: String): List<GeminiPrediction> {
        return queries.GET_PREDICTIONS_BY_DATE(userId,date).executeAsList().map { it.toDomain() }
    }


    fun getAllPredictionsForUser(userId: String): List<GeminiPrediction> {
        val predicted = queries.GET_ALL_PREDICTIONS().executeAsList().map { it.toDomain() }
        if (predicted.isEmpty()) {
            println( "GeminiPredictionDao : getAllPredictionsForUser() - No predictions found for user $userId")
            // If no predictions found for the user, return empty list
            return emptyList()
        }else{

            println( "GeminiPredictionDao : getAllPredictionsForUser() - Found ${predicted.size} predictions for user $userId")
        }
        return predicted
    }

    fun getAllPredictions(): List<GeminiPrediction> {
        return queries.GET_ALL_PREDICTIONS_NO_USER().executeAsList().map { it.toDomain() }
    }

    fun deleteAllPredictions() {
        queries.deleteAllPredictions()
    }

    private fun GeminiPredictionEntity.toDomain() = GeminiPrediction(
        predictionID = this.predictionID,
        userID = this.userID,
        forecastLatitude = this.forecastLatitude,
        forecastLongitude = this.forecastLongitude,
        date = this.forecastDate,
        surfboardID = this.surfboardID,
        score = this.score.toInt(),
        description = this.description
    )


}
