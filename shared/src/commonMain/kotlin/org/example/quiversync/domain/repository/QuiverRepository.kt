package org.example.quiversync.domain.repository

import org.example.quiversync.domain.model.Surfboard

interface QuiverRepository {
    suspend fun getMyQuiver(): Result<List<Surfboard>>

}