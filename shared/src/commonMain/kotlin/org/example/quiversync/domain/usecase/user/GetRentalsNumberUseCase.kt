package org.example.quiversync.domain.usecase.user

import org.example.quiversync.data.local.dao.QuiverDao
import org.example.quiversync.domain.repository.QuiverRepository

class GetRentalsNumberUseCase(
    private val quiverDao: QuiverDao
) {
    operator fun invoke(uid: String): Int = quiverDao.getRentalBoardsNumber(uid)
}