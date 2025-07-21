package org.example.quiversync.features.exploreRentals

import org.example.quiversync.domain.model.RentalOffer
import org.example.quiversync.domain.model.Surfboard

sealed class ExploreState {
    object Loading : ExploreState()
    data class Loaded(val explorePageData: List<RentalOffer>) : ExploreState()
    data class Error(val message: String) : ExploreState()
}