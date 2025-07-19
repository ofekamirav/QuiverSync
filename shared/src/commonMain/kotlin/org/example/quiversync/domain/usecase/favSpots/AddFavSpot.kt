package org.example.quiversync.domain.usecase.favSpots

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.repository.FavSpotRepository

class AddFavSpot(
    private val favSpotRepository: FavSpotRepository
) {
    suspend operator fun invoke(favSpot : FavoriteSpot) : Result<Unit, Error> {
        return favSpotRepository.addFavSpot(favSpot)
    }
}