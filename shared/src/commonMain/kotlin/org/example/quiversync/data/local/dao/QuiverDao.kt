package org.example.quiversync.data.local.dao

import org.example.quiversync.SurfboardEntity
import org.example.quiversync.SurfboardQueries
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toLong

class QuiverDao(
    private val queries: SurfboardQueries
) {
    fun getMyQuiver(userId: String): List<SurfboardEntity> {
        return try {
            queries.getSurfboardsByOwnerId(userId).executeAsList()
        } catch (e: Exception) {
            emptyList()
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

    fun publishForRental(surfboardId: String): Boolean {
        try {
            queries.publishSurfboardForRental(surfboardId)
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

    fun updateSurfboardRentalDetails(
        surfboardId: String,
        rentalsDetails: RentalPublishDetails
    ): Boolean {
        try {
            queries.updateSurfboardRentalDetails(
                id = surfboardId,
                isRentalPublished = rentalsDetails.isRentalPublished?.toLong(),
                isRentalAvailable = rentalsDetails.isRentalAvailable?.toLong(),
                pricePerDay = rentalsDetails.pricePerDay,
                latitude = rentalsDetails.latitude,
                longitude = rentalsDetails.longitude
            )
            return true
        } catch (e: Exception) {
            platformLogger("QuiverDao", "Error updating surfboard rental details: ${e.message}")
            return false
        }
    }

}