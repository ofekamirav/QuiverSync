package org.example.quiversync.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.firestore
import org.example.quiversync.data.remote.dto.UserDto
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.AuthError
import org.example.quiversync.utils.extensions.toDto
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.AuthRepository
import org.example.quiversync.utils.extensions.toDomain
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.dao.FavSpotDao
import org.example.quiversync.data.local.dao.GeminiPredictionDao
import org.example.quiversync.data.local.dao.QuiverDao
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.remote.dto.AuthResult
import org.example.quiversync.utils.extensions.platformLogger

class AuthRepositoryImpl(
    private val sessionManager: SessionManager,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val favSpotDao: FavSpotDao,
    private val quiverDao: QuiverDao,
    private val predictionDao: GeminiPredictionDao
): AuthRepository  {

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): Result<Unit,AuthError> {
        return try {
            val userCredential = auth.createUserWithEmailAndPassword(email, password)
            platformLogger("AuthRepositoryImpl", "User registered: ${userCredential.user?.email}")
            val uid = userCredential.user?.uid

            if (uid != null) {
                platformLogger("AuthRepositoryImpl", "User UID: $uid")
                val userDto = UserDto(email = email, name = name)
                firestore.collection("users").document(uid).set(userDto)
                sessionManager.setUid(uid)
                Result.Success(Unit)
            } else {
                Result.Failure(AuthError("Failed to create user: UID is null after registration."))
            }
        } catch (e: FirebaseAuthException) {
            Result.Failure(AuthError("Registration failed: ${e.message}"))
        } catch (e: FirebaseFirestoreException) {
            Result.Failure(AuthError("Failed to save user data after registration: ${e.message}"))
        } catch (e: Exception) {
            Result.Failure(AuthError("An unexpected error occurred during registration: ${e.message}"))
        }
    }

    override suspend fun getCurrentUser(): User?  {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            try {
                val snapshot = firestore
                    .collection("users")
                    .document(firebaseUser.uid)
                    .get()

                val userDto = snapshot.data<UserDto>()
                platformLogger("AuthRepositoryImpl", "Fetched user data for UID: ${firebaseUser.uid}")
                userDto.toDomain(firebaseUser.uid)
            } catch (e: Exception) {
                platformLogger("AuthRepositoryImpl", "Failed to fetch user data: ${e.message}")
                null
            }
        } else {
            null
        }
    }


    override suspend fun login(
        email: String,
        password: String,
    ): Result<Unit, AuthError> {
        return try {
            platformLogger("AuthRepositoryImpl", "Attempting to log in user with email: $email")
            val userCredential = auth.signInWithEmailAndPassword(email, password)
            platformLogger("AuthRepositoryImpl", "User logged in: ${userCredential.user?.email}")
            val uid = userCredential.user?.uid
            if (uid != null) {
                platformLogger("AuthRepositoryImpl", "User UID: $uid")
                sessionManager.setUid(uid)
                Result.Success(Unit)
            } else {
                platformLogger("AuthRepositoryImpl", "Login failed: UID is null after authentication.")
                Result.Failure(AuthError("Failed to login: UID is null after authentication."))
            }
        } catch (e: FirebaseAuthException) {
            platformLogger("AuthRepositoryImpl", "Login failed: ${e.message}")
            Result.Failure(AuthError("Login failed: ${e.message}"))
        } catch (e: Exception) {
            Result.Failure(AuthError("An unexpected error occurred during login: ${e.message}"))
        }
    }

    override suspend fun logout() {
        auth.signOut()
        clearLocalData()
    }

    private suspend fun clearLocalData(){
        val uid = sessionManager.getUid()
        sessionManager.clearUserData()
        if (uid != null) {
            userDao.deleteProfile(uid)
            favSpotDao.deleteAllFavSpots(uid)
            quiverDao.deleteAllSurfboardsByOwnerId(uid)
            predictionDao.deleteAllPredictions()
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit, AuthError> {
        return try {
            val userDto = user.toDto()
            platformLogger("AuthRepositoryImpl", "Updating user profile for UID: $userDto")
            firestore.collection("users").document(user.uid).set(userDto, merge = true)
            userDao.updateUserProfile(user, user.uid)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(AuthError("Failed to update user profile: ${e.message}"))
        }
    }

    override suspend fun isUserSignedInWithPassword(): Boolean {
        return try {
            val user = auth.currentUser
            if (user != null) {
                // Check if the user is signed in with email/password
                val providerData = user.providerData
                providerData.any { it.providerId == "password" }
            } else {
                false
            }
        } catch (e: Exception) {
            platformLogger("AuthRepositoryImpl", "Error checking user sign-in method: ${e.message}")
            false
        }
    }

    override suspend fun reauthenticate(password: String): Result<Unit, Error> {
        val user = auth.currentUser
        val email = user?.email

        if (user == null || email.isNullOrEmpty()) {
            return Result.Failure(AuthError("User not logged in."))
        }

        return try {
            val credential = EmailAuthProvider.credential(email, password)
            user.reauthenticate(credential)
            Result.Success(Unit)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Failure(AuthError("The password you entered is incorrect."))
        } catch (e: Exception) {
            Result.Failure(AuthError("Re-authentication failed: ${e.message}"))
        }
    }
    override suspend fun updatePassword(newPassword: String): Result<Unit, Error> {
        val user = auth.currentUser ?: return Result.Failure(AuthError("User not logged in."))

        return try {
            user.updatePassword(newPassword)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(AuthError("Failed to update password: ${e.message}"))
        }
    }
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit, Error> {
        return try {
            auth.sendPasswordResetEmail(email)
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            val message = e.message ?: ""
            val errorMessage = when {
                message.contains("USER_NOT_FOUND", ignoreCase = true) ->
                    "No account was found with this email address."

                message.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) ->
                    "Password reset is not enabled for this account. Please sign in with Google or Apple."

                else -> message
            }
            Result.Failure(AuthError(errorMessage))
        } catch (e: Exception) {
            Result.Failure(AuthError("An unexpected error occurred. Please check your network connection."))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthResult, Error> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, null)
            val authResult = auth.signInWithCredential(credential)
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                return Result.Failure(AuthError("Failed to get user info after sign-in."))
            }

            sessionManager.setUid(firebaseUser.uid)
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                val newUserProfile = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: "",
                    profilePicture = firebaseUser.photoURL,
                    heightCm = null,
                    weightKg = null,
                    surfLevel = null,
                    dateOfBirth = null
                )
                updateUserProfile(newUserProfile)
            }
            Result.Success(AuthResult(isNewUser = isNewUser))
        } catch (e: Exception) {
            Result.Failure(AuthError("Google Sign-In failed: ${e.message}"))
        }
    }
}
