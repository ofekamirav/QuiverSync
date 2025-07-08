package org.example.quiversync.features.quiver

import org.example.quiversync.domain.model.Surfboard

sealed interface QuiverEvent {
    data class onToggleClick(val surfboard: Surfboard, val isChecked: Boolean) : QuiverEvent
    data class ShowPublishDialog(val surfboardId: String) : QuiverEvent
    data object DismissPublishDialog : QuiverEvent
    data class ConfirmPublish(val surfboardId: String, val pricePerDay: Double) : QuiverEvent
    data class onDeleteClick(val surfboardId: String) : QuiverEvent
    data class UnpublishSurfboard(val surfboardId: String) : QuiverEvent
}