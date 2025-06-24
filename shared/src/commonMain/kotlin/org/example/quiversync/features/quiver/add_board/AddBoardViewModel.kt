package org.example.quiversync.features.quiver.add_board

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.example.quiversync.features.BaseViewModel

class AddBoardViewModel: BaseViewModel() {
    private val _uiState = MutableStateFlow(AddBoardState())
    val uiState: StateFlow<AddBoardState> get() = _uiState

    fun onEvent(event: AddBoardEvent) {
        when (event) {
            is AddBoardEvent.ModelChanged -> _uiState.update { it.copy(model = event.value) }
            is AddBoardEvent.CompanyChanged -> _uiState.update { it.copy(company = event.value) }
            is AddBoardEvent.BoardTypeChanged -> _uiState.update { it.copy(boardType = event.value) }
            is AddBoardEvent.HeightChanged -> _uiState.update { it.copy(height = event.value) }
            is AddBoardEvent.WidthChanged -> _uiState.update { it.copy(width = event.value) }
            is AddBoardEvent.VolumeChanged -> _uiState.update { it.copy(volume = event.value) }
            AddBoardEvent.NextStepClicked -> if (_uiState.value.currentStep < _uiState.value.totalSteps) {
                _uiState.update { it.copy(currentStep = it.currentStep + 1) }
            }
            AddBoardEvent.PreviousStepClicked -> if (_uiState.value.currentStep > 1) {
                _uiState.update { it.copy(currentStep = it.currentStep - 1) }
            }
            AddBoardEvent.SubmitClicked -> {}//submitBoard()
        }
    }
}