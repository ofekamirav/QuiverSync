package org.example.quiversync.features.exploreRentals

import org.example.quiversync.domain.usecase.quiver.GetAvaibleBoardsUseCase
import org.example.quiversync.domain.usecase.rentals.exploreRentals.BuildRentalOfferUseCase
import org.example.quiversync.domain.usecase.rentals.exploreRentals.GetRentalsBoardsUseCase
import org.example.quiversync.domain.usecase.user.GetUserByIDUseCase
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase

class ExploreUseCases (
//    val getRentalsBoardsUseCase : GetRentalsBoardsUseCase,
    val getAvailableBoardsUseCase : GetAvaibleBoardsUseCase,
    val getUserProfileUseCase: GetUserProfileUseCase,
    val getUserByIDUseCase : GetUserByIDUseCase,
    val buildRentalOfferUseCase : BuildRentalOfferUseCase
)