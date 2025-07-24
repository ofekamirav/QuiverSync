package org.example.quiversync.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.dao.QuiverDao
import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSource
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardError
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toDto

class QuiverRepositoryImpl(
    private val remoteDataSource: QuiverRemoteDataSource,
    private val localDataSource: QuiverDao,
    private val sessionManager: SessionManager,
    private val applicationScope: CoroutineScope
) : QuiverRepository {

    private var quiverSyncJob: Job? = null

    override suspend fun getMyQuiver(): Flow<Result<List<Surfboard>, Error>> {
        val userId = sessionManager.getUid()
            ?: return flowOf(Result.Failure(SurfboardError("User not logged in")))

        return localDataSource.getMyQuiver(userId)
            .map<List<Surfboard>, Result<List<Surfboard>, Error>> { boards ->
                Result.Success(boards)
            }
            .catch { e ->
                emit(Result.Failure(SurfboardError("DB Error: ${e.message}")))
            }
    }

    override suspend fun startQuiverSync() {
        if (quiverSyncJob?.isActive == true) return

        val userId = sessionManager.getUid() ?: return
        quiverSyncJob = applicationScope.launch {
            remoteDataSource.observeQuiver(userId)
                .catch { e -> platformLogger("QuiverRepo", "Quiver sync error: ${e.message}") }
                .collect { remoteBoards ->
                    syncRemoteToLocal(userId, remoteBoards)
                }
        }
        platformLogger("QuiverRepositoryImpl", "Core sync for Quiver has started.")
    }

    private suspend fun syncRemoteToLocal(userId: String, remoteBoards: List<Surfboard>) {
        val localBoards = localDataSource.getMyQuiver(userId).firstOrNull().orEmpty()
        val remoteIds = remoteBoards.map { it.id }.toSet()
        val localIds = localBoards.map { it.id }.toSet()

        localDataSource.transaction {
            (localIds - remoteIds).forEach { localDataSource.deleteSurfboard(it) }
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
            val userId = sessionManager.getUid()
                ?: return Result.Failure(SurfboardError("User not logged in"))

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val formattedDate = "${now.dayOfMonth.toString().padStart(2,'0')}/" +
                    "${now.monthNumber.toString().padStart(2,'0')}/${now.year}"
            val boardWithDate = surfboard.copy(ownerId = userId, addedDate = formattedDate)

            when (val remoteRes = remoteDataSource.addSurfboardRemote(boardWithDate.toDto())) {
                is Result.Success -> {
                    platformLogger("QuiverRepositoryImpl", "Surfboard added remotely: ${boardWithDate.model}")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to add surfboard: ${remoteRes.error?.message}")
                    Result.Failure(SurfboardError(remoteRes.error?.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError("Error adding surfboard: ${e.message}"))
        }
    }

    override suspend fun deleteSurfboard(surfboardId: String): Result<Boolean, Error> {
        return try {
            when (val remoteRes = remoteDataSource.deleteSurfboardRemote(surfboardId)) {
                is Result.Success -> {
                    platformLogger("QuiverRepositoryImpl", "Surfboard deleted: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to delete surfboard: ${remoteRes.error?.message}")
                    Result.Failure(SurfboardError(remoteRes.error?.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError("Error deleting surfboard: ${e.message}"))
        }
    }

    override suspend fun publishForRental(
        surfboardId: String,
        rentalsDetails: RentalPublishDetails
    ): Result<Boolean, Error> {
        return try {
            when (val remoteRes = remoteDataSource.publishForRentalRemote(surfboardId, rentalsDetails)) {
                is Result.Success -> {
                    platformLogger("QuiverRepositoryImpl", "Published for rental: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to publish for rental: ${remoteRes.error?.message}")
                    Result.Failure(SurfboardError(remoteRes.error?.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError("Error publishing for rental: ${e.message}"))
        }
    }

    override suspend fun unpublishForRental(surfboardId: String): Result<Boolean, Error> {
        return try {
            when (val remoteRes = remoteDataSource.unpublishForRentalRemote(surfboardId)) {
                is Result.Success -> {
                    platformLogger("QuiverRepositoryImpl", "Unpublished for rental: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed to unpublish for rental: ${remoteRes.error?.message}")
                    Result.Failure(SurfboardError(remoteRes.error?.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError("Error unpublishing for rental: ${e.message}"))
        }
    }

    override suspend fun setSurfboardAsRentalAvailable(surfboardId: String): Result<Boolean, Error> {
        return try {
            when (val remoteRes = remoteDataSource.setSurfboardAsRentalAvailableRemote(surfboardId)) {
                is Result.Success -> {
                    platformLogger("QuiverRepositoryImpl", "Set available for rental: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed set available: ${remoteRes.error?.message}")
                    Result.Failure(SurfboardError(remoteRes.error?.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError("Error setting available: ${e.message}"))
        }
    }

    override suspend fun setSurfboardAsRentalUnavailable(surfboardId: String): Result<Boolean, Error> {
        return try {
            when (val remoteRes = remoteDataSource.setSurfboardAsRentalUnavailableRemote(surfboardId)) {
                is Result.Success -> {
                    platformLogger("QuiverRepositoryImpl", "Set unavailable for rental: $surfboardId")
                    Result.Success(true)
                }
                is Result.Failure -> {
                    platformLogger("QuiverRepositoryImpl", "Failed set unavailable: ${remoteRes.error?.message}")
                    Result.Failure(SurfboardError(remoteRes.error?.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError("Error setting unavailable: ${e.message}"))
        }
    }
}
