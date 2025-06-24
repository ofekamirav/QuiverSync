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

    private fun fetchUser(){
        scope.launch {
            val user = createMockUser()

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
        locationName = "San Diego, CA",
        latitude = 32.7157, // Coordinates for San Diego
        longitude = -117.1611,
        dateOfBirth = "2000-01-01", // Use ISO format if consistent
        heightCm = 169,
        weightKg = 62,
        surfLevel = "Intermediate",
        imageUrl = "hs_shortboard", // Or leave empty if no image
        updatedAt = "2025-06-20T12:00:00Z" // Example timestamp
    )
}
