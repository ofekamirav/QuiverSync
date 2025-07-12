package org.example.quiversync.features.register

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.OnboardingProfileDetails
import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.data.local.Result
import org.example.quiversync.features.user.UserUseCases
import org.example.quiversync.utils.extensions.toSurfLevel

class OnboardingViewModel(
    private val registerUseCases: RegisterUseCases,
    private val uploadImageUseCase: UploadImageUseCase,
    private val userUseCases: UserUseCases
): BaseViewModel() {

    private val _onboardingState = MutableStateFlow<OnboardingState>(OnboardingState.Loading)
    val onboardingState: StateFlow<OnboardingState> get() = _onboardingState

    init {
        loadInitialUserDetails()
    }

    private fun loadInitialUserDetails() {
        scope.launch {
            val userProfile = userUseCases.getUserProfileUseCase()
            when (userProfile) {
                is Result.Success ->{
                    val profile = userProfile.data
                    _onboardingState.emit(
                        OnboardingState.Idle(
                            data = OnboardingFormData(
                                dateOfBirth = profile?.dateOfBirth ?: "",
                                heightCm = profile?.heightCm?.toString() ?: "",
                                weightKg = profile?.weightKg?.toString() ?: "",
                                selectedSurfLevel = profile?.surfLevel?.toSurfLevel(),
                                profileImageUrl = profile?.profilePicture,
                                isUploadingImage = false,
                                dateOfBirthError = null,
                                heightError = null,
                                weightError = null,
                                surfLevelError = null,
                                profileImageError = null,
                                imageUploadError = null
                            )
                        )
                    )
                }
                is Result.Failure -> {
                    platformLogger("OnboardingViewModel", "Failed to load user profile: ${userProfile.error?.message}")
                    _onboardingState.emit(OnboardingState.Error(userProfile.error?.message ?: "Unknown error"))
                }
            }
        }
    }


    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.DateOfBirthChanged -> updateForm { it.copy(dateOfBirth = event.value) }
            is OnboardingEvent.HeightChanged -> updateForm { it.copy(heightCm = event.value) }
            is OnboardingEvent.WeightChanged -> updateForm { it.copy(weightKg = event.value) }
            is OnboardingEvent.SurfLevelChanged -> updateForm { it.copy(selectedSurfLevel = event.level) }
            is OnboardingEvent.ProfileImageSelected -> onProfileImageSelected(event.bytes)
            OnboardingEvent.ContinueClicked -> validateAndComplete()
        }
    }

    private fun updateForm(updateAction: (OnboardingFormData) -> OnboardingFormData) {
        val currentState = _onboardingState.value
        if (currentState is OnboardingState.Idle) {
            _onboardingState.update {
                currentState.copy(data = updateAction(currentState.data))
            }
        }
    }


    fun onProfileImageSelected(imageBytes: ByteArray) {
        val currentIdleState = _onboardingState.value as? OnboardingState.Idle ?: return

        scope.launch {
            updateForm { it.copy(isUploadingImage = true, imageUploadError = null) }

            try {
                val result = uploadImageUseCase(
                    bytes = imageBytes,
                    folder = UploadImageUseCase.Folder.PROFILES
                )

                val updatedIdleState = _onboardingState.value as? OnboardingState.Idle ?: return@launch

                when (result) {
                    is Result.Success -> {
                        updateForm { it.copy(isUploadingImage = false, profileImageUrl = result.data) }
                        platformLogger("OnboardingViewModel", "Image uploaded successfully: ${result.data}")
                    }
                    is Result.Failure -> {
                        platformLogger("OnboardingViewModel", "Image upload failed: ${result.error?.message}")
                        updateForm { it.copy(isUploadingImage = false, imageUploadError = result.error?.message) }
                        return@launch
                    }
                }

            } catch (e: Exception) {
                val fallbackIdle = _onboardingState.value as? OnboardingState.Idle ?: return@launch
                updateForm { it.copy(isUploadingImage = false, imageUploadError = e.message) }
            }
        }
    }

    fun resetStateToIdle() {
        _onboardingState.value = OnboardingState.Idle(
            data = OnboardingFormData(
                dateOfBirth = "",
                heightCm = "",
                weightKg = "",
                selectedSurfLevel = null,
                profileImageUrl = null,
                isUploadingImage = false,
                dateOfBirthError = null,
                heightError = null,
                weightError = null,
                surfLevelError = null,
                profileImageError = null,
                imageUploadError = null
            )
        )
    }

    private fun validateAndComplete() {
        val currentState = _onboardingState.value
        if (currentState !is OnboardingState.Idle) return
        val formData = currentState.data
        val height = formData.heightCm.toDoubleOrNull()
        val weight = formData.weightKg.toDoubleOrNull()

        val heightError = when {
                formData.heightCm.isBlank() -> "Height is required"
            height == null || height > 250.0 || height < 50.0 -> "Enter a valid height"
            else -> null
        }

        val weightError = when {
            formData.weightKg.isBlank() -> "Weight is required"
            weight == null || weight > 200.0 || weight < 20.0 -> "Enter a valid weight"
            else -> null
        }

        val dateError = if (formData.dateOfBirth.isBlank()) "Date of birth is required" else null

        val surfLevelError = if (formData.selectedSurfLevel == null) "Select your surf level" else null
        val imageError = if (formData.profileImageUrl == null) "Please upload a profile picture" else null

        val hasErrors = listOf(dateError, heightError, weightError, surfLevelError, imageError).any { it != null }

        updateForm {
            it.copy(
                dateOfBirthError = dateError,
                heightError = heightError,
                weightError = weightError,
                surfLevelError = surfLevelError,
                profileImageError = imageError
            )
        }

        if (hasErrors) return

        scope.launch {
            _onboardingState.value = OnboardingState.Loading
            val details = formData.selectedSurfLevel?.let {
                OnboardingProfileDetails(
                    dateOfBirth = formData.dateOfBirth,
                    heightCm = height ?: 0.0,
                    weightKg = weight ?: 0.0,
                    surfLevel = it.label,
                    profilePicture = formData.profileImageUrl
                )
            }

            val result = details?.let { registerUseCases.updateUserProfile(it) }
            if (result == null) {
                platformLogger("OnboardingViewModel", "Profile details are null, cannot update.")
                _onboardingState.emit(OnboardingState.Error("Profile details are incomplete."))
                return@launch
            }
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