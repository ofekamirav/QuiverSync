package org.example.quiversync.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.quiversync.SurfboardEntity
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.QuiverDao
import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSource
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardError
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.utils.LocationProvider
import org.example.quiversync.utils.extensions.toDomain
import org.example.quiversync.utils.extensions.toDto
import org.example.quiversync.utils.extensions.toEntity



class QuiverRepositoryImpl(
    private val remoteDataSource: QuiverRemoteDataSource,
    private val localDataSource: QuiverDao,
    private val sessionManager: SessionManager
): QuiverRepository {
    override suspend fun getMyQuiver(): Result<List<Surfboard>, Error> {
        return try {
            val userId = sessionManager.getUid() ?: return Result.Failure<SurfboardError>(SurfboardError("User not logged in"))

            val localResult = localDataSource.getMyQuiver(userId)
            if (localResult.isSuccess) {
                val localBoards = localResult.getOrNull().orEmpty()
                if (localBoards.isNotEmpty()) {
                    return Result.Success(localBoards.map { it.toDomain() })
                }
            }

            val remoteBoards = remoteDataSource.getSurfboardsRemote(userId)
            remoteBoards.forEach { board ->
                localDataSource.addSurfboard(board.toEntity())
            }
            return Result.Success(remoteBoards.map { it.toDomain() })


        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while fetching the quiver"))
        }
    }


    override suspend fun addSurfboard(surfboard: Surfboard): Result<Boolean, Error> {
        return try {
            val userId = sessionManager.getUid() ?: return Result.Failure<SurfboardError>(SurfboardError("User not logged in"))
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val formattedDate = "${now.dayOfMonth.toString().padStart(2, '0')}/${now.monthNumber.toString().padStart(2, '0')}/${now.year}"
            val surfboard = surfboard.copy(ownerId = userId, addedDate = formattedDate)
            val result = remoteDataSource.addSurfboardRemote(surfboard.toDto())
            if (result) {
                localDataSource.addSurfboard(surfboard.toEntity())
                Result.Success(true)
            }
            else {
                Result.Failure(SurfboardError("Failed to add surfboard remotely"))
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while adding the surfboard"))
        }
    }

    override suspend fun deleteSurfboard(surfboardId: String): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.deleteSurfboardRemote(surfboardId)
            if (result) {
                localDataSource.deleteSurfboard(surfboardId)
                Result.Success(true)
            } else {
                Result.Failure(SurfboardError("Failed to delete surfboard remotely"))
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while deleting the surfboard"))
        }
    }

    override suspend fun publishForRental(surfboardId: String): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.publishForRentalRemote(surfboardId)
            if (result) {
                localDataSource.publishForRental(surfboardId)
                Result.Success(true)
            } else {
                Result.Failure(SurfboardError("Failed to publish surfboard for rental remotely"))
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while publishing the surfboard for rental"))
        }
    }

    override suspend fun unpublishForRental(surfboardId: String): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.unpublishForRentalRemote(surfboardId)
            if (result) {
                localDataSource.unpublishForRental(surfboardId)
                Result.Success(true)
            } else {
                Result.Failure(SurfboardError("Failed to unpublish surfboard for rental remotely"))
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while unpublishing the surfboard for rental"))
        }
    }

    override suspend fun updateSurfboardRentalDetails(
        surfboardId: String,
        rentalsDetails: RentalPublishDetails,
    ): Result<Boolean, Error> {
        return try {
            val result = remoteDataSource.updateSurfboardRentalDetailsRemote(surfboardId, rentalsDetails)
            if (result) {
                localDataSource.updateSurfboardRentalDetails(surfboardId, rentalsDetails)
                Result.Success(true)
            } else {
                Result.Failure(SurfboardError("Failed to update surfboard rental details remotely"))
            }
        } catch (e: Exception) {
            Result.Failure(SurfboardError(e.message ?: "An error occurred while updating the surfboard rental details"))
        }
    }
}