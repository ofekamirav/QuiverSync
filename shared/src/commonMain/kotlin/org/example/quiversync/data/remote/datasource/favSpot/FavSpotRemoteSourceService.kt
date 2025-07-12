package org.example.quiversync.data.remote.datasource.favSpot

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.FavSpotDto
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.utils.extensions.platformLogger

data class FavSpotRemoteSourceService(
    private val firebase: FirebaseFirestore,
) : FavSpotRemoteSource {
    override suspend fun getFavSpotsRemote(
        userId: String
    ): Result<List<FavoriteSpot>, TMDBError> {
        try {
            val spots = firebase.collection("favSpot")
                .where{ "userID" equalTo userId }
                .get()
                .documents
            if (spots.isEmpty()) {
                println("FavSpotRemoteSourceService No favorite spots found for user: $userId")
                return Result.Failure(TMDBError("NO favorite spots found for user: $userId"))
            }

            val favSpots = spots.map {
                val data = it.data<FavSpotDto>()
                run {
                    platformLogger("FavSpotRemoteSourceService", "Fetched favorite spot: ${data.name} with ID: ${it.id}")
                    FavoriteSpot(
                        spotID = it.id,
                        userID = data.userID,
                        name = data.name,
                        spotLatitude = data.spotLatitude,
                        spotLongitude = data.spotLongitude
                    )
                }
            }
            return Result.Success(favSpots)

        } catch (e: Exception) {
            return Result.Failure(TMDBError("Error fetching favorite spots: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun addFavSpotRemote(
        favSpot: FavoriteSpot,
        userId: String
    ): Result<FavoriteSpot, TMDBError> {
        try {
            val existingSpotInDB = firebase.collection("favSpot")
                .where { "spotLatitude" equalTo  favSpot.spotLatitude }
                .where { "spotLongitude" equalTo favSpot.spotLongitude }
                .where { "userID" equalTo userId }
                .get()
                .documents
            if (existingSpotInDB.isEmpty()) {
                val addedSpot = firebase.collection("favSpot").add(
                    mapOf(
                        "name" to favSpot.name,
                        "spotLatitude" to favSpot.spotLatitude,
                        "spotLongitude" to favSpot.spotLongitude,
                        "userID" to userId
                    )
                )
                platformLogger("FavSpotRemoteSourceService", "Added favorite spot with ID: ${addedSpot.id}")
                val addToDao = FavoriteSpot(
                    spotID = addedSpot.id,
                    userID = userId,
                    name = favSpot.name,
                    spotLatitude = favSpot.spotLatitude,
                    spotLongitude = favSpot.spotLongitude
                )
                return Result.Success(addToDao)
            } else {
                return Result.Failure(TMDBError("Spot already exists in the user favorites"))
            }
        } catch (e: Exception) {
            return Result.Failure(
                TMDBError("Error adding favorite spot: ${e.message ?: "Unknown error"}")
            )
        }
    }


    override suspend fun deleteFavSpotRemote(
        spotID : String
    ): Result<Unit, TMDBError> {
        try {
            firebase.collection("favSpot").document(spotID).delete()
            platformLogger( "FavSpotRemoteSourceService", "Deleted favorite spot with ID: $spotID")
            return Result.Success(Unit)

        } catch (e: Exception) {
            return Result.Failure(
                TMDBError("Error deleting favorite spot: ${e.message ?: "Unknown error"}")
            )
        }
    }

}