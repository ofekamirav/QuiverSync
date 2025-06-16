package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.Surfboard

interface QuiverRepository {
    suspend fun getMyQuiver(): Result<List<Surfboard>>
    suspend fun addSurfboard(surfboard: Surfboard): Result<Boolean>
    suspend fun deleteSurfboard(surfboardId: String): Result<Boolean>
    suspend fun publishForRental(surfboardId: String): Result<Boolean>
    suspend fun unpublishForRental(surfboardId: String): Result<Boolean>
}