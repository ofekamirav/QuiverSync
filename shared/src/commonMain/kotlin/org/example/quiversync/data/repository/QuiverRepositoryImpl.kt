package org.example.quiversync.data.repository

import androidx.compose.ui.geometry.Rect
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.QuiverDao
import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSource
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardError
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toDomain
import org.example.quiversync.utils.extensions.toDto



class QuiverRepositoryImpl(
    private val remoteDataSource: QuiverRemoteDataSource,
    private val localDataSource: QuiverDao,
    private val sessionManager: SessionManager
): QuiverRepository {
    override suspend fun getMyQuiver(): Result<List<Surfboard>, Error> {
        return try {
            val userId = sessionManager.getUid() ?: return Result.Failure(SurfboardError("User not logged in"))

            val localResult = localDataSource.getMyQuiver(userId)
            if (localResult.isEmpty()) {
                platformLogger("QuiverRepositoryImpl", "Local quiver is empty, fetching from remote source")
            } else {
                val localBoardsDomain = localResult.map { it.toDomain() }
                return Result.Success(localBoardsDomain)
            }

            val remoteResult = remoteDataSource.getSurfboardsRemote(userId)

            return when (remoteResult) {
                is Result.Success -> {
                    val remoteBoards = remoteResult.data.orEmpty()
                    platformLogger("QuiverRepositoryImpl", "Fetched ${remoteBoards.size} surfboards from remote source")
                remoteBoards.forEach { board ->
                        try {
                            localDataSource.addSurfboard(board)
                        } catch (e: Exception) {
                            platformLogger("QuiverRepositoryImpl", "Warning: Failed to save surfboard to local DB: ${e.message}")
                        }
                    }
                    Result.Success(remoteBoards)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to fetch from remote: ${remoteResult.error?.message}")
                    Result.Failure(remoteResult.error ?: SurfboardError("Remote fetch failed with no specific error."))
                }
            }
        } catch (e: FirebaseFirestoreException) {
            platformLogger("QuiverRepositoryImpl", "Firebase Firestore exception in getMyQuiver: ${e.message}")
            Result.Failure(SurfboardError("Firebase error: ${e.message}"))
        } catch (e: Exception) {
            platformLogger("QuiverRepositoryImpl", "Unknown exception in getMyQuiver: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "An unknown error occurred while fetching the quiver"))
        }
    }


    override suspend fun addSurfboard(surfboard: Surfboard): Result<Boolean, Error> {
        return try {
            val userId = sessionManager.getUid() ?: return Result.Failure<SurfboardError>(SurfboardError("User not logged in"))
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val formattedDate = "${now.dayOfMonth.toString().padStart(2, '0')}/${now.monthNumber.toString().padStart(2, '0')}/${now.year}"
            val surfboard = surfboard.copy(ownerId = userId, addedDate = formattedDate)
            val result = remoteDataSource.addSurfboardRemote(surfboard.toDto())
            when (result) {
                is Result.Success -> {
                    platformLogger("QuiverRepositoryImpl", "Surfboard added remotely: ${surfboard.model} by ${surfboard.company}")
                    result.data?.let { localDataSource.addSurfboard(it) }
                    return Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to add surfboard remotely: ${result.error?.message}")
                    return Result.Failure(SurfboardError(result.error?.message ?: "Failed to add surfboard remotely"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while adding the surfboard"))
        }
    }

    override suspend fun deleteSurfboard(surfboardId: String): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.deleteSurfboardRemote(surfboardId)
            when (result) {
                is Result.Success -> {
                    localDataSource.deleteSurfboard(surfboardId)
                    platformLogger("QuiverRepositoryImpl", "Surfboard deleted successfully: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to delete surfboard remotely: ${result.error?.message}")
                    Result.Failure(SurfboardError(result.error?.message ?: "Failed to delete surfboard remotely"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while deleting the surfboard"))
        }
    }

    override suspend fun publishForRental(
        surfboardId: String,
        rentalsDetails: RentalPublishDetails
        ): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.publishForRentalRemote(surfboardId, rentalsDetails)
            when(result) {
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to publish surfboard for rental remotely: ${result.error?.message}")
                    return Result.Failure(SurfboardError(result.error?.message ?: "Failed to publish surfboard for rental remotely"))
                }
                is Result.Success -> {
                    platformLogger(
                        "QuiverRepositoryImpl",
                        "Surfboard published for rental remotely: $surfboardId"
                    )
                    localDataSource.publishForRental(surfboardId, rentalsDetails)
                    Result.Success(true)

                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while publishing the surfboard for rental"))
        }
    }

    override suspend fun unpublishForRental(surfboardId: String): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.unpublishForRentalRemote(surfboardId)
            when (result) {
                is Result.Success -> {
                    localDataSource.unpublishForRental(surfboardId)
                    platformLogger("QuiverRepositoryImpl", "Surfboard unpublished from rental successfully: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to unpublish surfboard from rental remotely: ${result.error?.message}")
                    Result.Failure(SurfboardError(result.error?.message ?: "Failed to unpublish surfboard from rental remotely"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while unpublishing the surfboard for rental"))
        }
    }

    override suspend fun setSurfboardAsRentalAvailable(surfboardId: String): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.setSurfboardAsRentalAvailableRemote(surfboardId)
            when (result) {
                is Result.Success -> {
                    localDataSource.setSurfboardAsAvailableForRental(surfboardId)
                    platformLogger("QuiverRepositoryImpl", "Surfboard set as available for rental successfully: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to set surfboard as available for rental remotely: ${result.error?.message}")
                    Result.Failure(SurfboardError(result.error?.message ?: "Failed to set surfboard as available for rental remotely"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while setting the surfboard as available for rental"))
        }
    }

    override suspend fun setSurfboardAsRentalUnavailable(surfboardId: String): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.setSurfboardAsRentalUnavailableRemote(surfboardId)
            when (result) {
                is Result.Success -> {
                    localDataSource.setSurfboardAsUnavailableForRental(surfboardId)
                    platformLogger("QuiverRepositoryImpl", "Surfboard set as unavailable for rental successfully: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to set surfboard as unavailable for rental remotely: ${result.error?.message}")
                    Result.Failure(SurfboardError(result.error?.message ?: "Failed to set surfboard as unavailable for rental remotely"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while setting the surfboard as unavailable for rental"))
        }
    }

}