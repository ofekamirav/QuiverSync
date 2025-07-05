package org.example.quiversync.features.rentals.explore

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.FinsSetup
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardType
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
                    type = board.type.toString(),
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
                type = SurfboardType.SHORTBOARD,
                imageRes = "hs_shortboard",
                height = "6'2\"",
                volume = "32L",
                width = "19\"",
                addedDate = "2024-05-01",
                isRentalPublished = false,
                isRentalAvailable = false,
                finSetup = FinsSetup.FIVEFINS,
                latitude = 32.355,
                longitude = 32.355,
                pricePerDay = 32.2,
            ),
        )
    }

    private fun getOwnerDetail(): User{
        return User(
            uid = "1",
            name = "John Doe",
            email = "william.henry.harrison@example-pet-store.com",
            profilePicture = "",
            dateOfBirth = "",
            heightCm = 0.0,
            weightKg = 0.0,
            surfLevel = "",
        )
    }

}