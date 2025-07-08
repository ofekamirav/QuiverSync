package org.example.quiversync.features.quiver

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.utils.extensions.platformLogger


class QuiverViewModel(
    private val quiverUseCases: QuiverUseCases,
    private val boardEventBus: BoardEventBus
): BaseViewModel() {
    private val _uiState = MutableStateFlow<QuiverState>(QuiverState.Loading)
    val uiState: StateFlow<QuiverState> get() = _uiState

    private val _boardToPublish = MutableStateFlow<Surfboard?>(null)
    val boardToPublish: StateFlow<Surfboard?> get() = _boardToPublish

    init {
        fetchQuiver()
        scope.launch {
            boardEventBus.events.collect { event ->
                when (event) {
                    BoardAddedEvent -> {
                        platformLogger("QuiverViewModel", "BoardAddedEvent received, refetching quiver.")
                        fetchQuiver()
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

    private fun fetchQuiver(){
        scope.launch {
            val result = quiverUseCases.getMyQuiverUseCase()
            when(result){
                is Result.Success -> {
                    result.data?.let { _uiState.emit(QuiverState.Loaded(it)) }
                }
                is Result.Failure -> {
                    _uiState.emit(QuiverState.Error(result.error.toString()))
                }
            }

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
                    _uiState.emit(QuiverState.Loading)
                    fetchQuiver() // Refresh the quiver after deletion
                }
                is Result.Failure -> {
                    _uiState.emit(QuiverState.Error(result.error.toString()))
                }
            }
        }
    }

    private fun publishSurfboardForRental(surfboardId: String, pricePerDay: Double) {
        scope.launch {
            val result = quiverUseCases.publishSurfboardToRentalUseCase(surfboardId,
                RentalPublishDetails(
                    pricePerDay = pricePerDay,
                    latitude = null,
                    longitude = null
                )
            )
            when(result) {
                is Result.Success -> {
                    dismissPublishDialog()
                    fetchQuiver() // Refresh the quiver after publishing
                }
                is Result.Failure -> {
                    _uiState.emit(QuiverState.Error(result.error?.message ?: "Failed to publish surfboard for rental."))
                    dismissPublishDialog()
                    fetchQuiver()
                }
            }
        }
    }

    private fun unpublishSurfboardFromRental(surfboardId: String) {
        scope.launch {
            val result = quiverUseCases.unpublishForRentalUseCase(surfboardId)
            when(result) {
                is Result.Success -> {
                    fetchQuiver() // Refresh the quiver after unpublishing
                }
                is Result.Failure -> {
                    _uiState.emit(QuiverState.Error(result.error.toString()))
                    fetchQuiver()
                }
            }
        }
    }

}
