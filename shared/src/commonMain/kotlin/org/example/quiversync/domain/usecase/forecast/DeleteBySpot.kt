package org.example.quiversync.domain.usecase.forecast

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.repository.ForecastRepository

class DeleteBySpot(
    private val forecastRepository: ForecastRepository,
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double
    ) : Result<Unit, Error> {
        return forecastRepository.deleteBySpot(latitude, longitude)
    }
}