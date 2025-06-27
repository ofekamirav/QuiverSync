package org.example.quiversync.features.register

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.OnboardingProfileDetails
import org.example.quiversync.domain.model.SurfLevel
import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.utils.extensions.platformLogger

class OnboardingViewModel(
    private val registerUseCases: RegisterUseCases,
    private val uploadImageUseCase: UploadImageUseCase
): BaseViewModel() {

    private val _onboardingState = MutableStateFlow<OnboardingState>(OnboardingState.Idle())
    val onboardingState: StateFlow<OnboardingState> get() = _onboardingState

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.DateOfBirthChanged -> updateState { it.copy(dateOfBirth = event.value) }
            is OnboardingEvent.HeightChanged -> updateState { it.copy(heightCm = event.value) }
            is OnboardingEvent.WeightChanged -> updateState { it.copy(weightKg = event.value) }
            is OnboardingEvent.SurfLevelChanged -> updateState { it.copy(selectedSurfLevel = event.level) }
            is OnboardingEvent.ProfileImageSelected -> onProfileImageSelected(event.bytes)
            OnboardingEvent.ContinueClicked -> validateAndComplete()
        }
    }

    private fun updateState(update: (OnboardingState.Idle) -> OnboardingState.Idle) {
        val currentState = _onboardingState.value
        if (currentState is OnboardingState.Idle) {
            _onboardingState.update { update(currentState) }
        }
    }


    fun onProfileImageSelected(imageBytes: ByteArray) {
        val currentIdleState = _onboardingState.value as? OnboardingState.Idle ?: return

        scope.launch {
            _onboardingState.value = currentIdleState.copy(isUploadingImage = true, imageUploadError = null)

            try {
                val result = uploadImageUseCase(
                    bytes = imageBytes,
                    folder = UploadImageUseCase.Folder.PROFILES
                )

                val updatedIdleState = _onboardingState.value as? OnboardingState.Idle ?: return@launch

                result.onSuccess { imageUrl ->
                    _onboardingState.value = updatedIdleState.copy(
                        isUploadingImage = false,
                        profileImageUrl = imageUrl
                    )
                    platformLogger("OnboardingViewModel","Success: $imageUrl")
                }.onFailure { error ->
                    _onboardingState.value = updatedIdleState.copy(
                        isUploadingImage = false,
                        imageUploadError = "Upload failed: ${error.message}"
                    )
                    platformLogger("OnboardingViewModel","Error: ${error.message}")
                }

            } catch (e: Exception) {
                val fallbackIdle = _onboardingState.value as? OnboardingState.Idle ?: return@launch
                _onboardingState.value = fallbackIdle.copy(
                    isUploadingImage = false,
                    imageUploadError = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    fun resetStateToIdle() {
        _onboardingState.value = OnboardingState.Idle()
    }

    private fun validateAndComplete() {
        val currentState = _onboardingState.value
        if (currentState !is OnboardingState.Idle) return

        val dateError = if (currentState.dateOfBirth.isBlank()) "Date of birth is required" else null
        val heightError = {
            if (currentState.heightCm == 0.0) "Height is required" else null
            if (currentState.heightCm > 250.00 || currentState.heightCm < 50.00) "Enter a valid height" else null
        }
        val weightError = {
            if (currentState.weightKg == 0.0) "Weight is required" else null
            if (currentState.weightKg > 200.00 || currentState.weightKg < 20.00) "Enter a valid weight" else null
        }
        val surfLevelError = if (currentState.selectedSurfLevel == null) "Select your surf level" else null
        val imageError = if (currentState.profileImageUrl == null) "Please upload a profile picture" else null

        val hasErrors = listOf(dateError, heightError, weightError, surfLevelError, imageError).any { it != null }

        updateState {
            it.copy(
                dateOfBirthError = dateError,
                heightError = heightError.toString(),
                weightError = weightError.toString(),
                surfLevelError = surfLevelError,
                profileImageError = imageError
            )
        }

        if (hasErrors) return

        scope.launch {
            _onboardingState.value = OnboardingState.Loading
            val details = OnboardingProfileDetails(
                dateOfBirth = currentState.dateOfBirth,
                heightCm = currentState.heightCm,
                weightKg = currentState.weightKg,
                surfLevel = currentState.selectedSurfLevel!!.label,
                profilePicture = currentState.profileImageUrl
            )

            val result = registerUseCases.updateUserProfile(details)
            result.onSuccess {
                _onboardingState.value = OnboardingState.Success
            }.onFailure { exception ->
                _onboardingState.value = OnboardingState.Error(exception.message ?: "An unknown error occurred.")
            }
        }
    }


}