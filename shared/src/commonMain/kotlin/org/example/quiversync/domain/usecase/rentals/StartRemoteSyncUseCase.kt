package org.example.quiversync.domain.usecase.rentals

import org.example.quiversync.domain.repository.RentalsRepository

class StartRemoteSyncUseCase(
    private val rentalsRepository: RentalsRepository
) {
    operator fun invoke() = rentalsRepository.startRemoteSync()
}