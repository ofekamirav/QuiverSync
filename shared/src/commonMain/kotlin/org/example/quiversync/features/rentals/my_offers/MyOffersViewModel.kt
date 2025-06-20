package org.example.quiversync.features.rentals.my_offers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.Rental
import org.example.quiversync.domain.model.RentalStatus
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.rentals.my_rentals.MyRentRequest

class MyOffersViewModel: BaseViewModel() {
    private val _uiState = MutableStateFlow<MyOffersState>(MyOffersState.Loading)
    val uiState: StateFlow<MyOffersState> get() = _uiState

    init{
        fetchMyOffers()
    }

    private fun fetchMyOffers(){
        scope.launch {
            val myOffers = getMyOffers()
            val myOfferRequests = myOffers.map {
                MyRentRequest(
                    requestId = it.id,
                    boardModel = it.surfboardId,
                    renterName = it.renterId,
                    renterImageUrl = "",
                    ownerImageUrl = "",
                    ownerName = "my name",
                    startDate = it.startDate,
                    endDate = it.endDate,
                    status = it.status,
                    createdDate = it.addedDate,
                )

            }
            _uiState.emit(
                MyOffersState.Loaded(myOfferRequests)
            )
        }
    }
    private fun getMyOffers(): List<Rental> {
        return listOf(
            Rental(
                id = "1",
                surfboardId = "",
                ownerId = "",
                renterId = "",
                startDate = "",
                endDate = "",
                totalPrice = 0.0,
                status = RentalStatus.APPROVED,
                addedDate = ""
            ),
            Rental(
                id = "2",
                surfboardId = "",
                ownerId = "",
                renterId = "",
                startDate = "",
                endDate = "",
                totalPrice = 0.0,
                status = RentalStatus.PENDING,
                addedDate = ""
            )
        )
    }
}