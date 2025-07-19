package org.example.quiversync.domain.usecase.favSpots

import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.repository.FavSpotRepository

class ClearAllFavSpots(
    private val favSpotRepository: FavSpotRepository
) {
    suspend operator fun invoke() : Result<Unit,Error>{
        return favSpotRepository.clearAllSpots()
    }
}
