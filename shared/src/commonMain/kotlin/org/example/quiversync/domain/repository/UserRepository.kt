package org.example.quiversync.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.quiversync.data.local.Error
import org.example.quiversync.domain.model.User
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.OwnerLocal
import org.example.quiversync.utils.Location

interface UserRepository {
    suspend fun getUserProfile(): Flow<Result<User, Error>>
    suspend fun getOwnerById(id: String): Flow<Result<OwnerLocal, Error>>
    suspend fun updateUserProfile(user: User):Result<Unit, Error>
    suspend fun deleteProfileLocal(uid:String)
    suspend fun getUserCurrentLocation(): Result<Location, Error>
    suspend fun updateUserCurrentLocation(location: Location): Result<Unit, Error>
    suspend fun stopUserSync()
    suspend fun startUserSync()
}
