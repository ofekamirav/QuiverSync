package org.example.quiversync.utils.extensions

import org.example.quiversync.UserProfileEntity
import org.example.quiversync.domain.model.User
import org.example.quiversync.data.remote.dto.UserDto

fun UserDto.toDomain(uid: String): User {
    return User(
        uid = uid,
        name = this.name ?: "Guest",
        email = this.email ?: "",
        dateOfBirth = this.dateOfBirth,
        heightCm = this.heightCm,
        weightKg = this.weightKg,
        surfLevel = this.surfLevel ?: "Beginner",
        profilePicture = this.profilePicture,
        password = this.password ?: "",
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        name = this.name,
        email = this.email,
        dateOfBirth = this.dateOfBirth,
        heightCm = this.heightCm,
        weightKg = this.weightKg,
        surfLevel = this.surfLevel,
        profilePicture = this.profilePicture,
    )
}

fun UserProfileEntity.toUser(): User {
    return User(
        uid = uid,
        name = name,
        email = email,
        dateOfBirth = dateOfBirth,
        heightCm = heightCm,
        weightKg = weightKg,
        surfLevel = surfLevel,
        profilePicture = profilePicture
    )
}
