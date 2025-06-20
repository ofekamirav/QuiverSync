package org.example.quiversync.domain.model

data class RentalRequest(
    val requestId: String,
    val board: Surfboard,
    val renterId: String,
    val startDate: Long,
    val endDate: Long,
    val status: RentalStatus,
    val createdDate: Long
)

enum class RentalStatus{
    PENDING, APPROVED, COMPLETED, REJECTED, CANCELLED
}