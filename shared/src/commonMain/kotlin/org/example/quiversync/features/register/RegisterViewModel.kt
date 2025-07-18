package org.example.quiversync.features.register

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.data.local.Result

class RegisterViewModel(
    private val registerUseCases: RegisterUseCases
): BaseViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle(RegisterFormData()))
    val registerState: StateFlow<RegisterState> = _registerState

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged -> {
                updateState { it.copy(name = event.value, nameError = null) }
            }
            is RegisterEvent.EmailChanged -> {
                updateState { it.copy(email = event.value, emailError = null) }
            }
            is RegisterEvent.PasswordChanged -> {
                updateState { it.copy(password = event.value, passwordError = null) }
            }
            RegisterEvent.ResetState -> {
                resetState()
            }
            RegisterEvent.SignUpClicked -> {
                validateAndRegister()
            }
        }
    }



    fun resetState(){
        _registerState.value = RegisterState.Idle(RegisterFormData())
    }


    private fun validateAndRegister() {
        val currentState = _registerState.value as? RegisterState.Idle ?: return

        val nameError = if (currentState.data.name.isBlank()) "Name is required" else null
        val emailError = if (!isEmailValid(currentState.data.email)) "Invalid email address" else null
        val passwordError = validatePasswordStrength(currentState.data.password)

        val hasErrors = listOf(nameError, emailError, passwordError).any { it != null }

        updateState {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
            )
        }

        if (hasErrors) return

        scope.launch {
            _registerState.value = RegisterState.Loading
            val result = registerUseCases.registerUser(
                name = currentState.data.name,
                email = currentState.data.email,
                password = currentState.data.password
            )
            when(result) {
                is Result.Success -> {
                   _registerState.emit(RegisterState.Loaded)
                }
                is Result.Failure -> {
                    _registerState.emit(RegisterState.Error(result.error?.message ?: "Unknown error"))
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val regex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return Regex(regex).matches(email)
    }

    private fun validatePasswordStrength(password: String): String? {
        if (password.length < 6) return "Password must be at least 6 characters"
        if (!password.any { it.isUpperCase() }) return "Password must contain at least one uppercase letter"
        return null
    }

    private fun updateState(update: (RegisterFormData) -> RegisterFormData) {
        val currentState = _registerState.value
        if (currentState is RegisterState.Idle) {
            _registerState.update { RegisterState.Idle(update(currentState.data)) }
        }
    }
}