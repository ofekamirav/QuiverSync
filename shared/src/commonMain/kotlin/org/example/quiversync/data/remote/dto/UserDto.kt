package org.example.quiversync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String? = null,
    val email: String? = null,
    val dateOfBirth: String? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val surfLevel: String? = null,
    val profilePicture: String? = null,
)