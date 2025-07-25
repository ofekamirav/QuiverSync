package org.example.quiversync.features.spots.add_fav_spot

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.spots.FavSpotsUseCases
import org.example.quiversync.utils.extensions.platformLogger

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
            _addFavSpotState.emit(AddFavSpotState.Loading)
            val spot = FavoriteSpot(
                spotID = "",
                userID = "", // Assuming userID is set elsewhere or not needed for this operation
                name = currentState.data.name,
                spotLatitude = currentState.data.latitude,
                spotLongitude = currentState.data.longitude
            )
            when(val forecastResult = favSpotUseCases.getWeeklyForecastBySpotUseCase(spot,false)) {
                is Result.Success -> {
                   val forecast = forecastResult.data
                    if (forecast == null) {
                        platformLogger("AddFavSpotViewModel", ">>> [DEBUG] 🛑 FAILURE: No forecast data available for this spot.")
                        _addFavSpotState.emit(AddFavSpotState.Error("No forecast data available for this spot"))
                        return@launch
                    }
                    forecast.let { dailyForecasts ->
                        if(dailyForecasts.first().waveHeight <= 0.0) {
                            platformLogger("AddFavSpotViewModel", ">>> [DEBUG] 🛑 FAILURE: Invalid spot, no waves detected.")
                            _addFavSpotState.emit(AddFavSpotState.Error("Please enter a valid spot near the ocean yew!"))
                            return@launch
                        }
                    }

                }
                is Result.Failure -> {
                    platformLogger("AddFavSpotViewModel", ">>> [DEBUG] 🛑 FAILURE: Failed to fetch forecast: ${forecastResult.error?.message}")
                    _addFavSpotState.emit(AddFavSpotState.Error("Failed to fetch forecast: ${forecastResult.error?.message}"))
                    return@launch
                }
            }

            val result = favSpotUseCases.addFavSpotUseCase(spot)
            if( result is Result.Success ) {
                platformLogger("AddFavSpotViewModel", ">>> [DEBUG] 🛑 SUCCESS: Favorite spot added successfully.")
                _addFavSpotState.emit(AddFavSpotState.Loaded)
            } else {
                _addFavSpotState.emit(AddFavSpotState.Error("Failed to add favorite spot"))
            }
        }
    }
    fun resetToIdle() {
        _addFavSpotState.value = AddFavSpotState.Idle(FavoriteSpotForm())
    }
}
