package org.example.quiversync.data.repository

import org.example.quiversync.data.remote.datasource.quiver.QuiverRemoteDataSource
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.repository.QuiverRepository

class QuiverRepositoryImplementation(
    private val remoteDataSource: QuiverRemoteDataSource,
    //private val localDataSource: QuiverLocalDataSource
): QuiverRepository {
    override suspend fun getMyQuiver(): Result<List<Surfboard>> {
        TODO("Not yet implemented")
    }

    override suspend fun addSurfboard(surfboard: Surfboard): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSurfboard(surfboardId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun publishForRental(surfboardId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun unpublishForRental(surfboardId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }
}