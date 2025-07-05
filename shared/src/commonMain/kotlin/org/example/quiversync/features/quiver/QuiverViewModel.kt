package org.example.quiversync.features.quiver

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.features.BaseViewModel



class QuiverViewModel(
    private val quiverUseCases: QuiverUseCases
): BaseViewModel() {
    private val _uiState = MutableStateFlow<QuiverState>(QuiverState.Loading)
    val uiState: StateFlow<QuiverState> get() = _uiState

    init {
        fetchQuiver()
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
}
