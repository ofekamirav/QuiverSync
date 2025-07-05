package org.example.quiversync.data.remote.datasource.quiver

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.data.remote.dto.SurfboardDto
import org.example.quiversync.utils.extensions.platformLogger

class QuiverRemoteDataSourceService(
    private val firebase: FirebaseFirestore,
): QuiverRemoteDataSource {
    override suspend fun getSurfboardsRemote(userId: String): List<SurfboardDto> {
        return try {
            val snapshot = firebase
                .collection("surfboards")
                .where("ownerId", "==", userId)
                .get()
            snapshot.documents.map { it.data<SurfboardDto>() }
        } catch (e: Exception) {
            platformLogger("QuiverRemoteDataSourceService","Error fetching surfboards for user $userId")
            emptyList()
        }
    }

    override suspend fun addSurfboardRemote(surfboard: SurfboardDto): Boolean {
        return try {
            firebase.collection("surfboards").add(surfboard)
            platformLogger("QuiverRemoteDataSourceService", "Surfboard added successfully")
            true
        } catch (e: Exception) {
            platformLogger("QuiverRemoteDataSourceService", "Error adding surfboard: ${e.message}")
            false
        }
    }

    override suspend fun deleteSurfboardRemote(surfboardId: String): Boolean {
        return try {
            firebase.collection("surfboards").document(surfboardId).delete()
            platformLogger("QuiverRemoteDataSourceService", "Surfboard deleted successfully")
            true
        } catch (e: Exception) {
            platformLogger("QuiverRemoteDataSourceService", "Error deleting surfboard: ${e.message}")
            false
        }
    }

    override suspend fun publishForRentalRemote(surfboardId: String): Boolean {
        return try {
            firebase.collection("surfboards").document(surfboardId).update("isAvailable", true)
            platformLogger("QuiverRemoteDataSourceService", "Surfboard published for rental successfully")
            true
        } catch (e: Exception) {
            platformLogger("QuiverRemoteDataSourceService", "Error publishing surfboard for rental: ${e.message}")
            false
        }
    }

    override suspend fun unpublishForRentalRemote(surfboardId: String): Boolean {
        return try {
            firebase.collection("surfboards").document(surfboardId).update("isAvailable", false)
            platformLogger("QuiverRemoteDataSourceService", "Surfboard unpublished for rental successfully")
            true
        } catch (e: Exception) {
            platformLogger("QuiverRemoteDataSourceService", "Error unpublishing surfboard for rental: ${e.message}")
            false
        }
    }

    override suspend fun updateSurfboardRentalDetailsRemote(
        surfboardId: String,
        rentalsDetails: RentalPublishDetails,
    ): Boolean {
        return try {
            firebase.collection("surfboards").document(surfboardId).update(
                "latitude" to rentalsDetails.latitude,
                "longitude" to rentalsDetails.longitude,
                "isRentalPublished" to rentalsDetails.isRentalPublished,
                "isRentalAvailable" to rentalsDetails.isRentalAvailable,
                "pricePerDay" to rentalsDetails.pricePerDay
            )
            platformLogger("QuiverRemoteDataSourceService", "Surfboard rental details updated successfully")
            true
        } catch (e: Exception) {
            platformLogger("QuiverRemoteDataSourceService", "Error updating surfboard rental details: ${e.message}")
            false
        }
    }
}