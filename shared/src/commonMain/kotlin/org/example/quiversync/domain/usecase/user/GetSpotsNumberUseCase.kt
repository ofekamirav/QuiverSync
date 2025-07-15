package org.example.quiversync.domain.usecase.user

import org.example.quiversync.data.local.dao.FavSpotDao
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.utils.extensions.platformLogger

class GetSpotsNumberUseCase(
    private val favSpotsDao: FavSpotDao,
) {
    suspend operator fun invoke(userId: String): Int {
        return try {
            favSpotsDao.selectAllFavSpots(userId).size
        } catch (e: Exception) {
            platformLogger("GetSpotsNumberUseCase", "Error fetching spots: ${e.message}")
            0 // Return 0 if there's an error fetching the spots
        }
    }
}