package org.example.quiversync.data.local.dao


import org.example.quiversync.OwnersQueries
import org.example.quiversync.domain.model.OwnerLocal

class OwnersDao(private val queries: OwnersQueries) {

    fun getOwnerById(id: String): OwnerLocal?  {
        return queries.getOwnerById(id)
            .executeAsOneOrNull()?.let {
                OwnerLocal(
                    id = it.id,
                    fullName = it.full_name,
                    email = it.email,
                    profilePicture = it.profile_picture,
                    phoneNumber = it.phone_number,
                    updatedAt = it.updated_at
                )
            }
    }

    fun getAllOwners(): List<OwnerLocal> {
        return queries.getAllOwners()
            .executeAsList()
            .map {
                OwnerLocal(
                    id = it.id,
                    fullName = it.full_name,
                    email = it.email,
                    profilePicture = it.profile_picture,
                    phoneNumber = it.phone_number,
                    updatedAt = it.updated_at
                )
            }
    }

    fun insertOwner(user: OwnerLocal) {
        queries.insertOwner(
            id = user.id,
            full_name = user.fullName,
            email = user.email,
            profile_picture = user.profilePicture,
            phone_number = user.phoneNumber,
            updated_at = user.updatedAt
        )
    }

    fun insertOwners(users: List<OwnerLocal>) {
        queries.transaction {
            users.forEach { insertOwner(it) }
        }
    }

    fun deleteAll() {
        queries.deleteAllOwners()
    }
}
