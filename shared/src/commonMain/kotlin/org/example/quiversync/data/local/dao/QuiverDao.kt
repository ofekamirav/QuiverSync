package org.example.quiversync.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.quiversync.SurfboardEntity
import org.example.quiversync.SurfboardQueries
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toDomain
import org.example.quiversync.utils.extensions.toLong

class QuiverDao(
    private val queries: SurfboardQueries
) {
    fun getMyQuiver(userId: String): Flow<List<Surfboard>> {
        return queries.getSurfboardsByOwnerId(userId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    fun addSurfboard(surfboard: Surfboard): Boolean {
        try {
            queries.insertOrReplaceSurfboard(
                id = surfboard.id,
                ownerId = surfboard.ownerId,
                model = surfboard.model,
                company = surfboard.company,
                type = surfboard.type.serverName,
                height = surfboard.height,
                width = surfboard.width,
                volume = surfboard.volume,
                finSetup = surfboard.finSetup.serverName,
                imageRes = surfboard.imageRes,
                latitude = surfboard.latitude,
                longitude = surfboard.longitude,
                addedDate = surfboard.addedDate,
                isRentalPublished = surfboard.isRentalPublished?.toLong(),
                isRentalAvailable = surfboard.isRentalAvailable?.toLong(),
                pricePerDay = surfboard.pricePerDay
            )
            return true
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error adding surfboard: ${e.message}")
        }
        return false
    }

    fun deleteSurfboard(surfboardId: String): Boolean {
        try {
            queries.deleteSurfboardById(surfboardId)
            return true
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error deleting surfboard: ${e.message}")
            return false
        }
    }

    fun deleteAllSurfboardsByOwnerId(ownerId: String): Boolean {
        try {
            queries.deleteAllSurfboardsByOwnerId(ownerId)
            return true
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error deleting all surfboards by owner ID: ${e.message}")
            return false
        }
    }

    fun publishForRental(
        surfboardId: String,
        rentalsDetails: RentalPublishDetails
    ): Boolean {
        try {
            queries.publishSurfboardForRental(
                id = surfboardId,
                pricePerDay = rentalsDetails.pricePerDay,
                latitude = rentalsDetails.latitude,
                longitude = rentalsDetails.longitude
            )
            return true
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error publishing surfboard for rental: ${e.message}")
            return false
        }
    }

    fun unpublishForRental(surfboardId: String): Boolean {
        try {
            queries.unpublishSurfboardForRental(surfboardId)
            return true
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error unpublishing surfboard for rental: ${e.message}")
            return false
        }
    }
    fun getSurfboardById(surfboardId: String): SurfboardEntity? {
        try {
            val surfboard = queries.getSurfboardById(surfboardId).executeAsOneOrNull()
            return surfboard
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error fetching surfboard by ID: ${e.message}")
            return null
        }
    }
    fun setSurfboardAsUnavailableForRental(surfboardId: String){
        try {
            queries.setSurfboardAsUnavailableForRental(surfboardId)
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error setting surfboard as unavailable for rental: ${e.message}")
        }
    }
    fun setSurfboardAsAvailableForRental(surfboardId: String){
        try {
            queries.setSurfboardAsAvailableForRental(surfboardId)
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error setting surfboard as available for rental: ${e.message}")
        }
    }

    fun getBoardsNumber(userId: String): Int {
        return try {
            queries.getSurfboardsByOwnerId(userId).executeAsList().size
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error fetching boards number: ${e.message}")
            0
        }
    }
    fun transaction(block: () -> Unit) {
        queries.transaction {
            block()
        }
    }
}