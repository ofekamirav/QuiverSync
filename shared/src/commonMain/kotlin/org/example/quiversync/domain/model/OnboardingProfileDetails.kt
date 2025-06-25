package org.example.quiversync.domain.model


data class OnboardingProfileDetails(
    val dateOfBirth: String,
    val heightCm: String,
    val weightKg: String,
    val surfLevel: String,
    val profilePicture: String? = null
)
