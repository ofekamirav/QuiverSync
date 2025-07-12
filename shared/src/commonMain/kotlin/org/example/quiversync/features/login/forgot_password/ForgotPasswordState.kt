package org.example.quiversync.features.login.forgot_password

sealed class ForgotPasswordState {
    data class Idle(val data: ForgotPasswordFormData) : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}
