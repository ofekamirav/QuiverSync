package org.example.quiversync.domain.usecase.forecast

import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.utils.LocationProvider
import org.example.quiversync.data.local.Result

class GetWeeklyForecastByLocationUseCase(
    private val repository: ForecastRepository,
    private val locationProvider: LocationProvider
) {
    suspend operator fun invoke(): Result<List<DailyForecast>,TMDBError> {
        val location = locationProvider.getCurrentLocation()
            ?: return Result.Failure(TMDBError("Could not get current location"))
        return repository.getWeeklyForecast(
            latitude = location.latitude,
            longitude = location.longitude,
            isHomePage = true // Assuming this is always true for the home page use case
        )
    }
}