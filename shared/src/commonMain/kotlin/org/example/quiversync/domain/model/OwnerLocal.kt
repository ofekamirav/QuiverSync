package org.example.quiversync.domain.model

data class OwnerLocal(
    val id: String,
    val fullName: String? = null,
    val email: String? = null,
    val profilePicture: String? = null,
    val phoneNumber: String? = null,
    val updatedAt: Long? = null
)
