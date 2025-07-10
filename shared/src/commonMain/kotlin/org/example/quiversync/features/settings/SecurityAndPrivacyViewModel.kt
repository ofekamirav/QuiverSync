package org.example.quiversync.features.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.user.UserUseCases
import org.example.quiversync.data.local.Result

class SecurityAndPrivacyViewModel(
    private val userUseCases: UserUseCases,
): BaseViewModel() {

    private val _uiState = MutableStateFlow<SecurityAndPrivacyState>(
        SecurityAndPrivacyState.Editing(form = SecurityAndPrivacyFormData())
    )
    val uiState: StateFlow<SecurityAndPrivacyState> get() = _uiState


    fun onEvent(event: SecurityAndPrivacyEvent) {
        when (event) {
            is SecurityAndPrivacyEvent.onNewPasswordChange -> {
                updateForm { form -> form.copy(newPassword = event.newPassword) }
            }

            is SecurityAndPrivacyEvent.onConfirmPasswordChange -> {
                updateForm { form -> form.copy(confirmPassword = event.confirmPassword) }
            }

            is SecurityAndPrivacyEvent.onCurrentPasswordChange -> {
                updateForm { form -> form.copy(currentPassword = event.currentPassword) }
            }
            SecurityAndPrivacyEvent.OnChangePasswordClicked -> {
                changePassword()
            }
        }
    }

    private fun updateForm(updateAction: (SecurityAndPrivacyFormData) -> SecurityAndPrivacyFormData) {
        val currentState = _uiState.value
        if (currentState is SecurityAndPrivacyState.Editing) {
            _uiState.update {
                currentState.copy(form = updateAction(currentState.form))
            }
        }
    }

    fun changePassword() {
        scope.launch {
            val form = (_uiState.value as? SecurityAndPrivacyState.Editing)?.form ?: return@launch

            if (form.newPassword.length < 6) {
                updateForm { it.copy(error = "Password must be at least 6 characters.") }
                return@launch
            }
            if (form.currentPassword.isBlank()) {
                updateForm { it.copy(error = "Current password cannot be empty.") }
                return@launch
            }
            if (form.newPassword.isBlank()) {
                updateForm { it.copy(error = "New password cannot be empty.") }
                return@launch
            }
            if (form.confirmPassword.isBlank()) {
                updateForm { it.copy(error = "Confirm password cannot be empty.") }
                return@launch
            }
            if (form.currentPassword == form.newPassword) {
                updateForm { it.copy(error = "New password cannot be the same as current password.") }
                return@launch
            }
            if(!form.newPassword.any { it.isUpperCase()}){
                updateForm { it.copy(error = "New password cannot contain uppercase letters.") }
                return@launch
            }
            if (form.newPassword != form.confirmPassword) {
                updateForm { it.copy(error = "New passwords do not match.") }
                return@launch
            }
            _uiState.emit(SecurityAndPrivacyState.Loading)
            updateForm { it.copy(isPasswordLoading = true, error = null) }

            val result = userUseCases.updatePasswordUseCase(
                currentPassword = form.currentPassword,
                newPassword = form.newPassword
            )

            when (result) {
                is Result.Success -> {
                    _uiState.emit(SecurityAndPrivacyState.Success)
                }
                is Result.Failure -> {
                    _uiState.emit(SecurityAndPrivacyState.Error(result.error?.message ?: "Unknown error"))
                }
            }
        }
    }
}