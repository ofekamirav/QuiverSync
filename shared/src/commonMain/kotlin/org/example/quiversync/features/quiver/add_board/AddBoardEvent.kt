package org.example.quiversync.features.quiver.add_board

import org.example.quiversync.domain.model.SurfboardType

sealed interface AddBoardEvent {
    data class ModelChanged(val value: String) : AddBoardEvent
    data class CompanyChanged(val value: String) : AddBoardEvent
    data class BoardTypeChanged(val value: SurfboardType) : AddBoardEvent
    data class HeightChanged(val value: String) : AddBoardEvent
    data class WidthChanged(val value: String) : AddBoardEvent
    data class VolumeChanged(val value: String) : AddBoardEvent
    object NextStepClicked : AddBoardEvent
    object PreviousStepClicked : AddBoardEvent
    object SubmitClicked : AddBoardEvent
}