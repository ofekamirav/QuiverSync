package org.example.quiversync.domain.model


data class OnboardingProfileDetails(
    val dateOfBirth: String,
    val heightCm: Double,
    val weightKg: Double,
    val surfLevel: String,
    val profilePicture: String? = null
)


enum class SurfLevel(val label: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    PRO("Pro");

    companion object {
        val all: List<SurfLevel> = entries
    }
}