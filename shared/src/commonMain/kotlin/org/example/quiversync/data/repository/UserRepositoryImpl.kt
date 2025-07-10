package org.example.quiversync.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.UserError
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toDto

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUserProfile(): Result<User, Error> {
        val uid = sessionManager.getUid() ?: return Result.Failure<Error>(UserError("No UID"))

        val local = userDao.getUserProfile(uid)
        if (local != null) {
            return Result.Success(local)
        }

        val snapshot = firestore.collection("users").document(uid).get()
        if (snapshot.exists) {
            val user = snapshot.data<User>()

            userDao.insertOrReplaceProfile(user)
            return Result.Success(user)
        }
        return Result.Failure<UserError>(UserError("User profile not found"))
    }

    override suspend fun updateUserProfile(user: User): Result<Unit, Error> {
        val uid = sessionManager.getUid() ?: return Result.Failure(UserError("No UID"))

        return try {
            firestore.collection("users").document(uid).set(user.toDto(), merge = true)

            userDao.insertOrReplaceProfile(user)

            Result.Success(Unit)
        } catch (e: Exception) {
            platformLogger("UserRepository", "Failed to update user profile: ${e.message}")
            Result.Failure(UserError("Update failed: ${e.message}"))
        }
    }

    override suspend fun deleteProfileLocal(uid: String) {
        userDao.deleteProfile(uid)
    }

}