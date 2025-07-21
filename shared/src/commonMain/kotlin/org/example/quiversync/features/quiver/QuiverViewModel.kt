package org.example.quiversync.features.quiver

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.utils.extensions.platformLogger


class QuiverViewModel(
    private val quiverUseCases: QuiverUseCases
): BaseViewModel() {
    private val _uiState = MutableStateFlow<QuiverState>(QuiverState.Loading)
    val uiState: StateFlow<QuiverState> get() = _uiState

    private val _boardToPublish = MutableStateFlow<Surfboard?>(null)
    val boardToPublish: StateFlow<Surfboard?> get() = _boardToPublish

    init {
        observeQuiver()
    }

    fun refreshQuiver() {
        observeQuiver()
    }

    private fun observeQuiver() {
        scope.launch {
            _uiState.value = QuiverState.Loading
            quiverUseCases.getMyQuiverUseCase().collect { result ->
                when(result) {
                    is Result.Success -> {
                        _uiState.value = QuiverState.Loaded(result.data ?: emptyList())
                    }
                    is Result.Failure -> {
                        _uiState.value = QuiverState.Error(result.error.toString())
                    }
                }
            }
        }
    }

    fun onEvent(event: QuiverEvent) {
        when (event) {
            is QuiverEvent.ConfirmPublish -> publishSurfboardForRental(event.surfboardId, event.pricePerDay)
            is QuiverEvent.onDeleteClick -> deleteSurfboard(event.surfboardId)
            is QuiverEvent.DismissPublishDialog -> dismissPublishDialog()
            is QuiverEvent.ShowPublishDialog -> showPublishDialog(event.surfboardId)
            is QuiverEvent.UnpublishSurfboard -> unpublishSurfboardFromRental(event.surfboardId)
            is QuiverEvent.onToggleClick -> { }
        }
    }

    private fun showPublishDialog(surfboardId: String) {
        scope.launch {
            val currentBoards = (_uiState.value as? QuiverState.Loaded)?.boards ?: emptyList()
            val surfboard = currentBoards.find { it.id == surfboardId }
            _boardToPublish.emit(surfboard)
        }
    }

    private fun dismissPublishDialog() {
        scope.launch {
            _boardToPublish.emit(null)
        }
    }

    private fun deleteSurfboard(surfboardId: String) {
        scope.launch {
            val result = quiverUseCases.deleteSurfboardUseCase(surfboardId)
            when(result) {
                is Result.Success -> {
                }
                is Result.Failure -> {
                    _uiState.emit(QuiverState.Error(result.error.toString()))
                }
            }
        }
    }

    private fun publishSurfboardForRental(surfboardId: String, pricePerDay: Double) {
        scope.launch {
            platformLogger("RentalFlow", "Starting publishSurfboardForRental with surfboardId=$surfboardId, pricePerDay=$pricePerDay")

            val result = quiverUseCases.publishSurfboardToRentalUseCase(
                surfboardId,
                RentalPublishDetails(
                    pricePerDay = pricePerDay,
                    latitude = null,
                    longitude = null
                )
            )
            platformLogger("RentalFlow", "Publish surfboard result: $result")

            val user =
                (quiverUseCases.getUserProfileUseCase().firstOrNull() as? Result.Success)?.data

            platformLogger("RentalFlow", "this is the user: $user")
            if(user == null) {
                platformLogger("RentalFlow", "User is null, cannot proceed with rental publish.")
                _uiState.emit(QuiverState.Error("User not found."))
                return@launch
            }


            when (result) {
                is Result.Success -> {
                    platformLogger("RentalFlow", "Surfboard published successfully, now fetching board by ID: $surfboardId")

                    when (val surfboard = quiverUseCases.getBoardByIDUseCase(surfboardId)) {
                        is Result.Success -> {
                            if (surfboard.data == null) {
                                platformLogger("RentalFlow", "Surfboard not found after publish.")
                                _uiState.emit(QuiverState.Error("Surfboard not found."))
                                return@launch
                            }

                            platformLogger("RentalFlow", "Surfboard found: ${surfboard.data.model}")
                            val publishRental = user.let {
                                quiverUseCases.addRentalOfferUseCase(
                                    surfboard = surfboard.data,
                                    rentalDetails = RentalPublishDetails(
                                        pricePerDay = pricePerDay,
                                        latitude = null,
                                        longitude = null
                                    ),
                                    user = it
                                )
                            }

                            platformLogger("RentalFlow", "RentalOffer creation result: $publishRental")
                        }

                        is Result.Failure -> {
                            platformLogger("RentalFlow", "Failed to fetch surfboard: ${surfboard.error}")
                            _uiState.emit(QuiverState.Error(surfboard.error.toString()))
                        }
                    }

                    dismissPublishDialog()
                }

                is Result.Failure -> {
                    platformLogger("RentalFlow", "Failed to publish surfboard: ${result.error}")
                    _uiState.emit(QuiverState.Error(result.error?.message ?: "Failed to publish surfboard for rental."))
                    dismissPublishDialog()
                }
            }
        }
    }

    private fun unpublishSurfboardFromRental(surfboardId: String) {
        scope.launch {
            val result = quiverUseCases.unpublishForRentalUseCase(surfboardId)
            when(result) {
                is Result.Success -> {
                }
                is Result.Failure -> {
                    _uiState.emit(QuiverState.Error(result.error.toString()))
                }
            }
        }
    }

}
