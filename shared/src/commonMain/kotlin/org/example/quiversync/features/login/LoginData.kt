package org.example.quiversync.features.login

data class LoginData(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)
