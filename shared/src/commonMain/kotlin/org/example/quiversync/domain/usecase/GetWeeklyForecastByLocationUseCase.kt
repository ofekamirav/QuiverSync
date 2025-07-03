package org.example.quiversync.domain.usecase

import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.utils.LocationProvider

class GetWeeklyForecastByLocationUseCase(
    private val repository: ForecastRepository,
    private val locationProvider: LocationProvider
) {
    suspend operator fun invoke(): Result<WeeklyForecast> {
        val location = locationProvider.getCurrentLocation()
            ?: return Result.failure(Exception("Could not get current location"))
        return repository.getWeeklyForecast(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
}