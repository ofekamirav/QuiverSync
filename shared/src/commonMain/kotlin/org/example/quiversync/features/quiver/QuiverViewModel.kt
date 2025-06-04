package org.example.quiversync.features.quiver

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.quiversync.features.BaseViewModel

class QuiverViewModel: BaseViewModel() {
    private val _uiState = MutableStateFlow<QuiverState>(QuiverState.Loading)
    val uiState: StateFlow<QuiverState> get() = _uiState

}