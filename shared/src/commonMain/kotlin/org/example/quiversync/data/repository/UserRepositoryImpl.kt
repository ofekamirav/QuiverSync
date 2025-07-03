package org.example.quiversync.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.UserRepository

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUserProfile(): Result<User> {
        val uid = sessionManager.getUid() ?: return Result.failure(Exception("No UID"))

        val local = userDao.getUserProfile(uid)
        if (local != null) {
            return Result.success(local)
        }

        val snapshot = firestore.collection("users").document(uid).get()
        if (snapshot.exists) {
            val user = snapshot.data<User>()

            userDao.insertOrReplaceProfile(user)
            return Result.success(user)
        }
        return Result.failure(Exception("No profile found in Firebase"))
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        val uid = sessionManager.getUid() ?: return Result.failure(Exception("No UID"))

        firestore.collection("users").document(uid).set(user)

        userDao.insertOrReplaceProfile(user)
        return Result.success(Unit)
    }

    override suspend fun deleteProfileLocal(uid: String) {
        userDao.deleteProfile(uid)
    }

}