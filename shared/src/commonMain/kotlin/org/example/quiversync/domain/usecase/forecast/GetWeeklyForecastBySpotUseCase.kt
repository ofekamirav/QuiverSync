package org.example.quiversync.domain.usecase.forecast

import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.domain.repository.ForecastRepository

class GetWeeklyForecastBySpotUseCase(
    private val repository: ForecastRepository

) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<WeeklyForecast> {
        return repository.getWeeklyForecast(latitude, longitude)
    }
}