package org.example.quiversync.domain.usecase.rentals

import org.example.quiversync.domain.repository.RentalsRepository

class StopRemoteSyncUseCase(
    private val rentalsRepository: RentalsRepository
) {
    operator fun invoke() = rentalsRepository.stopRemoteSync()

}