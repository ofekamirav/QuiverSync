package org.example.quiversync.domain.repository

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot

interface FavSpotRepository {
    suspend fun getAllFavSpots(): Result<List<FavoriteSpot> , TMDBError>
    suspend fun addFavSpot(spot: FavoriteSpot) : Result<Unit, TMDBError>
    suspend fun removeFavSpot(spot: FavoriteSpot) : Result<Unit, TMDBError>
    suspend fun getFavSpotByLocation(spotLatitude: Double , spotLongitude: Double): Result<FavoriteSpot, TMDBError>
    suspend fun clearAll() : Result<Unit, TMDBError>
}