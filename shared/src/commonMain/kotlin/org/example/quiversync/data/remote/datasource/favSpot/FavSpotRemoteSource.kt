package org.example.quiversync.data.remote.datasource.favSpot

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.FavSpotDto
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot

interface FavSpotRemoteSource {
    suspend fun getFavSpotsRemote(userId: String): Result<List<FavSpotDto>, TMDBError>
    suspend fun addFavSpotRemote(favSpot: FavoriteSpot, userId: String): Result<Unit, TMDBError>
    suspend fun deleteFavSpotRemote(favSpot: FavoriteSpot , userID: String): Result<Unit, TMDBError>
//    suspend fun updateFavSpotRemote(favSpotId: String, favSpot: FavSpotDto): Boolean
}