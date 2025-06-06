package org.example.quiversync.features.Rentals.my_offers

import org.example.quiversync.domain.model.Rental

sealed class MyOffersState {
    object Loading : MyOffersState()
    data class Success(val myOfferRequests: List<Rental>) : MyOffersState()
    data class Error(val message: String) : MyOffersState()
}