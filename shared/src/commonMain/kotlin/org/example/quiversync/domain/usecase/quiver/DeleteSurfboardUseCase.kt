package org.example.quiversync.domain.usecase.quiver
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error

class DeleteSurfboardUseCase(
    private val quiverRepository: QuiverRepository
) {
    suspend operator fun invoke(surfboardId: String): Result<Boolean, Error>  = quiverRepository.deleteSurfboard(surfboardId)
}