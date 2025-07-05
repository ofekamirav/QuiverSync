package org.example.quiversync.domain.repository

import org.example.quiversync.data.local.Error
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.data.local.Result

interface QuiverRepository {
    suspend fun getMyQuiver(): Result<List<Surfboard>, Error>
    suspend fun addSurfboard(surfboard: Surfboard): Result<Boolean, Error>
    suspend fun deleteSurfboard(surfboardId: String): Result<Boolean, Error>
    suspend fun publishForRental(surfboardId: String): Result<Boolean, Error>
    suspend fun unpublishForRental(surfboardId: String): Result<Boolean, Error>
    suspend fun updateSurfboardRentalDetails(surfboardId: String, rentalsDetails: RentalPublishDetails): Result<Boolean, Error>
}