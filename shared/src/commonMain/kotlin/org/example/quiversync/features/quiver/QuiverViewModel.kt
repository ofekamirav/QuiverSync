package org.example.quiversync.features.quiver

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.Quiver
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.features.BaseViewModel



class QuiverViewModel(
    //private val quiverUseCases: QuiverUseCases
): BaseViewModel() {
    private val _uiState = MutableStateFlow<QuiverState>(QuiverState.Loading)
    val uiState: StateFlow<QuiverState> get() = _uiState

    init {
        fetchQuiver()
    }
    private fun fetchQuiver(){
        scope.launch {
            val quiver = createMockSurfBoards()
            delay(1500)


            _uiState.emit(
                QuiverState.Loaded(quiver)
            )
        }
    }
}


private fun createMockSurfBoards(): Quiver{
    val quiverList = listOf(
    Surfboard(
        id= "1",
        ownerId = "",
        model = "Holly Grail",
        company = "Hayden Shapes",
        type = "Shortboard",
        imageRes = "hs_shortboard",
        height = "6'2\"",
        volume = "32L",
        width = "19\"",
        addedDate = "2024-05-01",
        isRentalPublished = false,
        isRentalAvailable = false,
    ),
    Surfboard(
        id= "2",
        ownerId = "",
        model = "FRK+",
        company = "Slater Designs",
        type = "Funboard",
        imageRes = "hs_shortboard",
        height = "5'8\"",
        volume = "28L",
        width = "20 1/4\"",
        addedDate = "2024-04-15",
        isRentalPublished = true,
        isRentalAvailable = true,
        pricePerDay = 10.0)
    )

    return Quiver(items = quiverList)
}