package org.example.quiversync.features.Rentals.my_rentals

import org.example.quiversync.domain.model.Rental

sealed class MyRentalsState {
    object Loading : MyRentalsState()
    data class Success(val myRentalRequests: List<Rental>) : MyRentalsState()
    data class Error(val message: String) : MyRentalsState()
}