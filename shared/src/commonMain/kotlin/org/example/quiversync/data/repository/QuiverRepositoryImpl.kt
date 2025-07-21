package org.example.quiversync.data.repository

import androidx.compose.ui.geometry.Rect
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
    private val sessionManager: SessionManager,
    private val applicationScope: CoroutineScope
): QuiverRepository {
    private var quiverSyncJob: Job? = null

    override suspend fun getMyQuiver(): Flow<Result<List<Surfboard>, Error>> {
        return flow {
            val userId = sessionManager.getUid()
                ?: return@flow emit(Result.Failure(SurfboardError("User not logged in")))

            startQuiverSync()

            emitAll(
                localDataSource.getMyQuiver(userId)
                    .map<List<Surfboard>, Result<List<Surfboard>, Error>> { boards ->
                        Result.Success(boards)
                    }
                    .catch { e ->
                        emit(Result.Failure(SurfboardError("DB Error: ${e.message}")))
                    }
            )
        }
    }

    private suspend fun startQuiverSync() {
        if (quiverSyncJob?.isActive == true) return

        val userId = sessionManager.getUid() ?: return
        quiverSyncJob = applicationScope.launch {
            remoteDataSource.observeQuiver(userId)
                .catch { e -> platformLogger("QuiverRepo", "Quiver sync error: ${e.message}") }
                .collect { remoteBoards ->
                    syncRemoteToLocal(userId, remoteBoards)
                }
        }
    }

    private suspend fun syncRemoteToLocal(userId: String, remoteBoards: List<Surfboard>) {
        val localBoards = localDataSource.getMyQuiver(userId).firstOrNull().orEmpty()
        val remoteBoardIds = remoteBoards.map { it.id }.toSet()
        val localBoardIds = localBoards.map { it.id }.toSet()

        localDataSource.transaction {
            val boardsToDelete = localBoardIds - remoteBoardIds
            boardsToDelete.forEach { localDataSource.deleteSurfboard(it) }
            remoteBoards.forEach { localDataSource.addSurfboard(it) }
        }
        platformLogger("QuiverRepo", "Quiver sync complete.")
    }

    override suspend fun stopQuiverSync() {
        quiverSyncJob?.cancel()
        quiverSyncJob = null
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

    override suspend fun getBoardById(surfboardId: String): Result<Surfboard, Error> {
        return try{
            val surfboardEntity = localDataSource.getSurfboardById(surfboardId)
                ?: return Result.Failure(SurfboardError("Surfboard not found with ID: $surfboardId"))

            val surfboard = surfboardEntity.toDomain()
            platformLogger("QuiverRepositoryImpl", "Fetched surfboard by ID: ${surfboard.id}")
            Result.Success(surfboard)
        }
        catch ( e: Exception) {
            platformLogger("QuiverRepositoryImpl", "Error fetching surfboard by ID: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "An error occurred while fetching the surfboard by ID"))
        }
    }

    override suspend fun getAvailableSurfboards(userId: String): Result<List<Surfboard>, Error> {
        return try{
            when (val result = remoteDataSource.getAvailableSurfboardsRemote(userId)) {
                is Result.Success -> {
                    platformLogger("QuiverRepositoryImpl", "Fetched available surfboards for user: $userId")
                    Result.Success(result.data)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to fetch available surfboards: ${result.error?.message}")
                    Result.Failure(SurfboardError(result.error?.message ?: "Failed to fetch available surfboards"))
                }
            }
        }
        catch ( e: Exception) {
            platformLogger("QuiverRepositoryImpl", "Error fetching available surfboards: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "An error occurred while fetching available surfboards"))
        }
    }


}