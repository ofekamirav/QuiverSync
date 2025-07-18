package org.example.quiversync.domain.model


data class OnboardingProfileDetails(
    val phoneNumber: String,
    val dateOfBirth: String,
    val heightCm: Double,
    val weightKg: Double,
    val surfLevel: String,
    val profilePicture: String? = null,
    val agreedToTerms: Boolean = false,
    val agreementTimestamp: String? = null
)


enum class SurfLevel(val label: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    PRO("Pro");

    companion object {
        val all: List<SurfLevel> = entries
    }
}