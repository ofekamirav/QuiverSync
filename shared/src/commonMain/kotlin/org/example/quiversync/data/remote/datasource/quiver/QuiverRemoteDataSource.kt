package org.example.quiversync.data.remote.datasource.quiver

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.data.remote.dto.SurfboardDto
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.RentalOffer


interface QuiverRemoteDataSource {
    suspend fun addSurfboardRemote(surfboard: SurfboardDto): Result<Surfboard,Error>
    suspend fun deleteSurfboardRemote(surfboardId: String): Result<Boolean, Error>
    suspend fun publishForRentalRemote(surfboardId: String, rentalsDetails: RentalPublishDetails): Result<Boolean,Error>
    suspend fun unpublishForRentalRemote(surfboardId: String): Result<Boolean, Error>
    suspend fun setSurfboardAsRentalAvailableRemote(surfboardId: String): Result<Boolean, Error>
    suspend fun setSurfboardAsRentalUnavailableRemote(surfboardId: String): Result<Boolean, Error>
    suspend fun getAvailableSurfboardsRemote(userId: String): Result<List<Surfboard>, Error>
    fun observeQuiver(userId: String): Flow<List<Surfboard>>
}