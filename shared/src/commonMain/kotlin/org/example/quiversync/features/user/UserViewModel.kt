package org.example.quiversync.features.user

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.features.BaseViewModel

class UserViewModel(
    private val userUseCases: UserUseCases
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<UserState>(UserState.Loading)
    val uiState: StateFlow<UserState> get() = _uiState

    init {
        observeUserProfile()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeUserProfile() {
        scope.launch {
            userUseCases.getUserProfileUseCase()
                .flatMapLatest { userResult ->
                    when (userResult) {
                        is Result.Success -> {
                            val user = userResult.data
                            if (user != null) {
                                combine(
                                    flowOf(userUseCases.getBoardsNumberUseCase(user.uid)),
                                    flowOf(userUseCases.getSpotsNumberUseCase(user.uid)),
                                    flowOf(userUseCases.getRentalsNumberUseCase(user.uid))
                                ) { boardsNumber, spotsNumber, rentalsNumber ->
                                    UserState.Loaded(user, boardsNumber, spotsNumber, rentalsNumber)
                                }
                            } else {
                                flowOf(UserState.Error("User profile not found."))
                            }
                        }

                        is Result.Failure -> {
                            val errorMessage = userResult.error?.message ?: "Failed to load profile."
                            flowOf(UserState.Error(errorMessage))
                        }
                    }
                }
                .catch { e ->
                    emit(UserState.Error(e.message ?: "An unexpected error occurred."))
                }
                .collect { finalState ->
                    _uiState.emit(finalState)
                }
        }
    }

}