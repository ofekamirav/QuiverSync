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
import kotlinx.datetime.Clock
import org.example.quiversync.data.local.dao.UserDao
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.UserRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.dao.OwnersDao
import org.example.quiversync.data.remote.datasource.user.UserRemoteSource
import org.example.quiversync.domain.model.OwnerLocal
import org.example.quiversync.domain.model.UserError
import org.example.quiversync.utils.Location
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toDto
import org.example.quiversync.utils.extensions.toOwnerLocal
import kotlin.coroutines.cancellation.CancellationException
import kotlin.onFailure
import kotlin.onSuccess

class UserRepositoryImpl(
    private val userRemoteSource: UserRemoteSource,
    private val userDao: UserDao,
    private val ownersDao: OwnersDao,
    private val sessionManager: SessionManager,
    private val applicationScope: CoroutineScope
) : UserRepository {
    private var syncJob: Job? = null

    override suspend fun getUserProfile(): Flow<Result<User, Error>> {
        val uid = sessionManager.getUid() ?: return flowOf(Result.Failure(UserError("No UID")))

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

    override suspend fun startUserSync() {
        val uid = sessionManager.getUid() ?: run {
            platformLogger("UserRepository", "Cannot start sync: No user UID.")
            return
        }

        if (syncJob?.isActive == true) {
            platformLogger("UserRepository", "User profile sync already in progress.")
            return
        }

        syncJob = applicationScope.launch {
            platformLogger("UserRepository", "Starting user profile sync for UID: $uid")
            userRemoteSource.observeUserProfile(uid)
                .catch { e -> platformLogger("UserRepository", "User sync error: ${e.message}") }
                .collect { userFromFirestore ->
                    if (userFromFirestore != null) {
                        userDao.insertOrReplaceProfile(userFromFirestore, uid)
                    } else {
                        userDao.deleteProfile(uid)
                    }
                }
        }.also { job ->
            job.invokeOnCompletion { cause ->
                when (cause) {
                    null -> platformLogger("UserRepository", "User profile sync completed successfully.")
                    is CancellationException -> platformLogger("UserRepository", "User profile sync was cancelled.")
                    else -> platformLogger("UserRepository", "User profile sync failed: ${cause.message}")
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

    override suspend fun getOwnerById(id: String): Flow<Result<OwnerLocal, Error>> = flow {
        val cached = ownersDao.getOwnerById(id)

        val isStale = cached == null || cached.updatedAt?.let { Clock.System.now().toEpochMilliseconds() - it> 1000 * 60 * 60 * 24 } == true

        if (!isStale) {
            emit(Result.Success(cached))
            return@flow
        }

        when (val remote = userRemoteSource.getUserById(id)) {
            is Result.Success -> {
                val user = remote.data
                if (user != null) {
                    val owner = user.toOwnerLocal()
                    ownersDao.insertOwner(owner)
                    emit(Result.Success(owner))
                } else {
                    emit(Result.Failure(UserError("User not found")))
                }
            }

            is Result.Failure -> emit(Result.Failure(remote.error ?: UserError("Unknown error")))
        }
    }


    override suspend fun stopUserSync() {
        syncJob?.cancel()
        syncJob = null
        platformLogger("UserRepository", "User sync stopped.")
    }

}
