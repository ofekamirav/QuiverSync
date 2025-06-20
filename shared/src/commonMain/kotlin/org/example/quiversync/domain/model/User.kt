package org.example.quiversync.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val boards: Int,
    val spots: Int,
    val rentals: Int,
    val dateOfBirth: String,
    val heightCm: Int,
    val weightKg: Int,
    val surfLevel: String,
    val imageUrl: String,
    val updatedAt: String
)
