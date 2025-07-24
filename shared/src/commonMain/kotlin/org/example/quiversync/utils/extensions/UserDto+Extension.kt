package org.example.quiversync.utils.extensions

import kotlinx.datetime.Clock
import org.example.quiversync.UserProfileEntity
import org.example.quiversync.domain.model.User
import org.example.quiversync.data.remote.dto.UserDto
import org.example.quiversync.domain.model.OwnerLocal

fun UserDto.toDomain(uid: String): User {
    return User(
        uid = uid,
        name = this.name ?: "Guest",
        email = this.email ?: "",
        dateOfBirth = this.dateOfBirth,
        phoneNumber = this.phoneNumber,
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
        dateOfBirth = this.dateOfBirth,
        phoneNumber = this.phoneNumber,
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
        phoneNumber = phoneNumber,
        heightCm = heightCm,
        weightKg = weightKg,
        surfLevel = surfLevel,
        profilePicture = profilePicture
    )
}

fun User.toOwnerLocal(): OwnerLocal {
    return OwnerLocal(
        id = uid,
        fullName = name,
        email = email,
        profilePicture = profilePicture,
        phoneNumber = phoneNumber,
        updatedAt = Clock.System.now().toEpochMilliseconds()
    )
}
