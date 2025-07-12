package org.example.quiversync.domain.usecase.forecast

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.repository.ForecastRepository

data class DeleteOutDateForecastUseCase(
    private val repository: ForecastRepository
){
    suspend operator fun invoke() : Result<Unit,TMDBError> {
        return repository.deleteOutDateForecast()
    }
}
