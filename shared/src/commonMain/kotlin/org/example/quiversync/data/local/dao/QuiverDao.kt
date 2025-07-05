package org.example.quiversync.data.local.dao

import org.example.quiversync.SurfboardEntity
import org.example.quiversync.SurfboardQueries
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.utils.extensions.toLong

class QuiverDao(
    private val queries: SurfboardQueries
) {
    fun getMyQuiver(userId: String): Result<List<SurfboardEntity>> {
        return try {
            val boards = queries.getSurfboardsByOwnerId(userId).executeAsList()
            Result.success(boards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun addSurfboard(surfboard: SurfboardEntity): Result<Boolean> {
        return try {
            queries.insertOrReplaceSurfboard(
                id = surfboard.id,
                ownerId = surfboard.ownerId,
                model = surfboard.model,
                company = surfboard.company,
                type = surfboard.type,
                height = surfboard.height,
                width = surfboard.width,
                volume = surfboard.volume,
                finSetup = surfboard.finSetup,
                imageRes = surfboard.imageRes,
                latitude = surfboard.latitude,
                longitude = surfboard.longitude,
                addedDate = surfboard.addedDate,
                isRentalPublished = surfboard.isRentalPublished,
                isRentalAvailable = surfboard.isRentalAvailable,
                pricePerDay = surfboard.pricePerDay
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteSurfboard(surfboardId: String): Result<Boolean> {
        return try {
            queries.deleteSurfboardById(surfboardId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun publishForRental(surfboardId: String): Result<Boolean> {
        return try {
            queries.publishSurfboardForRental(surfboardId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun unpublishForRental(surfboardId: String): Result<Boolean> {
        return try {
            queries.unpublishSurfboardForRental(surfboardId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun getSurfboardById(surfboardId: String): Result<SurfboardEntity?> {
        return try {
            val surfboard = queries.getSurfboardById(surfboardId).executeAsOneOrNull()
            Result.success(surfboard)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateSurfboardRentalDetails(
        surfboardId: String,
        rentalsDetails: RentalPublishDetails
    ): Result<Boolean> {
        return try {
            queries.updateSurfboardRentalDetails(
                id = surfboardId,
                isRentalPublished = rentalsDetails.isRentalPublished?.toLong(),
                isRentalAvailable = rentalsDetails.isRentalAvailable?.toLong(),
                pricePerDay = rentalsDetails.pricePerDay,
                latitude = rentalsDetails.latitude,
                longitude = rentalsDetails.longitude
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}