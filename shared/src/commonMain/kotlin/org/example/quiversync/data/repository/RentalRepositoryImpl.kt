package org.example.quiversync.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.dao.RentalOfferDao
import org.example.quiversync.data.remote.datasource.rentals.RentalsRemoteDataSource
import org.example.quiversync.data.remote.dto.RentalOfferDto
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.RentalOffer
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardError
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.repository.RentalRepository
import org.example.quiversync.utils.extensions.isOutsideRadius
import org.example.quiversync.utils.extensions.platformLogger

class RentalRepositoryImpl(
    private val remoteDataSource: RentalsRemoteDataSource,
    private val rentalOffersDao: RentalOfferDao,
    private val sessionManager: SessionManager,
    ): RentalRepository {


    override suspend fun getRentalsBoards(): Result<List<RentalOffer>, Error> {
        return try {
            platformLogger("RentalRepositoryImpl", "Starting getRentalsBoards()")

            val userId = sessionManager.getUid()
                ?: return Result.Failure(SurfboardError("User not logged in")).also {
                    platformLogger("RentalRepositoryImpl", "User ID not found in session")
                }

            val lastLocation = sessionManager.getLastLocation()
            platformLogger("RentalRepositoryImpl", "Last known location: $lastLocation")

            val rentalOffersResult = remoteDataSource.getRentalsBoardsRemote(userId)
            platformLogger("RentalRepositoryImpl", "Remote fetch result: $rentalOffersResult")

            var resultOffers: List<RentalOffer> = emptyList()

            when (rentalOffersResult) {
                is Result.Failure -> {
                    platformLogger("RentalRepositoryImpl", "Failed to fetch rental offers: ${rentalOffersResult.error?.message}")
                    return Result.Failure(SurfboardError(rentalOffersResult.error?.message ?: "Failed to fetch rental offers"))
                }

                is Result.Success -> {
                    val rentalOffers = rentalOffersResult.data ?: emptyList()
                    platformLogger("RentalRepositoryImpl", "Fetched ${rentalOffers.size} remote offers")

                    if (rentalOffers.isEmpty()) {
                        platformLogger("RentalRepositoryImpl", "No rental offers returned")
                        return Result.Success(emptyList())
                    }

//                    resultOffers = if (rentalOffers.size > 30 && lastLocation != null) {
//                        val filtered = rentalOffers.filter {
//                            !isOutsideRadius(
//                                it.longitude,
//                                it.latitude,
//                                lastLocation.longitude,
//                                lastLocation.latitude,
//                                50.0
//                            )
//                        }.take(30)
//
//                        platformLogger("RentalRepositoryImpl", "Filtered to ${filtered.size} offers within 50km radius")
//                        filtered
//                    } else {
//                        rentalOffers
//                    }

                    rentalOffersDao.insertAll(resultOffers)
                    platformLogger("RentalRepositoryImpl", "Inserted ${resultOffers.size} offers into local DB")
                    Result.Success(resultOffers)
                }
            }
        } catch (e: FirebaseFirestoreException) {
            platformLogger("RentalRepositoryImpl", "Firestore exception: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "Firestore error"))
        } catch (e: Exception) {
            platformLogger("RentalRepositoryImpl", "Unexpected error: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "Unknown error fetching rental offers"))
        }
    }

    override suspend fun addRentalOffer(
        surfboard: Surfboard,
        rentalDetails: RentalPublishDetails,
        user : User
    ): Result<RentalOffer, Error> {
        return try {
            val userId = sessionManager.getUid()
                ?: return Result.Failure(SurfboardError("User not logged in"))

            val location = sessionManager.getLastLocation()
                ?: return Result.Failure(SurfboardError("User location not available"))

            // will implement location validation later
//            val latitude = rentalDetails.latitude ?: return Result.Failure(SurfboardError("Latitude is required"))
//            val longitude = rentalDetails.longitude ?: return Result.Failure(SurfboardError("Longitude is required"))
            val price = rentalDetails.pricePerDay ?: return Result.Failure(SurfboardError("Price per day is required"))

            val rentalOffer = RentalOfferDto(
                ownerId = userId,
                ownerName = user.name,
                ownerProfilePicture = user.profilePicture,
                surfboardId = surfboard.id,
                surfboardName = surfboard.model,
                surfboardImageUrl = surfboard.imageRes,
                pricePerDay = price,
                latitude = location.latitude,
                longitude = location.longitude,
                description = "${surfboard.company} ${surfboard.model}",
                isAvailable = rentalDetails.isRentalAvailable ?: true
            )

            // Save to Firestore
            val result = remoteDataSource.addRentalOffer(rentalOffer)

            // Cache locally if successful
//            if (result is Result.Success) {
//                result.data?.let { rentalOffersDao.insert(it) }
//            }

            return result

        } catch (e: Exception) {
            platformLogger("RentalRepositoryImpl", "Error adding rental offer: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun buildRentalOffer(
        surfboard: Surfboard,
        user: User
    ): Result<RentalOffer, Error> {
        return try{
            platformLogger("RentalRepositoryImpl", "Building rental offer for surfboard: ${surfboard.id}")
            val rentalInDao = rentalOffersDao.getById(surfboard.id)
            if (rentalInDao != null) {
                platformLogger("RentalRepositoryImpl", "Rental offer already exists in local DB for surfboard: ${surfboard.id}")
                return Result.Success(rentalInDao)
            }

            val rentalOffer = surfboard.latitude?.let {
                surfboard.longitude?.let { it1 ->
                    RentalOffer(
                        id = surfboard.id,
                        ownerId = user.uid,
                        ownerName = user.name,
                        ownerProfilePicture = user.profilePicture,
                        surfboardId = surfboard.id,
                        surfboardName = surfboard.model,
                        surfboardImageUrl = surfboard.imageRes,
                        pricePerDay = surfboard.pricePerDay ?: 0.0,
                        latitude = it,
                        longitude = it1,
                        description = "${surfboard.company} ${surfboard.model}",
                        isAvailable = true
                    )
                }
            }

            if (rentalOffer != null) {
                rentalOffersDao.insert(rentalOffer)
            }

            Result.Success(rentalOffer)
        }
        catch (e: Exception) {
            platformLogger("RentalRepositoryImpl", "Error building rental offer: ${e.message}")
            Result.Failure(SurfboardError(e.message ?: "Unknown error occurred"))
        }
    }


}