package org.example.quiversync.features.rentals.explore


sealed class ExploreState {
    object Loading : ExploreState ()
    data class Loaded(val communityBoards: List<BoardForDisplay>) : ExploreState()
    data class Error(val message: String) : ExploreState()
}