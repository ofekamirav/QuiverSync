package org.example.quiversync.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot

interface FavSpotRepository {
    suspend fun addFavSpot(favSpot: FavoriteSpot): Result<Unit, Error>
    suspend fun removeFavSpot(favSpot: FavoriteSpot): Result<Unit, Error>
    suspend fun clearAllSpots(): Result<Unit, Error>
    fun getAllFavSpots(): Flow<Result<List<FavoriteSpot>, Error>>
    suspend fun stopRealtimeSync()
}
