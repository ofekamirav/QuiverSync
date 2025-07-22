package org.example.quiversync.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.remote.datasource.user.UserRemoteSource
import org.example.quiversync.domain.model.UserError
import org.example.quiversync.utils.Location
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toDto
import kotlin.onFailure
import kotlin.onSuccess

class UserRepositoryImpl(
    private val userRemoteSource: UserRemoteSource,
    private val userDao: UserDao,
    private val sessionManager: SessionManager,
    private val applicationScope: CoroutineScope
) : UserRepository {
    private var syncJob: Job? = null

    override suspend fun getUserProfile(): Flow<Result<User, Error>> {
        val uid = sessionManager.getUid() ?: return flowOf(Result.Failure(UserError("No UID")))

        startUserProfileSync(uid)

        return userDao.getUserProfile(uid)
            .map { user ->
                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Failure(UserError("User profile not available locally."))
                }
            }
            .catch { e ->
                emit(Result.Failure(UserError("DB error: ${e.message}")))
            }
    }

    private fun startUserProfileSync(uid: String) {
        if (syncJob?.isActive == true) {
            platformLogger("UserRepository", "User profile sync already in progress for UID: $uid")
            return
        }
        syncJob = applicationScope.launch {
            userRemoteSource.observeUserProfile(uid)
                .catch { e -> platformLogger("UserRepository", "User sync error: ${e.message}") }
                .collect { userFromFirestore ->
                    if (userFromFirestore != null) {
                        userDao.insertOrReplaceProfile(userFromFirestore, uid)
                    } else {
                        userDao.deleteProfile(uid)
                    }
                }
        }
    }
    override suspend fun updateUserProfile(user: User): Result<Unit, Error> {
        return try {
            val user = userRemoteSource.updateUserProfile(user)
            when(user){
                is Result.Success ->{
                    Result.Success(Unit)
                }
                is Result.Failure -> {
                    platformLogger("UserRepository", "Failed to update user profile: ${user.error?.message}")
                    Result.Failure(UserError("Update failed: ${user.error?.message}"))
                }
            }
        } catch (e: Exception) {
            platformLogger("UserRepository", "Error updating user profile: ${e.message}")
            Result.Failure(UserError("Update failed: ${e.message}"))
        }
    }


    override suspend fun deleteProfileLocal(uid: String) {
        userDao.deleteProfile(uid)
    }

    override suspend fun getUserCurrentLocation(): Result<Location, Error> {
        val latitude = sessionManager.getLatitude()
        val longitude = sessionManager.getLongitude()
        if (latitude == null || longitude == null) {
            return Result.Failure(UserError("Location not set"))
        }
        return Result.Success(Location(latitude, longitude))
    }

    override suspend fun updateUserCurrentLocation(location: Location): Result<Unit, Error> {
        return try {
            sessionManager.setLatitude(location.latitude)
            sessionManager.setLongitude(location.longitude)
            Result.Success(Unit)
        } catch (e: Exception) {
            platformLogger("UserRepository", "Failed to update user location: ${e.message}")
            Result.Failure(UserError("Update failed: ${e.message}"))
        }
    }

    override suspend fun getUserById(uid: String): Result<User, Error> {
        return try {
            val user = userDao.getUserById(uid)
            if (user != null) {
                Result.Success(user)
            } else {
                val remote = userRemoteSource.getUserById(uid)
                when (remote) {
                    is Result.Success -> {
                        if (remote.data == null) {
                            platformLogger("UserRepository", "User not found in remote source for UID: $uid")
                            return Result.Failure(UserError("User not found"))
                        }
                        userDao.insertOrReplaceProfile(remote.data, uid)
                        Result.Success(remote.data)
                    }

                    is Result.Failure -> {
                        platformLogger("UserRepository", "Failed to fetch user by ID: ${remote.error?.message}")
                        Result.Failure(UserError("Fetch failed: ${remote.error?.message}"))
                    }
                }
            }
        } catch (e: Exception) {
            platformLogger("UserRepository", "Error fetching user by ID: ${e.message}")
            Result.Failure(UserError("DB error: ${e.message}"))
        }
    }

    override suspend fun stopUserSync() {
        syncJob?.cancel()
        syncJob = null
        platformLogger("UserRepository", "User sync stopped.")
    }



}
