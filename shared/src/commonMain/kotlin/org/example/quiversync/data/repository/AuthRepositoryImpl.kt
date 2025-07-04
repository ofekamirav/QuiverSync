package org.example.quiversync.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.example.quiversync.data.remote.dto.UserDto
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.utils.extensions.toDto
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.utils.Location
import org.example.quiversync.utils.extensions.toDomain

class AuthRepositoryImpl(
    private val sessionManager: SessionManager,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): AuthRepository  {

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): Result<Unit> {
        return try {
            val userCredential = auth.createUserWithEmailAndPassword(email, password)
            val uid = userCredential.user?.uid

            if (uid != null) {
                val userDto = UserDto(email = email, name = name)
                firestore.collection("users").document(uid).set(userDto)
                // Set the user's UID in the session manager
                sessionManager.setUid(uid)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create user: UID is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User?  {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            val snapshot = firestore
                .collection("users")
                .document(firebaseUser.uid)
                .get()

            val userDto = snapshot.data<UserDto>()
            userDto.toDomain(firebaseUser.uid)
        } else {
            null
        }
    }


    override suspend fun login(
        email: String,
        password: String,
    ): Result<Unit> {
        return try {
            val userCredential = auth.signInWithEmailAndPassword(email, password)
            val uid = userCredential.user?.uid
            if (uid != null) {
                getCurrentUser()
                sessionManager.setUid(uid)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to login: UID is null"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        sessionManager.clearAll()
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            val userDto = user.toDto()
            firestore.collection("users").document(user.uid).set(userDto, merge = true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
