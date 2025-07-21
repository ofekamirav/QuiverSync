package org.example.quiversync.features.quiver

import org.example.quiversync.domain.usecase.quiver.AddBoardUseCase
import org.example.quiversync.domain.usecase.quiver.DeleteSurfboardUseCase
import org.example.quiversync.domain.usecase.quiver.GetBoardByIDUseCase
import org.example.quiversync.domain.usecase.quiver.GetMyQuiverUseCase
import org.example.quiversync.domain.usecase.quiver.PublishSurfboardToRentalUseCase
import org.example.quiversync.domain.usecase.quiver.UnpublishSurfboardFromRentalUseCase
import org.example.quiversync.domain.usecase.rentals.exploreRentals.AddRentalOfferUseCase
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase

data class QuiverUseCases(
    val getMyQuiverUseCase: GetMyQuiverUseCase,
    val deleteSurfboardUseCase: DeleteSurfboardUseCase,
    val addSurfboardUseCase: AddBoardUseCase,
    val publishSurfboardToRentalUseCase: PublishSurfboardToRentalUseCase,
    val unpublishForRentalUseCase: UnpublishSurfboardFromRentalUseCase,
    val getBoardByIDUseCase: GetBoardByIDUseCase,

    // Rental use cases
    val addRentalOfferUseCase: AddRentalOfferUseCase,

    //user useCases

    val getUserProfileUseCase: GetUserProfileUseCase
)
