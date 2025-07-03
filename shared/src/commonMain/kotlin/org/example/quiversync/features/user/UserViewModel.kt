package org.example.quiversync.features.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.features.BaseViewModel


class UserViewModel(
    private val userUseCases: UserUseCases
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<UserState>(UserState.Loading)
    val uiState: StateFlow<UserState> get() = _uiState

    init {
        fetchUser()
    }

    fun fetchUser() {
        scope.launch {
            _uiState.value = UserState.Loading
            val result = userUseCases.getUserProfileUseCase()
            result
                .onSuccess { user ->
                    _uiState.emit(
                        UserState.Loaded(user)
                    )
                }
                .onFailure { e ->
                    _uiState.emit(
                        UserState.Error(e.message ?: "Unknown error")
                    )
                }
        }
    }
}
