package org.example.quiversync.data.remote.datasource.favSpot

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.FavSpotDto
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot

data class FavSpotRemoteSourceService(
    private val firebase: FirebaseFirestore,
) : FavSpotRemoteSource {
    override suspend fun getFavSpotsRemote(
        userId: String
    ): Result<List<FavSpotDto>, TMDBError> {
        try {
            val spots = firebase.collection("favSpot")
                .where("userID", "==", userId)
                .get()
                .documents
            val favSpots = spots.map { it.data<FavSpotDto>() }
            return Result.Success(favSpots)

        } catch (e: Exception) {
            return Result.Failure(TMDBError("Error fetching favorite spots: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun addFavSpotRemote(
        favSpot: FavoriteSpot,
        userId: String
    ): Result<Unit, TMDBError> {
        try {
            val existingSpotInDB = firebase.collection("favSpot")
                .where("spotLatitude", "==", favSpot.spotLatitude)
                .where("spotLongitude", "==", favSpot.spotLongitude)
                .get()
                .documents
            if (existingSpotInDB.isEmpty()) {
                firebase.collection("favSpot").add(
                    mapOf(
                        "name" to favSpot.name,
                        "spotLatitude" to favSpot.spotLatitude,
                        "spotLongitude" to favSpot.spotLongitude,
                        "userID" to userId
                    )
                )
            } else {
                return Result.Failure(TMDBError("Spot already exists in the user favorites"))
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(
                TMDBError("Error adding favorite spot: ${e.message ?: "Unknown error"}")
            )
        }
    }


    override suspend fun deleteFavSpotRemote(
        favSpot: FavoriteSpot,
        userID: String
    ): Result<Unit, TMDBError> {
        try {
            val existingSpotInDB = firebase.collection("favSpot")
                .where("userID", "==", userID)
                .where("spotLatitude", "==", favSpot.spotLatitude)
                .where("spotLongitude", "==", favSpot.spotLongitude)
                .get()
                .documents
            if (existingSpotInDB.isEmpty()) {
                return Result.Failure(TMDBError("Spot not found in favorites"))
            }
            firebase.collection("favSpot")
                .document(existingSpotInDB.first().id)
                .delete()
            return Result.Success(Unit)

        } catch (e: Exception) {
            return Result.Failure(
                TMDBError("Error deleting favorite spot: ${e.message ?: "Unknown error"}")
            )
        }
    }

}