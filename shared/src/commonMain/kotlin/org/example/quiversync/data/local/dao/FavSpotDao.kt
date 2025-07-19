package org.example.quiversync.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.quiversync.FavSpotEntity
import org.example.quiversync.FavSpotQueries
import org.example.quiversync.QuiverSyncDatabase
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.utils.extensions.platformLogger

data class FavSpotDao(
    private val queries: FavSpotQueries,
) {
    fun insertFavSpot(favoriteSpot: FavoriteSpot) {
        platformLogger(
            "FavSpotDao",
            "Inserting favorite spot: ${favoriteSpot.name} at (${favoriteSpot.spotLatitude}, ${favoriteSpot.spotLongitude})"
        )
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

    fun selectAllFavSpots(userID: String): Flow<List<FavoriteSpot>> =
        queries.selectAllFavSpots(userID)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.map { ent ->
                    FavoriteSpot(
                        spotID = ent.spotID,
                        userID = ent.userID,
                        name = ent.name,
                        spotLatitude = ent.latitude,
                        spotLongitude = ent.longitude
                    )
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

    fun transaction(block: () -> Unit) {
        queries.transaction {
            block()
        }
    }


}
