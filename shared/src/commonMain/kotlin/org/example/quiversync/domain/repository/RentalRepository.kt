package org.example.quiversync.domain.repository

import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.domain.model.RentalOffer
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User

interface RentalRepository {


    suspend fun getRentalsBoards(): Result<List<RentalOffer>, Error>

    suspend fun addRentalOffer(surfboard: Surfboard ,rentalDetails : RentalPublishDetails ,user : User): Result<RentalOffer, Error>

    suspend fun buildRentalOffer(surfboard: Surfboard, user: User): Result<RentalOffer, Error>



}