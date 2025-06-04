package org.example.quiversync.model

data class Quiver(
    val userId: String,
    val surfboards: List<Surfboard>,
    val updatedAt: String
)
