package org.example.quiversync.data.remote.datasource.user

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.quiversync.domain.model.User
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.UserError
import org.example.quiversync.utils.extensions.toDto

class UserRemoteSourceService(
    private val firebase: FirebaseFirestore,
):UserRemoteSource {

    override fun observeUserProfile(uid: String): Flow<User?> {
        return firebase.collection("users").document(uid)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists) {
                    try {
                        platformLogger("FirestoreUserSource", "User profile found for UID: $uid")
                        snapshot.data<User>()
                    } catch (e: Exception) {
                        platformLogger(
                            "FirestoreUserSource",
                            "Failed to parse user ID: ${snapshot.id}. Error: ${e.message}"
                        )
                        null
                    }
                } else {
                    platformLogger("FirestoreUserSource", "User profile not found for UID: $uid")
                    null
                }
            }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit, Error> {
        return try {
            firebase.collection("users").document(user.uid).set(user.toDto(), merge = true)
            Result.Success(Unit)
        } catch (e: Exception) {
            platformLogger("UserRepository", "Failed to update profile: ${e.message}")
            Result.Failure(UserError("Update failed: ${e.message}"))
        }
    }

    override suspend fun getUserById(userId: String): Result<User, Error> {
        return try {
            val snapshot = firebase.collection("users").document(userId).get()
            if (snapshot.exists) {
                platformLogger("UserRepository", "User found for ID: $userId")
                Result.Success(snapshot.data<User>())
            } else {
                platformLogger("UserRepository", "User not found for ID: $userId")
                Result.Failure(UserError("User not found"))
            }
        } catch (e: Exception) {
            platformLogger("UserRepository", "Error fetching user by ID: ${e.message}")
            Result.Failure(UserError("Error fetching user: ${e.message}"))
        }
    }
}