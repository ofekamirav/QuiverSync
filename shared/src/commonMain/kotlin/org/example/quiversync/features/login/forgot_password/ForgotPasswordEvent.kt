package org.example.quiversync.features.login.forgot_password

sealed class ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
    object SendResetLink : ForgotPasswordEvent()
}