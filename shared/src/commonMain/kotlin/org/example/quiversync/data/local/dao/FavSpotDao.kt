package org.example.quiversync.data.local.dao

import org.example.quiversync.FavSpotEntity
import org.example.quiversync.QuiverSyncDatabase

data class FavSpotDao(private val db: QuiverSyncDatabase) {
    private val queries = db.favSpotQueries

    fun insertFavSpot(name: String, latitude: Double, longitude: Double) {
        queries.insertFavSpot(
            name = name,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun deleteFavSpot(latitude: Double, longitude: Double) {
        queries.deleteFavSpot(latitude, longitude)
    }

    fun selectAllFavSpots(): List<FavSpotEntity> {
        return queries.selectAllFavSpots().executeAsList().map { entity ->
            FavSpotEntity(
                name = entity.name,
                latitude = entity.latitude,
                longitude = entity.longitude
            )
        }
    }

    fun selectFavSpotByLocation(latitude: Double, longitude: Double): FavSpotEntity? {
        return queries.getFavSpotByLocation(latitude, longitude).executeAsOneOrNull()?.let { entity ->
            FavSpotEntity(
                name = entity.name,
                latitude = entity.latitude,
                longitude = entity.longitude
            )
        }
    }

    fun deleteAllFavSpots() {
        queries.deleteAllFavSpots()
    }



}
