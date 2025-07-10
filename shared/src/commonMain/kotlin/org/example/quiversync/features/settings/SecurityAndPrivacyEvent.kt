package org.example.quiversync.features.settings

sealed interface SecurityAndPrivacyEvent {
    data class onCurrentPasswordChange(val currentPassword: String) : SecurityAndPrivacyEvent
    data class onNewPasswordChange(val newPassword: String) : SecurityAndPrivacyEvent
    data class onConfirmPasswordChange(val confirmPassword: String) : SecurityAndPrivacyEvent
    object OnChangePasswordClicked : SecurityAndPrivacyEvent
}