package org.example.quiversync.features.spots

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.FavoriteSpots
import org.example.quiversync.features.BaseViewModel

class FavSpotsViewModel: BaseViewModel() {

    private val _uiState = MutableStateFlow<FavSpotsState>(FavSpotsState.Loading)
    val uiState: StateFlow<FavSpotsState> get() = _uiState

    init {
        fetchFavSpots()
    }
    private fun fetchFavSpots(){
        scope.launch {
            val favSpots = createMockSpots()
            _uiState.emit(
                FavSpotsState.Loaded(favSpots)
            )
        }
    }



}

private fun createMockSpots(): FavoriteSpots {
    val favSpots = listOf(
        FavoriteSpot("Pipeline", "North Shore, HI", "Pipeline", "94", 3.2,3.1,"1" , 2 , "d" , "4"),
        FavoriteSpot("Punta Roca", "North Shore, HI", "Punta Roca", "94", 3.2,3.1,"2" , 2 , "d" , "4"),
        FavoriteSpot("Trestles", "North Shore, HI", "Trestles", "94", 3.2,3.1,"1" , 2 , "d" , "4")
    )

    return FavoriteSpots(items = favSpots)
}
