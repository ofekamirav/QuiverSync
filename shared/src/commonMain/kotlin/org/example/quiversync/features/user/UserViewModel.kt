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

}

private fun createMockUser(): User {
    return User(
        id = "1",
        name = "Mike Rodriguez",
        email = "MikeRod@gmail.com",
        password = "mockPassword123", // Add a mock password
        locationName = "San Diego, CA",
        latitude = 32.7157,
        longitude = -117.1611,
        boards = 3,     // Example count
        spots = 2,      // Example count
        rentals = 1,    // Example count
        dateOfBirth = "2000-01-01",
        heightCm = 169,
        weightKg = 62,
        surfLevel = "Intermediate",
        imageUrl = "hs_shortboard",
        updatedAt = "2025-06-20T12:00:00Z"
    )
}

