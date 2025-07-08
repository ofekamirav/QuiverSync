package org.example.quiversync.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.FavSpotDao
import org.example.quiversync.data.remote.datasource.favSpot.FavSpotRemoteSource
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.repository.FavSpotRepository

class FavSpotRepositoryImpl(
    private val dao: FavSpotDao,
    private val remoteDataSource : FavSpotRemoteSource,
    private val sessionManager: SessionManager
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

    override suspend fun addFavSpot(favSpot: FavoriteSpot): Result<Unit, TMDBError> {
        return try {
            val userID = sessionManager.getUid() ?: return Result.Failure(TMDBError("User not logged in"))
            val result = remoteDataSource.addFavSpotRemote(favSpot , userID)
            if (result is Result.Failure) {
                return result
            }
            dao.insertFavSpot(
                name = favSpot.name,
                latitude = favSpot.spotLatitude,
                longitude = favSpot.spotLongitude
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error adding favorite spot: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun removeFavSpot(favSpot: FavoriteSpot): Result<Unit, TMDBError> {
        return try {
            val userID = sessionManager.getUid() ?: return Result.Failure(TMDBError("User not logged in"))
            val result = remoteDataSource.deleteFavSpotRemote(favSpot, userID)
            if (result is Result.Failure) {
                return result
            }
            dao.deleteFavSpot(favSpot.spotLatitude, favSpot.spotLongitude)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error removing favorite spot: ${e.message ?: "Unknown error"}"))
        }
    }


    override suspend fun clearAll(): Result<Unit, TMDBError> {
        return try {
            // Clear from remote data source
            val userID = sessionManager.getUid() ?: return Result.Failure(TMDBError("User not logged in"))
            val allSpots = dao.selectAllFavSpots()
            allSpots.forEach { spot ->
                val result = remoteDataSource.deleteFavSpotRemote(
                    FavoriteSpot(
                        name = spot.name,
                        spotLatitude = spot.latitude,
                        spotLongitude = spot.longitude
                    ), userID
                )
                if (result is Result.Failure) {
                    return result
                }
            }
            dao.deleteAllFavSpots()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error clearing all favorite spots: ${e.message ?: "Unknown error"}"))
        }
    }
}
