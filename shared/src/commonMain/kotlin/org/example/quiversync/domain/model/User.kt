package org.example.quiversync.domain.model

import kotlinx.serialization.Serializable
import org.example.quiversync.data.local.Error

@Serializable
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val surfLevel: String? = null,
    val profilePicture: String? = null,
    val agreedToTerms: Boolean = false,
    val agreementTimestamp: String? = null,
)