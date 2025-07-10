package org.example.quiversync.features.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.User
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.utils.event.AppEvent
import org.example.quiversync.utils.event.EventBus


class UserViewModel(
    private val userUseCases: UserUseCases,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<UserState>(UserState.Loading)
    val uiState: StateFlow<UserState> get() = _uiState

    init {
        fetchUser()
        listenForProfileUpdates()
    }

    fun fetchUser() {
        scope.launch {
            _uiState.value = UserState.Loading
            val result = userUseCases.getUserProfileUseCase()
            val userId = sessionManager.getUid().toString()
            val boardsNumber: Int = userUseCases.getBoardsNumberUseCase(userId)
            when (result) {
                is Result.Success -> {
                    if(boardsNumber!= null) {
                        result.data?.let { _uiState.emit(UserState.Loaded(it,boardsNumber)) }
                    }
                }
                is Result.Failure -> {
                    _uiState.emit(UserState.Error(result.error?.message ?: "Unknown error"))
                }
            }
        }
    }

    private fun listenForProfileUpdates() {
        scope.launch {
            EventBus.events.collect { event ->
                if (event is AppEvent.ProfileUpdated) {
                    fetchUser()
                }
            }
        }
    }

}
