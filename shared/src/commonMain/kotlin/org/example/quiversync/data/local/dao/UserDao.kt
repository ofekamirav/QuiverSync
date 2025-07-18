package org.example.quiversync.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.quiversync.UserProfileQueries
import org.example.quiversync.domain.model.User

class UserDao(
    private val queries: UserProfileQueries,
) {
    fun getUserProfile(uid: String): User? {
        return queries.getUserProfile(uid).executeAsOneOrNull()?.let { userEntity ->
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

    fun updateUserProfile(user: User, uid: String) {
        queries.updateProfile(
            uid = uid,
            name = user.name,
            email = user.email,
            dateOfBirth = user.dateOfBirth,
            heightCm = user.heightCm,
            weightKg = user.weightKg,
            surfLevel = user.surfLevel,
            profilePicture = user.profilePicture
        )
    }

    fun insertOrReplaceProfile(user: User , uid: String) {
        queries.insertOrReplaceProfile(
            uid = uid,
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
        queries.deleteProfile(uid)
    }

    fun getAllProfiles(): List<User> {
        return queries.getAllProfiles().executeAsList().map { userEntity ->
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
}