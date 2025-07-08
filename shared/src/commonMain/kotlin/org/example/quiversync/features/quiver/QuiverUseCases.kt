package org.example.quiversync.features.quiver

import org.example.quiversync.domain.usecase.quiver.AddBoardUseCase
import org.example.quiversync.domain.usecase.quiver.DeleteSurfboardUseCase
import org.example.quiversync.domain.usecase.quiver.GetMyQuiverUseCase
import org.example.quiversync.domain.usecase.quiver.PublishSurfboardToRentalUseCase
import org.example.quiversync.domain.usecase.quiver.UnpublishSurfboardFromRentalUseCase

data class QuiverUseCases(
    val getMyQuiverUseCase: GetMyQuiverUseCase,
    val deleteSurfboardUseCase: DeleteSurfboardUseCase,
    val addSurfboardUseCase: AddBoardUseCase,
    val publishSurfboardToRentalUseCase: PublishSurfboardToRentalUseCase,
    val unpublishForRentalUseCase: UnpublishSurfboardFromRentalUseCase,
)
