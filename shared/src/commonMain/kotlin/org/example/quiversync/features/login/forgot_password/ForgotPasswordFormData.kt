package org.example.quiversync.features.login.forgot_password

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordFormData(
    val email: String = "",
    val emailError: String? = null
)
