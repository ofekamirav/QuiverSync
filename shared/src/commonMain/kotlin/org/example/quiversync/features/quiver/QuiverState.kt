package org.example.quiversync.features.quiver

import org.example.quiversync.domain.model.Surfboard

sealed class QuiverState {
    object Loading : QuiverState()
    data class Loaded(val boards: List<Surfboard>) : QuiverState()
    data class Error(val message: String) : QuiverState()
}