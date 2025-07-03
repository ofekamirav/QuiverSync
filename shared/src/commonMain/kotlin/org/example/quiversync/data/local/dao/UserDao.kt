package org.example.quiversync.data.local.dao

import org.example.quiversync.UserProfileQueries
import org.example.quiversync.domain.model.User

class UserDao(
    private val quiries: UserProfileQueries
) {
    fun getUserProfile(uid: String): User? {
        return quiries.getUserProfile(uid).executeAsOneOrNull()?.let { userEntity ->
            User(
                uid = userEntity.uid,
                name = userEntity.name,
                email = userEntity.email,
                dateOfBirth = userEntity.dateOfBirth,
                heightCm = userEntity.heightCm,
                weightKg = userEntity.weightKg,
                surfLevel = userEntity.surfLevel,
                profilePicture = userEntity.profilePicture
            )
        }

    }

    fun insertOrReplaceProfile(user: User) {
        quiries.insertOrReplaceProfile(
            uid = user.uid,
            name = user.name,
            email = user.email,
            dateOfBirth = user.dateOfBirth,
            heightCm = user.heightCm,
            weightKg = user.weightKg,
            surfLevel = user.surfLevel,
            profilePicture = user.profilePicture
        )
    }

    fun deleteProfile(uid: String) {
        quiries.deleteProfile(uid)
    }
}