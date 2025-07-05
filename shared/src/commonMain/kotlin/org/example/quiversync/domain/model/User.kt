package org.example.quiversync.domain.model

import kotlinx.serialization.Serializable
import org.example.quiversync.data.local.Error

@Serializable
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val dateOfBirth: String? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val surfLevel: String? = null,
    val profilePicture: String? = null,
)

@Serializable
data class UserError(
   override val message: String
): Error
