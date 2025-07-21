package org.example.quiversync.data.remote.datasource.rentals

import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.RentalOfferDto
import org.example.quiversync.domain.model.RentalOffer
import org.example.quiversync.domain.model.Surfboard

interface RentalsRemoteDataSource {

    suspend fun addRentalOffer(
        rental: RentalOfferDto
    ): Result<RentalOffer, Error>

    suspend fun getRentalsBoardsRemote(userId: String): Result<List<RentalOffer>, Error>

    suspend fun getSurfboardsByLocationRemote(
        latitude: Double,
        longitude: Double,
        radius: Int,
        idSet: Set<String>,
        surfboards: List<Surfboard>
    ): Result<List<Surfboard>, Error>

}