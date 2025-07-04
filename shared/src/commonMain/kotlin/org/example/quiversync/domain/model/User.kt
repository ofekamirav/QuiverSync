package org.example.quiversync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val name: String,
    val password: String,
    val boards: Int? = null,
    val spots: Int? = null,
    val rentals: Int? = null,
    val email: String,
    val dateOfBirth: String? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val surfLevel: String? = null,
    val profilePicture: String? = null,
    val updatedAt: String? = null,
)