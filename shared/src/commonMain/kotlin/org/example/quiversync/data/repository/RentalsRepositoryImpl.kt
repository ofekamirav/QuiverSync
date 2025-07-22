package org.example.quiversync.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.quiversync.data.local.dao.QuiverDao
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error
import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSource
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.model.RentalError
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.repository.RentalsRepository
import org.example.quiversync.utils.extensions.platformLogger

class RentalsRepositoryImpl(
    private val dao: QuiverDao,
    private val quiverRemote: QuiverRemoteDataSource,
    private val session: SessionManager
) : RentalsRepository {
    private var lastFetchedId: String? = null

    override fun observeExploreBoards(): Flow<List<Surfboard>> =
        dao.getAllRentalSurfboards()
            .map { surfboards ->
                val me = session.getUid()
                surfboards.filter { it.ownerId != me }
            }

    override suspend fun fetchExplorePage(
        pageSize: Int,
        lastDocumentId: String?
    ): Result<List<Surfboard>, Error> {
        val userId = session.getUid() ?: return Result.Failure(RentalError("User not logged in"))
        return when (val res = quiverRemote.fetchRentalPage(userId, pageSize, lastDocumentId)) {
            is Result.Success -> {
                val boards = res.data ?: emptyList()
                dao.transaction {
                    boards.forEach { dao.addSurfboard(it) }
                }
                lastFetchedId = boards.lastOrNull()?.id
                Result.Success(boards)
            }
            is Result.Failure -> {
                platformLogger("RentalsRepo", "Fetch error: ${res.error?.message}")
                Result.Failure(res.error)
            }
        }
    }
}
