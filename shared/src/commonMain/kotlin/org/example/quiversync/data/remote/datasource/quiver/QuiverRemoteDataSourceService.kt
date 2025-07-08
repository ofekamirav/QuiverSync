    package org.example.quiversync.data.remote.datasource.quiver

    import dev.gitlive.firebase.firestore.FirebaseFirestore
    import org.example.quiversync.data.remote.dto.RentalPublishDetails
    import org.example.quiversync.data.remote.dto.SurfboardDto
    import org.example.quiversync.domain.model.Surfboard
    import org.example.quiversync.utils.extensions.platformLogger
    import org.example.quiversync.data.local.Result
    import org.example.quiversync.data.local.Error
    import org.example.quiversync.domain.model.SurfboardError
    import org.example.quiversync.utils.extensions.toDomain


    class QuiverRemoteDataSourceService(
        private val firebase: FirebaseFirestore,
    ): QuiverRemoteDataSource {
        override suspend fun getSurfboardsRemote(userId: String): Result<List<Surfboard>, Error> {
            return try {
                val querySnapshot = firebase.collection("surfboards").where {
                    "ownerId" equalTo userId
                }.get()
                val surfboardsWithIds = querySnapshot.documents.map { document ->
                    document.data<SurfboardDto>().toDomain(document.id)
                }
                platformLogger("QuiverRemoteDataSourceService", "Fetched ${surfboardsWithIds.size} surfboards for user $userId")
                Result.Success(surfboardsWithIds)
            } catch (e: Exception) {
                platformLogger("QuiverRemoteDataSourceService", "Error fetching surfboards: ${e.message}")
                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
            }
        }

        override suspend fun addSurfboardRemote(surfboard: SurfboardDto): Result<Surfboard,Error> {
            return try {
                val documentRef = firebase.collection("surfboards").add(surfboard)
                val addedSurfboard = surfboard.toDomain(documentRef.id)
                platformLogger("QuiverRemoteDataSourceService", "Surfboard added successfully with ID: ${addedSurfboard.id}")
                Result.Success(addedSurfboard)
            } catch (e: Exception) {
                platformLogger("QuiverRemoteDataSourceService", "Error adding surfboard: ${e.message}")
                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
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