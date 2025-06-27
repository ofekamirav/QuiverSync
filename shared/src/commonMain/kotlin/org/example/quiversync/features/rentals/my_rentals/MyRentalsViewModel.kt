package org.example.quiversync.features.rentals.my_rentals

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.DateTimeUnit
import org.example.quiversync.domain.model.Rental
import org.example.quiversync.domain.model.RentalStatus
import org.example.quiversync.domain.model.User
import org.example.quiversync.features.BaseViewModel


class MyRentalsViewModel: BaseViewModel() {

    private val _uiState = MutableStateFlow<MyRentalsState>(MyRentalsState.Loading)
    val uiState: StateFlow<MyRentalsState> get() = _uiState

    init{
        fetchMyRentals()
    }

    private fun fetchMyRentals(){
        scope.launch {
            val myRentals = getMyRentals()

            val myRentalsRequests = myRentals.map {
                val owner = getOwner(it.ownerId)
                MyRentRequest(
                    requestId = it.id,
                    boardModel = it.surfboardId,
                    renterName = owner.uid,
                    renterImageUrl = it.ownerId,
                    ownerName = owner.name,
                    ownerImageUrl = owner.profilePicture.toString(),
                    startDate = it.startDate,
                    endDate = it.endDate,
                    status = it.status,
                    createdDate = it.addedDate,
                )
            }

            _uiState.emit(
                MyRentalsState.Loaded(myRentalsRequests)
            )
        }
    }
    private fun getMyRentals(): List<Rental> {
        fun Long.toDate(): Long = this

        val currentTime = now()
        val oneDayInMillis = 24 * 60 * 60 * 1000L // Milliseconds in a day

        return listOf(
            Rental(
                id = "1",
                surfboardId = "surfboard_001",
                ownerId = "user_owner_A",
                renterId = "user_renter_X",
                startDate = "",
                endDate = "",
                totalPrice = 75.50,
                status = RentalStatus.APPROVED,
                addedDate = ""
            ),
            Rental(
                id = "2",
                surfboardId = "surfboard_002",
                ownerId = "user_owner_B",
                renterId = "user_renter_Y",
                startDate = "",
                endDate = "",
                totalPrice = 120.00,
                status = RentalStatus.PENDING,
                addedDate =""
            ),
            Rental(
                id = "3",
                surfboardId = "surfboard_003",
                ownerId = "user_owner_A", // Same owner as rental 1
                renterId = "user_renter_Z",
                startDate = "",
                endDate = "",
                totalPrice = 45.00,
                status = RentalStatus.APPROVED,
                addedDate =""
            ),
            Rental(
                id = "4",
                surfboardId = "surfboard_004",
                ownerId = "user_owner_C",
                renterId = "user_renter_X", // Same renter as rental 1
                startDate = "",
                endDate = "",
                totalPrice = 90.25,
                status = RentalStatus.PENDING,
                addedDate =""
            ),
            Rental(
                id = "5",
                surfboardId = "surfboard_005",
                ownerId = "user_owner_B",
                renterId = "user_renter_W",
                startDate = "",
                endDate = "",
                totalPrice = 150.00,
                status = RentalStatus.REJECTED,
                addedDate =""
            )
        )
    }

    private fun getOwner(ownerId: String): User {
        return User(
            uid = ownerId,
            name = "Ofek",
            locationName = "San Diego, CA",
            latitude = 32.7157,
            longitude = -117.1611,
            profilePicture = "",
            heightCm = 169.0,
            weightKg = 62.0,
            surfLevel = "Intermediate",
            email = "MikeRod@gmail.com",
            dateOfBirth = "01/01/2000",
        )
    }

}