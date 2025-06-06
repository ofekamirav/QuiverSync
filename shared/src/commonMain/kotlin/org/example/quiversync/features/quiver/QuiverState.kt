package org.example.quiversync.features.quiver

import org.example.quiversync.domain.model.Surfboard

sealed class QuiverState {
    object Loading : QuiverState()
    data class Success(val quiver: List<Surfboard>) : QuiverState()
    data class Error(val message: String) : QuiverState()
}