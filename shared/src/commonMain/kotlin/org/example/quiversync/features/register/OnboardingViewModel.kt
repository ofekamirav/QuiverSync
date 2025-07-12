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
import org.example.quiversync.data.local.Result

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

                when (result) {
                    is Result.Success -> {
                        _onboardingState.value = updatedIdleState.copy(
                            isUploadingImage = false,
                            profileImageUrl = result.data,
                        )
                        platformLogger("OnboardingViewModel", "Image uploaded successfully: ${result.data}")
                    }
                    is Result.Failure -> {
                        platformLogger("OnboardingViewModel", "Image upload failed: ${result.error?.message}")
                        _onboardingState.value = updatedIdleState.copy(
                            isUploadingImage = false,
                            imageUploadError = "Upload failed: ${result.error?.message}"
                        )
                        return@launch
                    }
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
        val height = currentState.heightCm.toDouble()
        val weight = currentState.weightKg.toDouble()

        val heightError = when {
            false -> "Height is required"
            height > 250.0 || height < 50.0 -> "Enter a valid height"
            else -> null
        }

        val weightError = when {
            false -> "Weight is required"
            weight > 200.0 || weight < 20.0 -> "Enter a valid weight"
            else -> null
        }

        val dateError = if (currentState.dateOfBirth.isBlank()) "Date of birth is required" else null

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
                heightCm = height,
                weightKg = weight,
                surfLevel = currentState.selectedSurfLevel!!.label,
                profilePicture = currentState.profileImageUrl
            )

            val result = registerUseCases.updateUserProfile(details)
            when (result) {
                is Result.Success -> {
                    platformLogger(
                        "OnboardingViewModel",
                        "Profile updated successfully: ${details.profilePicture}"
                    )
                    _onboardingState.emit(OnboardingState.Success)
                }

                is Result.Failure -> {
                    platformLogger(
                        "OnboardingViewModel",
                        "Error updating profile: ${result.error?.message}"
                    )
                    _onboardingState.emit(OnboardingState.Error(result.error?.message ?: "Unknown error"))
                }
            }
        }
    }


}