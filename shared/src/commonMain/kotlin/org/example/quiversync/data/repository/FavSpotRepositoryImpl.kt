package org.example.quiversync.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.FavSpotDao
import org.example.quiversync.data.remote.datasource.favSpot.FavSpotRemoteSource
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.SpotsError
import org.example.quiversync.domain.repository.FavSpotRepository
import org.example.quiversync.utils.extensions.platformLogger

class FavSpotRepositoryImpl(
    private val dao: FavSpotDao,
    private val remoteDataSource : FavSpotRemoteSource,
    private val sessionManager: SessionManager,
    private val applicationScope: CoroutineScope
    ) : FavSpotRepository {
    private var syncJob: Job? = null

    override fun getAllFavSpots(): Flow<Result<List<FavoriteSpot>, Error>> {
        return flow {
            val userId = sessionManager.getUid()
                ?: return@flow emit(Result.Failure(SpotsError("User not logged in")))

            startRealtimeSync()

            emitAll(
                dao.selectAllFavSpots(userId).map{ spots ->
                    spots.sortedBy { it.name.lowercase() }
                }
                .map<List<FavoriteSpot>, Result<List<FavoriteSpot>, Error>> { sortedSpots ->
                    Result.Success(sortedSpots)
                }
                .catch { e ->
                    emit(Result.Failure(SpotsError("DB read error: ${e.message}")))
                }
            )
        }
    }


    private suspend fun startRealtimeSync() {
        if (syncJob?.isActive == true) {
            return
        }

        val userId = sessionManager.getUid() ?: return

        syncJob = applicationScope.launch {
//            platformLogger("FavSpotRepo", "Starting Firestore sync for user $userId")
            remoteDataSource.observeFavSpots(userId)
                .catch { e -> platformLogger("FavSpotRepo", "Sync Error: ${e.message}") }
                .collect { remoteSpots ->
//                    platformLogger("FavSpotRepo", "Syncing ${remoteSpots.size} spots from remote.")
                    syncRemoteToLocal(userId, remoteSpots)
                }
        }
    }
    override suspend fun stopRealtimeSync() {
        syncJob?.cancel()
        syncJob = null
//        platformLogger("FavSpotRepo", "Favorite spots sync stopped.")
    }


    private suspend fun syncRemoteToLocal(userId: String, remoteSpots: List<FavoriteSpot>) {
        val localSpots = dao.selectAllFavSpots(userId).firstOrNull().orEmpty()

        val remoteSpotIds = remoteSpots.map { it.spotID }.toSet()
        val localSpotIds = localSpots.map { it.spotID }.toSet()
        val spotsToDelete = localSpotIds - remoteSpotIds

        dao.transaction {
            if (spotsToDelete.isNotEmpty()) {
//                platformLogger("FavSpotRepo", "Deleting ${spotsToDelete.size} spots.")
                spotsToDelete.forEach { dao.deleteFavSpot(it) }
            }

            if (remoteSpots.isNotEmpty()) {
//                platformLogger("FavSpotRepo", "Inserting/Updating ${remoteSpots.size} spots.")
                remoteSpots.forEach { dao.insertFavSpot(it) }
            }
        }
        platformLogger("FavSpotRepo", "Sync complete.")
    }


    override suspend fun addFavSpot(favSpot: FavoriteSpot): Result<Unit, Error> {
        return try {
            val userId = sessionManager.getUid() ?: return Result.Failure(SpotsError("User not logged in"))
            when (val result = remoteDataSource.addFavSpotRemote(favSpot, userId)) {
                is Result.Success -> {
                    if (result.data == null) {
                        return Result.Failure(SpotsError("Failed to add spot: Remote returned null"))
                    }
                    dao.insertFavSpot(result.data)
                    Result.Success(Unit)
                }
                is Result.Failure -> Result.Failure(SpotsError(result.error?.message ?: "Failed to add spot"))
            }
        } catch (e: Exception) {
            Result.Failure(SpotsError("Error adding favorite spot: ${e.message}"))
        }
    }

    override suspend fun removeFavSpot(favSpot: FavoriteSpot): Result<Unit, Error> {
        return try {
            when (val result = remoteDataSource.deleteFavSpotRemote(favSpot.spotID)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Failure -> Result.Failure(SpotsError(result.error?.message ?: "Failed to remove spot"))
            }
        } catch (e: Exception) {
            Result.Failure(SpotsError("Error removing favorite spot: ${e.message}"))
        }
    }


    override suspend fun clearAllSpots(): Result<Unit, Error> {
        return try {
            val userId = sessionManager.getUid() ?: return Result.Failure(SpotsError("User not logged in"))

            val allSpots = dao.selectAllFavSpots(userId).firstOrNull() ?: emptyList()

            allSpots.forEach { spot ->
                remoteDataSource.deleteFavSpotRemote(spot.spotID)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(SpotsError("Error clearing all spots: ${e.message}"))
        }
    }
}
