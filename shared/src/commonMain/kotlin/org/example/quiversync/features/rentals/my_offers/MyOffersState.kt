package org.example.quiversync.features.rentals.my_offers

import org.example.quiversync.features.rentals.my_rentals.MyRentRequest

sealed class MyOffersState {
    object Loading : MyOffersState()
    data class Loaded(val myOfferRequests: List<MyRentRequest>) : MyOffersState()
    data class Error(val message: String) : MyOffersState()
}