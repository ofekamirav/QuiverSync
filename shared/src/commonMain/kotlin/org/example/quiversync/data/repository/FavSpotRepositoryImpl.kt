package org.example.quiversync.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.FavSpotDao
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.repository.FavSpotRepository

class FavSpotRepositoryImpl(
    private val dao: FavSpotDao,
    private val firestore: FirebaseFirestore,

    ) : FavSpotRepository {

    override suspend fun getAllFavSpots(): Result<List<FavoriteSpot>, TMDBError> {
        return try {
            val spots = dao.selectAllFavSpots().map {
                FavoriteSpot(
                    name = it.name,
                    spotLatitude = it.latitude,
                    spotLongitude = it.longitude
                )
            }
            Result.Success(spots)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error fetching favorite spots: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun addFavSpot(spot: FavoriteSpot): Result<Unit, TMDBError> {
        return try {
            dao.insertFavSpot(
                name = spot.name,
                latitude = spot.spotLatitude,
                longitude = spot.spotLongitude
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error adding favorite spot: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun removeFavSpot(spot: FavoriteSpot): Result<Unit, TMDBError> {
        return try {
            dao.deleteFavSpot(spot.spotLatitude, spot.spotLongitude)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error removing favorite spot: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun getFavSpotByLocation(
        spotLatitude: Double,
        spotLongitude: Double
    ): Result<FavoriteSpot, TMDBError> {
        return try {
            val entity = dao.selectFavSpotByLocation(spotLatitude, spotLongitude)
                ?: return Result.Failure(TMDBError("Spot not found"))
            Result.Success(
                FavoriteSpot(
                    name = entity.name,
                    spotLatitude = entity.latitude,
                    spotLongitude = entity.longitude
                )
            )
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error fetching favorite spot by location: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun clearAll(): Result<Unit, TMDBError> {
        return try {
            dao.deleteAllFavSpots()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error clearing all favorite spots: ${e.message ?: "Unknown error"}"))
        }
    }
}
