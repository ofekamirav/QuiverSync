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
            println("FavSpotRepository: Fetching spots from local database")
            val userID = sessionManager.getUid() ?: return Result.Failure(TMDBError("User not logged in"))
            val spotsDao = dao.selectAllFavSpots(userID)
            var spots = emptyList<FavoriteSpot>()
            println( "FavSpotRepository: Local database returned ${spotsDao.size} spots")


            if(spotsDao.isNotEmpty()) {
                println("FavSpotRepository: Converting ${spotsDao.size} spots from DAO to domain model")
                val spotsFromDao =  spotsDao.map { entity ->
                    FavoriteSpot(
                        spotID = entity.spotID,
                        userID = entity.userID,
                        name = entity.name,
                        spotLatitude = entity.spotLatitude,
                        spotLongitude = entity.spotLongitude
                    )
                }
                return Result.Success(spotsFromDao)
            }

            // If local database is empty, try to fetch from Firebase
            if (spotsDao.isEmpty()) {
                println("FavSpotRepository: Local database empty, fetching from Firebase")

                println("FavSpotRepository: Calling getFavSpotsRemote for user $userID")
                when (val remoteResult = remoteDataSource.getFavSpotsRemote(userID)) {
                    is Result.Success -> {
                        val remoteFavSpots = remoteResult.data ?: emptyList()
                        println("FavSpotRepository: Retrieved ${remoteFavSpots.forEach { sp ->
                            println("FavSpotRepository: Spot ${sp.name} with ID ${sp.spotID} at (${sp.spotLatitude}, ${sp.spotLongitude})")
                        }} spots from Firebase")

                        // Save remote spots to local database
                        remoteFavSpots.forEach { favSpotDto ->
                            println("FavSpotRepository: Saving spot ${favSpotDto.name} to local database")
                            dao.insertFavSpot(favSpotDto)
                        }

                        // Convert DTOs to domain models
                        spots = remoteFavSpots.map {
                            FavoriteSpot(
                                spotID = it.spotID,
                                userID = it.userID,
                                name = it.name,
                                spotLatitude = it.spotLatitude,
                                spotLongitude = it.spotLongitude
                            )
                        }
                        println("FavSpotRepository: Successfully synced ${spots.size} spots from Firebase to local DB")
                    }
                    is Result.Failure -> {
                        Result.Failure(TMDBError("Error fetching favorite spots from Firebase: ${remoteResult.error?.message ?: "Unknown error"}"))
                    }
                }
            }

//            println("FavSpotRepository: Returning ${spots.size} favorite spots")
            Result.Success(spots)
        } catch (e: Exception) {
//            println("FavSpotRepository: Exception when fetching spots: ${e.message}")
            e.printStackTrace()
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
            when(result){
                is Result.Success -> {

//                    println("FavSpotRepository: Successfully added favorite spot ${result.data?.name} with ID ${result.data?.spotID}")
                    result.data?.let { dao.insertFavSpot(it) }
                }
                is Result.Failure -> {
//                    println("FavSpotRepository: Failed to add favorite spot: ${result.error?.message}")
                    return Result.Failure(result.error)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error adding favorite spot: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun removeFavSpot(favSpot: FavoriteSpot): Result<Unit, TMDBError> {
        return try {
//            val userID = sessionManager.getUid() ?: return Result.Failure(TMDBError("User not logged in"))
            val result = remoteDataSource.deleteFavSpotRemote(favSpot.spotID)
            if (result is Result.Failure) {
                return result
            }
            dao.deleteFavSpot(favSpot.spotID)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error removing favorite spot: ${e.message ?: "Unknown error"}"))
        }
    }


    override suspend fun clearAll(): Result<Unit, TMDBError> {
        return try {
            // Clear from remote data source
            val userID = sessionManager.getUid() ?: return Result.Failure(TMDBError("User not logged in"))
            val allSpots = dao.selectAllFavSpots(userID)
            allSpots.forEach { spot ->
                val result = remoteDataSource.deleteFavSpotRemote(spot.spotID)
                if (result is Result.Failure) {
                    return result
                }
            }
            dao.deleteAllFavSpots(userID)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(TMDBError("Error clearing all favorite spots: ${e.message ?: "Unknown error"}"))
        }
    }
}
