package org.example.quiversync.features.register

import org.example.quiversync.domain.model.SurfLevel

data class OnboardingFormData(
    // Data fields
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val selectedSurfLevel: SurfLevel? = null,
    val profileImageUrl: String? = null,
    val isUploadingImage: Boolean = false,
    val agreedToTerms: Boolean = false,
    val agreementTimestamp: String? = null,

    // Validation error fields
    val phoneNumberError: String? = null,
    val dateOfBirthError: String? = null,
    val heightError: String? = null,
    val weightError: String? = null,
    val surfLevelError: String? = null,
    val profileImageError: String? = null,
    val imageUploadError: String? = null,
    val termsError: String? = null,
    val uploadFromIOS: Boolean = false,
    )
