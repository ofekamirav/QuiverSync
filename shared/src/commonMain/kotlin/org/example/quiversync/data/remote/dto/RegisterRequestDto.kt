package org.example.quiversync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
    val picture: String? = null,
    val height: Int,
    val weight: Int,
    val level: String,
)
