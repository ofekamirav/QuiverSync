package org.example.quiversync.data.remote.datasource.quiver

import org.example.quiversync.data.remote.dto.SurfboardDto

interface QuiverRemoteDataSource {
    suspend fun getSurfboardsRemote(): List<SurfboardDto>
    suspend fun addSurfboardRemote(surfboard: SurfboardDto): Boolean
    suspend fun deleteSurfboardRemote(surfboardId: String): Boolean
    suspend fun publishForRentalRemote(surfboardId: String): Boolean
    suspend fun unpublishForRentalRemote(surfboardId: String): Boolean
}