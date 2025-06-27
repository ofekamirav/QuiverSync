package org.example.quiversync.domain.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val locationName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val dateOfBirth: String? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val surfLevel: String? = null,
    val profilePicture: String? = null,
)