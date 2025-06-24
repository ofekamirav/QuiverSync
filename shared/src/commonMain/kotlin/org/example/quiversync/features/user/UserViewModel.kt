package org.example.quiversync.features.user

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.user.UserState
import org.example.quiversync.domain.model.User


class UserViewModel: BaseViewModel() {

    private val _uiState = MutableStateFlow<UserState>(UserState.Loading)
    val uiState: StateFlow<UserState> get() = _uiState

    init {
        fetchUser()
    }

    fun fetchUser() {
        scope.launch {
            val user = createMockUser()
            delay(1500)


            _uiState.emit(
                UserState.Loaded(user)
            )
        }
    }

    fun createMockUser(): User {
        return User(
            id = "1",
            name = "Ofek",
            locationName = "San Diego, CA",
            latitude = 32.7157,
            longitude = -117.1611,
            password = "password",
            imageUrl = "",
            boards = 8,
            rentals = 5,
            spots = 12,
            heightCm = 169,
            weightKg = 62,
            surfLevel = "Intermediate",
            email = "MikeRod@gmail.com",
            dateOfBirth = "01/01/2000",
            updatedAt = "01/01/2000",
        )
    }
}
