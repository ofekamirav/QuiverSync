package org.example.quiversync.domain.usecase.favSpots

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.repository.FavSpotRepository

class GetAllFavUserSpots(
    private val favSpotRepository: FavSpotRepository
) {
    suspend operator fun invoke() : Result<List<FavoriteSpot> , TMDBError>{
        return favSpotRepository.getAllFavSpots()
    }
}