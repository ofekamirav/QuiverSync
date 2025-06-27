package org.example.quiversync.features.register

import org.example.quiversync.domain.model.SurfLevel

// In features/register/OnboardingState.kt

sealed class OnboardingState {
    data class Idle(
        // Data fields
        val dateOfBirth: String = "",
        val heightCm: String = "",
        val weightKg: String = "",
        val selectedSurfLevel: SurfLevel? = null,
        val profileImageUrl: String? = null,
        val isUploadingImage: Boolean = false,

        // Validation error fields
        val dateOfBirthError: String? = null,
        val heightError: String? = null,
        val weightError: String? = null,
        val surfLevelError: String? = null,
        val profileImageError: String? = null,
        val imageUploadError: String? = null

    ) : OnboardingState()

    object Loading : OnboardingState()
    object Success : OnboardingState()
    data class Error(val message: String) : OnboardingState()
}