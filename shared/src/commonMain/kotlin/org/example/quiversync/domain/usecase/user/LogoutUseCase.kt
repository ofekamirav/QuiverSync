package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.domain.repository.FavSpotRepository
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.utils.extensions.platformLogger

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val favSpotsRepository: FavSpotRepository,
    private val userRepository: UserRepository,
    private val quiverRepository: QuiverRepository
) {
    suspend operator fun invoke() {
        platformLogger("LogoutUseCase", "Starting logout process...")

        userRepository.stopUserSync()
        favSpotsRepository.stopRealtimeSync()
        quiverRepository.stopQuiverSync()

        authRepository.logout()

        platformLogger("LogoutUseCase", "Logout process complete.")
    }
}