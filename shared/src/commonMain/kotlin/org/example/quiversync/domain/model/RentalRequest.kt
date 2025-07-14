package org.example.quiversync.domain.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class RentalRequest(
    val id: String = "",
    val offerId: String = "",
    val renterId: String = "",
    val renterName: String = "",
    val ownerId: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val totalDays: Int = 0,
    val totalPrice: Double = 0.0,
    val status: RentalStatus = RentalStatus.PENDING,
    val paymentIntentId: String? = null,
    val clientSecret: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
)

@Serializable
enum class RentalStatus{
    PENDING,
    APPROVED,
    CONFIRMED,
    ACTIVE,
    COMPLETED,
    REJECTED,
    CANCELLED
}