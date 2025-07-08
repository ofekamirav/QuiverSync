package org.example.quiversync.domain.usecase.forecast

import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.repository.ForecastRepository

data class GetDailyForecast(
    private val repository: ForecastRepository
){
    suspend operator fun invoke(spot : FavoriteSpot) : Result<DailyForecast,TMDBError> {
        return try {
            return repository.getDailyForecastByDateAndSpot(
                latitude = spot.spotLatitude,
                longitude = spot.spotLongitude,
            )
        } catch ( e: Exception ) {
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }
}
