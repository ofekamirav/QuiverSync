package org.example.quiversync.features.user

import org.example.quiversync.domain.model.User

sealed class UserState {
    object Loading : UserState ()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}