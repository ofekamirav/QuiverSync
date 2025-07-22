package org.example.quiversync.domain.usecase.forecast

import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.utils.LocationProvider
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.session.SessionManager

class GetWeeklyForecastByLocationUseCase(
    private val repository: ForecastRepository,
    private val locationProvider: LocationProvider,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): Result<List<DailyForecast>,TMDBError> {
        val location = locationProvider.getCurrentLocation()
            ?: return Result.Failure(TMDBError("Could not get current location"))
        //save the location to session manager
        sessionManager.setLatitude(location.latitude)
        sessionManager.setLongitude(location.longitude)
        return repository.getWeeklyForecast(
            latitude = location.latitude,
            longitude = location.longitude,
            isHomePage = true // Assuming this is always true for the home page use case
        )
    }
}