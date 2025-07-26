package org.example.quiversync.domain.usecase

import org.example.quiversync.domain.repository.ForecastRepository
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.domain.repository.UserRepository

class GetHomePageDataUseCase(
    private val forecastRepository: ForecastRepository,
    private val quiverRepository: QuiverRepository,
    private val userRepository: UserRepository
) {
}