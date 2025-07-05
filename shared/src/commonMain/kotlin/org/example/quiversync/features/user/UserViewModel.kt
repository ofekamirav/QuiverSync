package org.example.quiversync.features.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.User
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
            when (result) {
                is Result.Success -> {
                    result.data?.let { _uiState.emit(UserState.Loaded(it)) }
                }
                is Result.Failure -> {
                    _uiState.emit(UserState.Error(result.error?.message ?: "Unknown error"))
                }
            }
        }
    }
}
