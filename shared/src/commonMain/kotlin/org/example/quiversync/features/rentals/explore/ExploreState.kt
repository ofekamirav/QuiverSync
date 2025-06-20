package org.example.quiversync.features.rentals.explore

import org.example.quiversync.domain.model.Surfboard

sealed class ExploreState {
    object Loading : ExploreState ()
    data class Loaded(val communityBoards: List<BoardForRent>) : ExploreState()
    data class Error(val message: String) : ExploreState()
}