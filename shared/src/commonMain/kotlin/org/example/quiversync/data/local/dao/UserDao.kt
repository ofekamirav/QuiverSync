package org.example.quiversync.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.quiversync.UserProfileQueries
import org.example.quiversync.domain.model.User

class UserDao(
    private val queries: UserProfileQueries,
) {
    fun getUserProfile(uid: String): Flow<User?> {
        return queries.getUserProfile(uid)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { userEntity ->
                userEntity?.let {
                    User(
                        uid = it.uid,
                        name = it.name,
                        email = it.email,
                        dateOfBirth = it.dateOfBirth,
                        phoneNumber = it.phoneNumber,
                        heightCm = it.heightCm,
                        weightKg = it.weightKg,
                        surfLevel = it.surfLevel,
                        profilePicture = it.profilePicture,
                    )
                }
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
            phoneNumber = user.phoneNumber,
            heightCm = user.heightCm,
            weightKg = user.weightKg,
            surfLevel = user.surfLevel,
            profilePicture = user.profilePicture
        )
    }

    fun deleteProfile(uid: String) {
        queries.deleteProfile(uid)
    }

    fun observeUserById(uid: String): Flow<User?> =
        queries.getUserById(uid)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity ->
                entity?.let {
                    User(
                        uid = it.uid,
                        name = it.name,
                        email = it.email,
                        dateOfBirth = it.dateOfBirth,
                        phoneNumber = it.phoneNumber,
                        heightCm = it.heightCm,
                        weightKg = it.weightKg,
                        surfLevel = it.surfLevel,
                        profilePicture = it.profilePicture
                    )
                }
            }

    fun getAllProfiles(): List<User> {
        return queries.getAllProfiles().executeAsList().map { userEntity ->
            User(
                uid = userEntity.uid,
                name = userEntity.name,
                email = userEntity.email,
                dateOfBirth = userEntity.dateOfBirth,
                phoneNumber = userEntity.phoneNumber,
                heightCm = userEntity.heightCm,
                weightKg = userEntity.weightKg,
                surfLevel = userEntity.surfLevel,
                profilePicture = userEntity.profilePicture
            )
        }
    }
}