package org.example.quiversync.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.RentalOffer

interface QuiverRepository {
    suspend fun getMyQuiver(): Flow<Result<List<Surfboard>, Error>>
    suspend fun addSurfboard(surfboard: Surfboard): Result<Boolean, Error>
    suspend fun deleteSurfboard(surfboardId: String): Result<Boolean, Error>
    suspend fun publishForRental(surfboardId: String, rentalsDetails: RentalPublishDetails): Result<Boolean, Error>
    suspend fun unpublishForRental(surfboardId: String): Result<Boolean, Error>
    suspend fun setSurfboardAsRentalAvailable(surfboardId: String): Result<Boolean, Error>
    suspend fun setSurfboardAsRentalUnavailable(surfboardId: String): Result<Boolean, Error>
    suspend fun getBoardById(surfboardId: String): Result<Surfboard, Error>
    suspend fun getAvailableSurfboards(userId: String): Result<List<Surfboard>, Error>
    suspend fun stopQuiverSync()

//    suspend fun getRentalsBoards(): Result<List<RentalOffer> , Error>
}