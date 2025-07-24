package org.example.quiversync.features.rentals.explore

import org.example.quiversync.domain.model.BoardForRent

data class BoardForDisplay(
    val board: BoardForRent?,
    val id: String
)
