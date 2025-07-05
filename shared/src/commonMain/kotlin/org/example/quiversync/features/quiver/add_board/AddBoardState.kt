package org.example.quiversync.features.quiver.add_board

sealed class AddBoardState {
    data class Idle(val data: AddBoardFormData = AddBoardFormData()) : AddBoardState()
    data object Loading : AddBoardState()
    data object Loaded : AddBoardState()
    data class Error(val message: String) : AddBoardState()
}

