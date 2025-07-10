package org.example.quiversync.features.settings

data class SecurityAndPrivacyFormData(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordSignIn: Boolean = false,
    val isPasswordLoading: Boolean = false,
    val error: String? = null
)
