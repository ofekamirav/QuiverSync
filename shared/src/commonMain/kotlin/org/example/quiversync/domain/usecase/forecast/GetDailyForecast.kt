package org.example.quiversync.domain.usecase.forecast

import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.ForecastError
import org.example.quiversync.domain.repository.ForecastRepository

data class GetDailyForecast(
    private val repository: ForecastRepository
){
    suspend operator fun invoke(spot : FavoriteSpot) : Result<DailyForecast,Error> {
        return try {
            return repository.getDailyForecastByDateAndSpot(
                latitude = spot.spotLatitude,
                longitude = spot.spotLongitude,
            )
        } catch ( e: Exception ) {
            Result.Failure(ForecastError(e.message ?: "Unknown error"))
        }
    }
}
