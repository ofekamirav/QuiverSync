package org.example.quiversync.features.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.features.BaseViewModel


class UserViewModel(
    private val userUseCases: UserUseCases
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<UserState>(UserState.Loading)
    val uiState: StateFlow<UserState> get() = _uiState

    init {
        observeUserProfile()
    }
    private fun observeUserProfile() {
        scope.launch {
            userUseCases.getUserProfileUseCase().collect { userResult ->
                when (userResult) {
                    is Result.Success -> {
                        val user = userResult.data
                        if (user != null) {
                            val boardsNumber = userUseCases.getBoardsNumberUseCase(user.uid)
                            val spotsNumber = userUseCases.getSpotsNumberUseCase(user.uid)
                            val rentalsNumber = userUseCases.getRentalsNumberUseCase(user.uid)
                            _uiState.emit(UserState.Loaded(user, boardsNumber, spotsNumber, rentalsNumber))
                        } else {
                            _uiState.emit(UserState.Error("User profile not found."))
                        }
                    }
                    is Result.Failure -> {
                        _uiState.emit(UserState.Error(userResult.error?.message ?: "Failed to load profile."))
                    }
                }
            }
        }
    }
}
