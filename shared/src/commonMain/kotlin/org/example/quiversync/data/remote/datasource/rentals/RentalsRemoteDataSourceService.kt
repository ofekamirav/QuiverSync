package org.example.quiversync.data.remote.datasource.rentals

import dev.gitlive.firebase.firestore.FirebaseFirestore
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.RentalOfferDto
import org.example.quiversync.data.remote.dto.SurfboardDto
import org.example.quiversync.data.remote.dto.UserDto
import org.example.quiversync.domain.model.RentalOffer
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardError
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toDomain

class RentalsRemoteDataSourceService (
    private val firebase: FirebaseFirestore,
    ) : RentalsRemoteDataSource {
    override suspend fun addRentalOffer(rental: RentalOfferDto): Result<RentalOffer, Error> {
        return try{
            val rentalRemote = firebase.collection("rentalOffers")
                .where{ "surfboardId" equalTo rental.surfboardId }
                .where{ "ownerId" equalTo rental.ownerId }
                .get()
            if (rentalRemote.documents.isNotEmpty()) {
                platformLogger("RentalsRemoteDataSourceService", "Rental offer already exists for surfboard: ${rental.surfboardId} by owner: ${rental.ownerId}")
                return Result.Failure(SurfboardError("Rental offer already exists"))
            }
            val rentalID = firebase.collection("rentalOffers").add(rental)
            val rentalOffer = RentalOffer(
                id = rentalID.id,
                ownerId = rental.ownerId,
                ownerName = rental.ownerName,
                ownerProfilePicture = rental.ownerProfilePicture,
                surfboardId = rental.surfboardId,
                surfboardName = rental.surfboardName,
                surfboardImageUrl = rental.surfboardImageUrl,
                pricePerDay = rental.pricePerDay,
                latitude = rental.latitude,
                longitude = rental.longitude,
                description = rental.description,
                isAvailable = rental.isAvailable
            )
            return Result.Success(rentalOffer)
        }
        catch (e: Exception) {
            platformLogger("RentalsRemoteDataSourceService", "Error adding rental offer: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "Unknown error"))
        }

    }




    override suspend fun getRentalsBoardsRemote(userId: String): Result<List<RentalOffer>, Error> {
        return try {
            val querySnapshot = firebase.collection("rentalOffers")
                .where { "ownerId" notEqualTo userId }
                .where { "isAvailable" equalTo true }
                .limit(50)

            val documents = querySnapshot.get().documents

            if (documents.isEmpty()) {
                platformLogger(
                    "QuiverRemoteDataSourceService",
                    "No rental boards found for user: $userId"
                )
                return Result.Failure(SurfboardError("No rental boards found"))
            }

            val rentalOffers = documents.mapNotNull { doc ->
                try {
                    doc.data<RentalOffer>().copy(id = doc.id)
                } catch (e: Exception) {
                    platformLogger("QuiverRemoteDataSourceService", "Failed to parse rental offer: ${doc.id}")
                    null
                }
            }

            Result.Success(rentalOffers)

        } catch (e: Exception) {
            platformLogger("QuiverRemoteDataSourceService", "Error fetching rental offers: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getSurfboardsByLocationRemote(
        latitude: Double,
        longitude: Double,
        radius: Int,
        idSet: Set<String>,
        surfboards: List<Surfboard>
    ): Result<List<Surfboard>, Error> {
        TODO("Not yet implemented")
    }



}