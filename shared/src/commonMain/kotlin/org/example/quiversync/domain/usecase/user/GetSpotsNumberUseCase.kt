package org.example.quiversync.domain.usecase.user

import kotlinx.coroutines.flow.firstOrNull
import org.example.quiversync.domain.repository.FavSpotRepository
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.data.local.Result

class GetSpotsNumberUseCase(
    private val spotsRepository: FavSpotRepository,
) {
    suspend operator fun invoke(userId: String): Int {
        return try {
            val result = spotsRepository.getAllFavSpots().firstOrNull()
            when(result) {
                is Result.Success -> {
                    platformLogger("GetSpotsNumberUseCase", "Successfully fetched spots for user: $userId")
                    result.data?.size ?: 0 // Return the size of the list or 0 if it's null
                }
                is Result.Failure -> {
                    platformLogger("GetSpotsNumberUseCase", "Error fetching spots: ${result.error?.message}")
                    0 // Return 0 if there's an error fetching the spots
                }
                null -> {
                    platformLogger("GetSpotsNumberUseCase", "No spots found for user: $userId")
                    0 // Return 0 if the result is null
                }
            }
        } catch (e: Exception) {
            platformLogger("GetSpotsNumberUseCase", "Error fetching spots: ${e.message}")
            0 // Return 0 if there's an error fetching the spots
        }
    }
}