    package org.example.quiversync.data.remote.datasource.quiver

    import dev.gitlive.firebase.firestore.FirebaseFirestore
    import dev.gitlive.firebase.firestore.where
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.map
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
        override fun observeQuiver(userId: String): Flow<List<Surfboard>> {
            return firebase.collection("surfboards")
                .where { "ownerId" equalTo userId }
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.data<SurfboardDto>().toDomain(doc.id)
                        } catch (e: Exception) {
                            platformLogger("QuiverRemote", "Failed to parse surfboard ${doc.id}")
                            null
                        }
                    }
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

        override suspend fun deleteSurfboardRemote(surfboardId: String): Result<Boolean, Error> {
            return try {
                firebase.collection("surfboards").document(surfboardId).delete()
                platformLogger("QuiverRemoteDataSourceService", "Surfboard deleted successfully with ID: $surfboardId")
                Result.Success(true)
            } catch (e: Exception) {
                platformLogger("QuiverRemoteDataSourceService", "Error deleting surfboard: ${e.message}")
                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
            }
        }

        override suspend fun publishForRentalRemote(surfboardId: String,rentalsDetails: RentalPublishDetails): Result<Boolean, Error> {
            return try {
                firebase.collection("surfboards").document(surfboardId).update(
                    "latitude" to rentalsDetails.latitude,
                    "longitude" to rentalsDetails.longitude,
                    "isRentalPublished" to true,
                    "isRentalAvailable" to true,
                    "pricePerDay" to rentalsDetails.pricePerDay,
                )
                platformLogger("QuiverRemoteDataSourceService", "Surfboard published for rental successfully")
                Result.Success(true)
            } catch (e: Exception) {
                platformLogger("QuiverRemoteDataSourceService", "Error publishing surfboard for rental: ${e.message}")
                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
            }
        }

        override suspend fun unpublishForRentalRemote(surfboardId: String): Result<Boolean, Error> {
            return try {
                firebase.collection("surfboards").document(surfboardId).update(
                    "isRentalPublished" to false,
                    "isRentalAvailable" to null,
                    "pricePerDay" to null,
                    "latitude" to null,
                    "longitude" to null
                )
                platformLogger("QuiverRemoteDataSourceService", "Surfboard unpublished for rental successfully")
                Result.Success(true)
            } catch (e: Exception) {
                platformLogger("QuiverRemoteDataSourceService", "Error unpublishing surfboard for rental: ${e.message}")
                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
            }
        }

        override suspend fun setSurfboardAsRentalAvailableRemote(surfboardId: String): Result<Boolean, Error> {
            return try {
                firebase.collection("surfboards").document(surfboardId).update(
                    "isRentalAvailable" to true
                )
                platformLogger(
                    "QuiverRemoteDataSourceService",
                    "Surfboard set as rental available successfully"
                )
                Result.Success(true)
            } catch (e: Exception) {
                platformLogger(
                    "QuiverRemoteDataSourceService",
                    "Error setting surfboard as rental available: ${e.message}"
                )
                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
            }
        }

        override suspend fun setSurfboardAsRentalUnavailableRemote(surfboardId: String): Result<Boolean, Error> {
            return try {
                firebase.collection("surfboards").document(surfboardId).update(
                    "isRentalAvailable" to false
                )
                platformLogger(
                    "QuiverRemoteDataSourceService",
                    "Surfboard set as rental unavailable successfully"
                )
                Result.Success(true)
            } catch (e: Exception) {
                platformLogger(
                    "QuiverRemoteDataSourceService",
                    "Error setting surfboard as rental unavailable: ${e.message}"
                )
                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
            }
        }


        override suspend fun fetchRentalPage(
            userId: String,
            pageSize: Int,
            lastDocumentId: String?
        ): Result<List<Surfboard>, Error> = try {
            var query = firebase.collection("surfboards")
                .where { "isRentalPublished" equalTo true }
                .where { "isRentalAvailable" equalTo true }
                .orderBy("__name__")

            //if we have a lastDocumentId, we start after it
            if (lastDocumentId != null) {
                query = query.startAfter(lastDocumentId)
            }

            query = query.limit(pageSize.toLong())

            val snapshot = query.get()

            val boards = snapshot.documents.mapNotNull { doc ->
                runCatching {
                    doc.data<SurfboardDto>().toDomain(doc.id)
                }.onFailure {
                    platformLogger("QuiverRemoteDataSourceService", "Failed to parse ${doc.id}: ${it.message}")
                }.getOrNull()
            }
            platformLogger(
                "QuiverRemoteDataSourceService",
                "Fetched ${boards.size} rental boards (startAfter=$lastDocumentId)"
            )

            if (boards.isEmpty()) {
                platformLogger("QuiverRemoteDataSourceService", "No rental boards found")
                Result.Success(emptyList())
            } else {
                Result.Success(boards)
            }
        } catch (e: Exception) {
            platformLogger("QuiverRemoteDataSourceService", "Error fetching rental boards: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "Unknown error"))
        }

        override fun observeAllRentals(): Flow<List<Surfboard>> =
            firebase.collection("surfboards")
                .where("isRentalPublished", true)
                .where("isRentalAvailable", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.data<SurfboardDto>().toDomain(doc.id)
                        } catch (e: Exception) {
                            platformLogger("QuiverRemote", "Failed to parse surfboard ${doc.id}")
                            null
                        }
                    }
                }



    }