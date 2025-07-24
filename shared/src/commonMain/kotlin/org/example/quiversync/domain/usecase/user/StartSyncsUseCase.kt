package org.example.quiversync.domain.usecase.user

import org.example.quiversync.domain.repository.FavSpotRepository
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.utils.extensions.platformLogger

class StartSyncsUseCase(
    private val quiverRepository: QuiverRepository,
    private val userRepository: UserRepository,
    private val favSpotsRepository: FavSpotRepository
) {
    suspend operator fun invoke() {
        platformLogger("StartSyncsUseCase", "Starting all core syncs...")
        quiverRepository.startQuiverSync()
        userRepository.startUserSync()
        favSpotsRepository.startRealtimeSync()
        platformLogger("StartSyncsUseCase", "All core syncs have been initiated.")
    }
}