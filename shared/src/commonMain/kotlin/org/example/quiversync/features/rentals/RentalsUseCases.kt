package org.example.quiversync.features.rentals

import org.example.quiversync.domain.usecase.rentals.FetchExplorePageUseCase
import org.example.quiversync.domain.usecase.rentals.ObserveExploreBoardsUseCase
import org.example.quiversync.domain.usecase.rentals.StartRemoteSyncUseCase
import org.example.quiversync.domain.usecase.rentals.StopRemoteSyncUseCase

data class RentalsUseCases(
    val observeExploreBoards: ObserveExploreBoardsUseCase,
    val fetchExplorePage: FetchExplorePageUseCase,
    val startRemoteSync: StartRemoteSyncUseCase,
    val stopRemoteSync: StopRemoteSyncUseCase
)