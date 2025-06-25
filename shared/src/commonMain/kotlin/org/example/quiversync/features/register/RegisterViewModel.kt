package org.example.quiversync.features.register

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.features.BaseViewModel

class RegisterViewModel(
    private val registerUseCases: RegisterUseCases
): BaseViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun onRegisterClick(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState.Error("All fields are required.")
            return
        }

        scope.launch {
            _registerState.value = RegisterState.Loading

            val result = registerUseCases.registerUser(name, email, password)

            result.onSuccess {
                _registerState.value = RegisterState.Success
            }.onFailure { exception ->
                _registerState.value =
                    RegisterState.Error(exception.message ?: "Registration failed.")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}
