package org.example.quiversync.domain.usecase.forecast

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.repository.ForecastRepository

class GetWeeklyForecastBySpotUseCase(
    private val repository: ForecastRepository

) {
    suspend operator fun invoke(spot:FavoriteSpot, isHomePage : Boolean): Result<List<DailyForecast>,Error> {
        return repository.getWeeklyForecast(spot.spotLatitude, spot.spotLongitude , isHomePage)
    }
}