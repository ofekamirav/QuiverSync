package org.example.quiversync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String? = null,
    val email: String? = null,
    val locationName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val dateOfBirth: String? = null,
    val heightCm: Int? = null,
    val weightKg: Int? = null,
    val surfLevel: String? = null,
    val profilePicture: String? = null,
)