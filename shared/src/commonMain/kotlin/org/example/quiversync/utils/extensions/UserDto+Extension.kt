package org.example.quiversync.utils.extensions

import org.example.quiversync.domain.model.User
import org.example.quiversync.data.remote.dto.UserDto

fun UserDto.toDomain(uid: String): User {
    return User(
        uid = uid,
        name = this.name ?: "Guest",
        email = this.email ?: "",
        locationName = this.locationName,
        latitude = this.latitude,
        longitude = this.longitude,
        dateOfBirth = this.dateOfBirth,
        heightCm = this.heightCm,
        weightKg = this.weightKg,
        surfLevel = this.surfLevel ?: "Beginner",
        profilePicture = this.profilePicture,
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        name = this.name,
        email = this.email,
        locationName = this.locationName,
        latitude = this.latitude,
        longitude = this.longitude,
        dateOfBirth = this.dateOfBirth,
        heightCm = this.heightCm,
        weightKg = this.weightKg,
        surfLevel = this.surfLevel,
        profilePicture = this.profilePicture,
    )
}