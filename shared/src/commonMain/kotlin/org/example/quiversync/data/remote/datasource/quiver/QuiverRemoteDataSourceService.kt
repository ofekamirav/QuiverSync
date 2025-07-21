    package org.example.quiversync.data.remote.datasource.quiver

    import dev.gitlive.firebase.firestore.FirebaseFirestore
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.map
    import org.example.quiversync.data.remote.dto.RentalPublishDetails
    import org.example.quiversync.data.remote.dto.SurfboardDto
    import org.example.quiversync.domain.model.Surfboard
    import org.example.quiversync.utils.extensions.platformLogger
    import org.example.quiversync.data.local.Result
    import org.example.quiversync.data.local.Error
    import org.example.quiversync.data.remote.dto.UserDto
    import org.example.quiversync.data.session.SessionManager
    import org.example.quiversync.domain.model.RentalOffer
    import org.example.quiversync.domain.model.SurfboardError
    import org.example.quiversync.utils.extensions.toDomain


    class QuiverRemoteDataSourceService(
        private val firebase: FirebaseFirestore,
        private val sessionManager: SessionManager,

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
                // only for now the user with location
                val location = sessionManager.getLastLocation()

                if (location != null) {
                    firebase.collection("surfboards").document(surfboardId).update(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "isRentalPublished" to true,
                        "isRentalAvailable" to true,
                        "pricePerDay" to rentalsDetails.pricePerDay,
                    )
                }
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
                    "isRentalAvailable" to false,
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

        override suspend fun getAvailableSurfboardsRemote(userId: String): Result<List<Surfboard>, Error> {
            return try {
                val querySnapshot = firebase.collection("surfboards")
                    .where { "ownerId" notEqualTo userId }
                    .where { "isRentalPublished" equalTo true }
                    .limit(50)

                val documents = querySnapshot.get().documents

                if (documents.isEmpty()) {
                    platformLogger("QuiverRemoteDataSourceService", "No available surfboards found for user: $userId")
                    return Result.Failure(SurfboardError("No available surfboards found"))
                }

                val surfboards = documents.mapNotNull { doc ->
                    try {
                        doc.data<SurfboardDto>().toDomain(doc.id)
                    } catch (e: Exception) {
                        platformLogger("QuiverRemoteDataSourceService", "Failed to parse surfboard ${doc.id}: ${e.message}")
                        null
                    }
                }

                Result.Success(surfboards)
            }
            catch ( e: Exception) {
                platformLogger("QuiverRemoteDataSourceService", "Error fetching available surfboards: ${e.message}")
                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
            }
        }

//        override suspend fun getRentalsBoardsRemote(userId: String): Result<List<RentalOffer>, Error> {
//            return try {
//                val querySnapshot = firebase.collection("surfboards")
//                    .where { "ownerId" notEqualTo userId }
//                    .where { "isRentalPublished" equalTo true }
//                    .limit(50)
//
//                val documents = querySnapshot.get().documents
//
//                if (documents.isEmpty()) {
//                    platformLogger("QuiverRemoteDataSourceService", "No rental boards found for user: $userId")
//                    return Result.Failure(SurfboardError("No rental boards found"))
//                }
//
//                // Get current user location from sessionManager (you must inject this)
//
//                val offers = documents.mapNotNull { doc ->
//                    try {
//                        val surfboard = doc.data<SurfboardDto>().toDomain(doc.id)
//
//                        val ownerSnapshot = firebase.collection("users")
//                            .document(surfboard.ownerId)
//                            .get()
//
//                        val owner = ownerSnapshot.data<UserDto>().toDomain(ownerSnapshot.id)
//
//                        RentalOffer(
//                            id = surfboard.id,
//                            ownerId = owner.uid,
//                            ownerName = owner.name,
//                            ownerProfilePicture = owner.profilePicture,
//                            surfboardId = surfboard.id,
//                            surfboardName = surfboard.model,
//                            surfboardImageUrl = surfboard.imageRes,
//                            pricePerDay = surfboard.pricePerDay ?: 0.0,
//                            latitude = surfboard.latitude ?: 0.0,
//                            longitude = surfboard.longitude ?: 0.0,
//                            description = "${surfboard.company} ${surfboard.model}",
//                            isAvailable = surfboard.isRentalAvailable ?: true
//                        )
//                    } catch (e: Exception) {
//                        platformLogger("QuiverRemoteDataSourceService", "Failed to map rental offer ${doc.id}: ${e.message}")
//                        null
//                    }
//                }
//
//                Result.Success(offers)
//
//            } catch (e: Exception) {
//                platformLogger("QuiverRemoteDataSourceService", "Error fetching rental offers: ${e.message}")
//                Result.Failure(SurfboardError(e.message ?: "Unknown error"))
//            }
//        }


        //ill implement this later
//        override suspend fun getSurfboardsByLocationRemote(
//            latitude: Double,
//            longitude: Double,
//            radius: Int,
//            idSet: Set<String>,
//            surfboards: List<Surfboard>
//        ): Result<List<Surfboard>, Error> {
//            TODO("Not yet implemented")
//        }
    }