package org.example.quiversync.data.remote.datasource.quiver

import io.ktor.client.HttpClient
import org.example.quiversync.data.remote.dto.SurfboardDto

class QuiverRemoteDataSourceImplementation(
    private val client: HttpClient,
    private val baseUrl: String
) : QuiverRemoteDataSource {
    override suspend fun getSurfboardsRemote(): List<SurfboardDto> {
        TODO("Not yet implemented")
    }

    override suspend fun addSurfboardRemote(surfboard: SurfboardDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSurfboardRemote(surfboardId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun publishForRentalRemote(surfboardId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unpublishForRentalRemote(surfboardId: String): Boolean {
        TODO("Not yet implemented")
    }

}