package org.example.quiversync.features.login.forgot_password

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.user.UserUseCases

class ForgotPasswordViewModel(
    private val userUseCases: UserUseCases
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle(ForgotPasswordFormData()))
    val uiState: StateFlow<ForgotPasswordState> = _uiState

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                updateIdleState { it.copy(email = event.email, emailError = null) }
            }
            ForgotPasswordEvent.SendResetLink -> sendResetLink()
        }
    }

    private fun sendResetLink() {
        scope.launch {
            val email = (_uiState.value as? ForgotPasswordState.Idle)?.data?.email
            if (email.isNullOrBlank() || !isEmailValid(email)) {
                updateIdleState { it.copy(emailError = "Please enter a valid email address.") }
                return@launch
            }

            _uiState.value = ForgotPasswordState.Loading

            val result = userUseCases.sendPasswordResetEmailUseCase(email)

            when (result) {
                is Result.Success -> {
                    _uiState.value = ForgotPasswordState.Success
                }
                is Result.Failure -> {
                    _uiState.value = ForgotPasswordState.Error(result.error?.message ?: "Unknown error")
                }
            }
        }
    }

    private suspend fun sendEmail(email: String) {
        when (val result = userUseCases.sendPasswordResetEmailUseCase(email)) {
            is Result.Success -> _uiState.value = ForgotPasswordState.Success
            is Result.Failure -> _uiState.value = ForgotPasswordState.Error(result.error?.message ?: "Unknown error")
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val regex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return Regex(regex).matches(email)
    }
    fun returnToIdle() {
        _uiState.value = ForgotPasswordState.Idle(ForgotPasswordFormData())
    }

    private fun updateIdleState(updateAction: (ForgotPasswordFormData) -> ForgotPasswordFormData) {
        (_uiState.value as? ForgotPasswordState.Idle)?.let { currentState ->
            _uiState.update { currentState.copy(data = updateAction(currentState.data)) }
        }
    }
}