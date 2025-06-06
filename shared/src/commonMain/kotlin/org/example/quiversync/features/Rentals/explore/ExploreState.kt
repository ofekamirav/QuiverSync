package org.example.quiversync.features.Rentals.explore

import org.example.quiversync.domain.model.Surfboard

sealed class ExploreState {
    object Loading : ExploreState ()
    data class Success(val communityBoards: List<Surfboard>) : ExploreState()
    data class Error(val message: String) : ExploreState()
}