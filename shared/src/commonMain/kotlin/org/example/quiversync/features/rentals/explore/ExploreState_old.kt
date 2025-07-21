package org.example.quiversync.features.rentals.explore

sealed class ExploreState_old {
    object Loading : ExploreState_old ()
    data class Loaded(val communityBoards: List<BoardForRent>) : ExploreState_old()
    data class Error(val message: String) : ExploreState_old()
}