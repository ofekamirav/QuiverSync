package org.example.quiversync.domain.usecase.user

import org.example.quiversync.data.local.dao.QuiverDao

class GetBoardsNumberUseCase(
    private val quiverDao: QuiverDao
) {
    operator fun invoke(uid: String): Int = quiverDao.getBoardsNumber(uid)
}