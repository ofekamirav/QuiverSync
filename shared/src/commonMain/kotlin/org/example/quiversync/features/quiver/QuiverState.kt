package org.example.quiversync.features.quiver

import org.example.quiversync.domain.model.Quiver
import org.example.quiversync.domain.model.Surfboard

sealed class QuiverState {
    object Loading : QuiverState()
    data class Success(val quiver: Quiver) : QuiverState()
    data class Error(val message: String) : QuiverState()
}