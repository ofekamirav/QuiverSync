package org.example.quiversync.domain.usecase.quiver
import org.example.quiversync.domain.repository.QuiverRepository
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.SurfboardError
import org.example.quiversync.domain.repository.GeminiRepository

class DeleteSurfboardUseCase(
    private val quiverRepository: QuiverRepository,
    private val geminiRepository: GeminiRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(surfboardId: String): Result<Boolean, Error>{
        val userId = sessionManager.getUid() ?: return Result.Failure(SurfboardError("User not logged in"))
        val result = quiverRepository.deleteSurfboard(surfboardId)
        if (result is Result.Success) {
            // If surfboard deletion is successful, also delete related Gemini predictions
            geminiRepository.deletePredictionsBySurfboardId(surfboardId, userId)
        }
        return result
    }
}