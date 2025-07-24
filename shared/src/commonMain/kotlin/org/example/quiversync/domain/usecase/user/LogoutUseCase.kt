package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.domain.repository.FavSpotRepository
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.domain.repository.RentalsRepository
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.utils.extensions.platformLogger

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val favSpotsRepository: FavSpotRepository,
    private val userRepository: UserRepository,
    private val quiverRepository: QuiverRepository,
    private val rentalsRepository: RentalsRepository
) {
    suspend operator fun invoke() {
        platformLogger("LogoutUseCase", "Starting logout process...")
        try {
            userRepository.stopUserSync()
            favSpotsRepository.stopRealtimeSync()
            quiverRepository.stopQuiverSync()
            rentalsRepository.stopRemoteSync()

            authRepository.logoutRemote()

        } catch (e: Exception) {
            platformLogger("LogoutUseCase", "Error during remote logout: ${e.message}")
        } finally {
            authRepository.clearLocalData()
            platformLogger("LogoutUseCase", "Local data cleared. Logout process complete.")
        }
    }
}