package org.example.quiversync.features.rentals.my_rentals


sealed class MyRentalsState {
    object Loading : MyRentalsState()
    data class Loaded(val myRentalRequests: List<MyRentRequest>) : MyRentalsState()
    data class Error(val message: String) : MyRentalsState()
}