package org.example.quiversync.features.register

data class RegisterFormData(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isWaiting: Boolean = false,
)
