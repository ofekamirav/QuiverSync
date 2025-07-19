package org.example.quiversync.data.remote.datasource.favSpot

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.FavSpotDto
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot

interface FavSpotRemoteSource {
    suspend fun getFavSpotsRemote(userId: String): Result<List<FavoriteSpot>, TMDBError>
    suspend fun addFavSpotRemote(favSpot: FavoriteSpot, userId: String): Result<FavoriteSpot, TMDBError>
    suspend fun deleteFavSpotRemote(spotID : String): Result<Unit, TMDBError>
    fun observeFavSpots(userId: String): Flow<List<FavoriteSpot>>
}