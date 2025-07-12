package org.example.quiversync.data.local.dao

import org.example.quiversync.FavSpotEntity
import org.example.quiversync.FavSpotQueries
import org.example.quiversync.QuiverSyncDatabase
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.utils.extensions.platformLogger

data class FavSpotDao(private val queries : FavSpotQueries) {

    fun insertFavSpot(favoriteSpot: FavoriteSpot) {
        platformLogger( "FavSpotDao", "Inserting favorite spot: ${favoriteSpot.name} at (${favoriteSpot.spotLatitude}, ${favoriteSpot.spotLongitude})")
        queries.inserOrReplaceSpot(
            spotID = favoriteSpot.spotID,
            name = favoriteSpot.name,
            latitude = favoriteSpot.spotLatitude,
            longitude = favoriteSpot.spotLongitude,
            userID = favoriteSpot.userID
        )
    }

    fun deleteFavSpot(spotID: String) {
        queries.deleteFavSpot(spotID)
    }

    fun selectAllFavSpots(userID : String): List<FavoriteSpot> {
        println("FavSpotDao: Fetching all favorite spots from local database")
        return try {
            queries.selectAllFavSpots(userID).executeAsList().map { entity ->
                FavoriteSpot(
                    spotID = entity.spotID,
                    userID = entity.userID,
                    name = entity.name,
                    spotLatitude = entity.latitude,
                    spotLongitude = entity.longitude
                )
            }
        }catch (e: Exception) {
            println("im returning empty list")
            return emptyList<FavoriteSpot>()
        }
    }

    fun selectFavSpotByLocation(latitude: Double, longitude: Double, userID : String): FavSpotEntity? {
        return queries.getFavSpotByLocation(latitude, longitude , userID).executeAsOneOrNull()?.let { entity ->
            FavSpotEntity(
                spotID = entity.spotID,
                name = entity.name,
                latitude = entity.latitude,
                longitude = entity.longitude,
                userID = entity.userID
            )
        }
    }

    fun deleteAllFavSpots(userID : String) {
        queries.deleteAllFavSpots(userID)
    }


}
