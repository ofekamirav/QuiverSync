package org.example.quiversync.domain.usecase.favSpots

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.repository.FavSpotRepository

class GetAllFavUserSpots(
    private val favSpotRepository: FavSpotRepository
) {
    operator fun invoke(): Flow<Result<List<FavoriteSpot>, Error>> {
        return favSpotRepository.getAllFavSpots()
    }
}