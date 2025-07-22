package org.example.quiversync.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error

interface RentalsRepository {
    fun observeExploreBoards(): Flow<List<Surfboard>>
    suspend fun fetchExplorePage(pageSize: Int, lastDocumentId: String?): Result<List<Surfboard>,Error>
}