package org.example.quiversync.features.settings

sealed class SecurityAndPrivacyState{
    object Loading : SecurityAndPrivacyState()
    object Success : SecurityAndPrivacyState()
    data class Error(val message: String) : SecurityAndPrivacyState()
    data class Editing(
        val form: SecurityAndPrivacyFormData,
    ) : SecurityAndPrivacyState()
}
