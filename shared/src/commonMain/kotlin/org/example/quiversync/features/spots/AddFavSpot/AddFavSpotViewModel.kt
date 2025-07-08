package org.example.quiversync.features.spots.AddFavSpot

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.spots.FavSpotMainPage.FavSpotsUseCases

class AddFavSpotViewModel(
    private val favSpotUseCases: FavSpotsUseCases
) :BaseViewModel() {
    private val _addFavSpotState = MutableStateFlow<AddFavSpotState>(AddFavSpotState.Idle(FavoriteSpotForm()))
    val addFavSpotState : StateFlow<AddFavSpotState> get() = _addFavSpotState

    fun onEvent(event: AddFavSpotEvent) {
        when (event) {
            is AddFavSpotEvent.NameChanged -> {
                updateState { it.copy(name = event.value, nameError = null) }
            }
            is AddFavSpotEvent.LocationChanged -> {
                updateState { it.copy(longitude = event.longitude, latitude = event.latitude, locationError = null) }
            }
            AddFavSpotEvent.SaveClicked -> {
                validateAndSave()
            }
        }
    }

    private fun updateState(update: (FavoriteSpotForm) -> FavoriteSpotForm) {
        _addFavSpotState.update { currentState ->
            if (currentState is AddFavSpotState.Idle) {
                currentState.copy(data = update(currentState.data))
            } else {
                currentState
            }
        }
    }


    private fun validateAndSave() {
        val currentState = _addFavSpotState.value as? AddFavSpotState.Idle ?: return

        val nameError = if (currentState.data.name.isBlank()) "Name is required" else null
        val locationError = if (currentState.data.longitude == 0.0 || currentState.data.latitude == 0.0) "Location is required" else null

        val hasErrors = listOf(nameError, locationError).any { it != null }

        updateState {
            it.copy(
                nameError = nameError,
                locationError = locationError
            )
        }

        if (hasErrors) return

        scope.launch {
            _addFavSpotState.value = AddFavSpotState.Loading
            val result = favSpotUseCases.AddFavSpotUseCase(
                FavoriteSpot(
                    name = currentState.data.name,
                    spotLatitude = currentState.data.latitude,
                    spotLongitude = currentState.data.longitude
                )
            )
            if( result is Result.Success ) {
                _addFavSpotState.value = AddFavSpotState.Loaded
            } else {
                _addFavSpotState.value = AddFavSpotState.Error("Failed to add favorite spot")
            }
        }
    }
}
