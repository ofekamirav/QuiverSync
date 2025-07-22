package org.example.quiversync.features.rentals

import org.example.quiversync.domain.usecase.rentals.FetchExplorePageUseCase
import org.example.quiversync.domain.usecase.rentals.ObserveExploreBoardsUseCase

data class RentalsUseCases(
    val observeExploreBoards: ObserveExploreBoardsUseCase,
    val fetchExplorePage: FetchExplorePageUseCase
)