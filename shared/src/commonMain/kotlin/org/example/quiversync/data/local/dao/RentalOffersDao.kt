package org.example.quiversync.data.local.dao

import org.example.quiversync.RentalOfferEntity
import org.example.quiversync.RentalOffersQueries
import org.example.quiversync.domain.model.RentalOffer

class RentalOfferDao(
    private val queries: RentalOffersQueries
) {

    fun insert(offer: RentalOffer) {
        queries.inserRentalOffer(
            id = offer.id,
            ownerId = offer.ownerId,
            ownerName = offer.ownerName,
            ownerProfilePicture = offer.ownerProfilePicture,
            surfboardId = offer.surfboardId,
            surfboardName = offer.surfboardName,
            surfboardImageUrl = offer.surfboardImageUrl,
            pricePerDay = offer.pricePerDay,
            latitude = offer.latitude,
            longitude = offer.longitude,
            description = offer.description,
            isAvailable = if (offer.isAvailable) 1 else 0
        )
    }

    fun insertAll(offers: List<RentalOffer>) {
        queries.transaction {
            offers.forEach {
                if(getById(it.id) == null) {
                    insert(it)
                }
            }
        }
    }

    fun getAll(): List<RentalOffer> {
        return queries.selectAllRentalOffers()
            .executeAsList()
            .map { it.toDomain() }
    }

    fun getById(id: String): RentalOffer? {
        return queries.selectRentalOfferById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    fun deleteById(id: String) {
        queries.deleteRentalOfferById(id)
    }

    fun deleteAll() {
        queries.deleteAllRentalOffers()
    }

    private fun RentalOfferEntity.toDomain(): RentalOffer {
        return RentalOffer(
            id = id,
            ownerId = ownerId,
            ownerName = ownerName,
            ownerProfilePicture = ownerProfilePicture,
            surfboardId = surfboardId,
            surfboardName = surfboardName,
            surfboardImageUrl = surfboardImageUrl,
            pricePerDay = pricePerDay,
            latitude = latitude,
            longitude = longitude,
            description = description,
            isAvailable = isAvailable != 0L  // Convert Long/Int to Boolean
        )
    }
}
