package org.example.quiversync.features.rentals.explore

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.features.BaseViewModel

class ExploreViewModel : BaseViewModel(){
    private val _uiState = MutableStateFlow<ExploreState>(ExploreState.Loading)
    val uiState: StateFlow<ExploreState> get() = _uiState

    init {
        fetchBoards()
    }

    fun fetchBoards(){
        scope.launch {
            val boards = createMockBoards()
            delay(1500)
            val owner = getOwnerDetail()
            val boardsForRent = boards.map { board ->
                BoardForRent(
                    surfboardId = board.id,
                    ownerName = owner.name,
                    ownerPic = owner.profilePicture.toString(),
                    surfboardPic = board.imageRes,
                    model = board.model,
                    type = board.type,
                    height = board.height,
                    width = board.width,
                    volume = board.volume,
                    pricePerDay = board.pricePerDay ?: 0.0
                )
            }

            _uiState.emit(
                ExploreState.Loaded(boardsForRent)
            )
        }
    }
    private fun createMockBoards(): List<Surfboard> {
        return listOf(
            Surfboard(
                id = "1",
                ownerId = "",
                model = "Holly Grail",
                company = "Hayden Shapes",
                type = "Shortboard",
                imageRes = "hs_shortboard",
                height = "6'2",
                width = "19",
                volume = "32L",
                addedDate = "2024-05-01",
                isRentalPublished = false,
                isRentalAvailable = false,
            ),
            Surfboard(
                id = "2",
                ownerId = "",
                model = "",
                company = "Slater Designs",
                type = "Funboard",
                imageRes = "hs_shortboard",
                height = "5'8",
                width = "20 1/4",
                volume = "25L",
                addedDate = "2024-04-15",
                isRentalPublished = true,
                isRentalAvailable = true,
                pricePerDay = 10.0
            )
        )
    }

    private fun getOwnerDetail(): User{
        return User(
            uid = "1",
            name = "John Doe",
            email = "william.henry.harrison@example-pet-store.com",
            profilePicture = "",
            locationName = "Tel-Aviv, Israel",
            latitude = 32.0853,
            longitude = 34.7818,
            dateOfBirth = "",
            heightCm = 0,
            weightKg = 0,
            surfLevel = "",
        )
    }

}