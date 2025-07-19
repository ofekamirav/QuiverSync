package org.example.quiversync.features.register

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
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
            val userProfile = userUseCases.getUserProfileUseCase().firstOrNull()
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
                                phoneNumber = profile?.phoneNumber ?: "",
                                agreedToTerms = profile?.agreedToTerms ?: false,
                                agreementTimestamp = profile?.agreementTimestamp,
                                isUploadingImage = false,
                                dateOfBirthError = null,
                                heightError = null,
                                weightError = null,
                                surfLevelError = null,
                                profileImageError = null,
                                imageUploadError = null,
                                phoneNumberError = null,
                                termsError = null
                            )
                        )
                    )
                }
                is Result.Failure -> {
                    platformLogger("OnboardingViewModel", "Failed to load user profile: ${userProfile.error?.message}")
                    _onboardingState.emit(OnboardingState.Error(userProfile.error?.message ?: "Unknown error"))
                }
                else ->{
                    platformLogger("OnboardingViewModel", "No user profile found, initializing empty form")
                    _onboardingState.emit(OnboardingState.Error( "Unknown error"))
                }
            }
        }
    }


    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.PhoneNumberChanged -> updateForm { it.copy(phoneNumber = event.value) }
            is OnboardingEvent.DateOfBirthChanged -> updateForm { it.copy(dateOfBirth = event.value) }
            is OnboardingEvent.HeightChanged -> updateForm { it.copy(heightCm = event.value) }
            is OnboardingEvent.WeightChanged -> updateForm { it.copy(weightKg = event.value) }
            is OnboardingEvent.SurfLevelChanged -> updateForm { it.copy(selectedSurfLevel = event.level) }
            is OnboardingEvent.ProfileImageSelected -> onProfileImageSelected(event.bytes)
            is OnboardingEvent.ProfileImageIOSChanged -> updateForm { it.copy(profileImageUrl = event.imageURL , uploadFromIOS = false) }
            is OnboardingEvent.OnAgreementChange -> updateForm { it.copy(
                agreedToTerms = event.isAgreed,
                agreementTimestamp = if (event.isAgreed) Clock.System.now().toString() else null,
                termsError = null
            ) }
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


    private fun onProfileImageSelected(imageBytes: ByteArray) {
        val currentIdleState = _onboardingState.value as? OnboardingState.Idle
        if (currentIdleState == null) {
            platformLogger("OnboardingViewModel", "❌ onProfileImageSelected: current state is not Idle")
            return
        }

        platformLogger("OnboardingViewModel", "📤 Image upload started (Android or iOS)")

        platformLogger( "OnboardingViewModel", "state before upload : ${_onboardingState.value}")

        scope.launch {
            _onboardingState.value = currentIdleState.copy(
                data = currentIdleState.data.copy(
                    isUploadingImage = true,
                    imageUploadError = null
                )
            )

            try {
                val result = uploadImageUseCase(
                    bytes = imageBytes,
                    folder = UploadImageUseCase.Folder.PROFILES
                )

                val updatedIdleState = _onboardingState.value as? OnboardingState.Idle
                if (updatedIdleState == null) {
                    platformLogger("OnboardingViewModel", "❌ State changed unexpectedly during image upload")
                    return@launch
                }

                var finalUrl: String? = null

                when (result) {
                    is Result.Success -> {
                        platformLogger("OnboardingViewModel", "✅ uploadImageUseCase success: ${result.data}")

                        if (result.data == "IOS uploader is been activated") {
                            platformLogger("OnboardingViewModel", "📱 iOS flow activated — waiting for SwiftUI upload")

                            _onboardingState.value = currentIdleState.copy(
                                data = currentIdleState.data.copy(
                                    uploadFromIOS = true
                                )
                            )

                            platformLogger( "OnboardingViewModel", "state that waiting : ${_onboardingState.value}")


                            finalUrl = onboardingState
                                .filterIsInstance<OnboardingState.Idle>()
                                .map { it.data }
                                .first { form -> !form.uploadFromIOS }
                                .profileImageUrl

                            platformLogger("OnboardingViewModel", "📥 iOS uploaded URL received: $finalUrl")

                            if (finalUrl == "failed to upload") {
                                _onboardingState.value = currentIdleState.copy(
                                    data = currentIdleState.data.copy(
                                        imageUploadError = "Upload failed on iOS",
                                        isUploadingImage = false,
                                        uploadFromIOS = false
                                    )
                                )

                                platformLogger("OnboardingViewModel", "❌ iOS upload failed (URL = failed to upload)")
                                return@launch
                            }
                        } else {
                            // Android upload
                            finalUrl = result.data
                        }


                        _onboardingState.value = currentIdleState.copy(
                            data = currentIdleState.data.copy(
                                profileImageUrl = finalUrl,
                                isUploadingImage = false,
                                uploadFromIOS = false,
                                imageUploadError = null
                            )
                        )


                        platformLogger("OnboardingViewModel", "✅ Final profileImageUrl set: $finalUrl")
                    }
                    is Result.Failure -> {
                        updateForm {
                            it.copy(
                                imageUploadError = "Upload failed: ${result.error?.message}",
                                isUploadingImage = false,
                                uploadFromIOS = false
                            )
                        }
                        platformLogger("OnboardingViewModel", "❌ Upload failed: ${result.error?.message}")
                    }
                }

            } catch (e: Exception) {
                val fallbackIdle = _onboardingState.value as? OnboardingState.Idle
                if (fallbackIdle != null) {

                    _onboardingState.value = currentIdleState.copy(
                        data = currentIdleState.data.copy(
                            imageUploadError = "Unexpected error: ${e.message}",
                            isUploadingImage = false,
                            uploadFromIOS = false
                        )
                    )
                    platformLogger("OnboardingViewModel", "❌ Exception during upload: ${e.message}")
                }
            }
        }
    }

    fun resetStateToIdle() {
        _onboardingState.value = OnboardingState.Idle(
            data = OnboardingFormData(
                phoneNumber = "",
                dateOfBirth = "",
                heightCm = "",
                weightKg = "",
                selectedSurfLevel = null,
                agreedToTerms = false,
                agreementTimestamp = null,
                profileImageUrl = null,
                isUploadingImage = false,
                dateOfBirthError = null,
                heightError = null,
                weightError = null,
                surfLevelError = null,
                profileImageError = null,
                imageUploadError = null,
                phoneNumberError = null,
                termsError = null
            )
        )
    }

    private fun validateAndComplete() {
        val currentState = _onboardingState.value
        if (currentState !is OnboardingState.Idle) return
        val formData = currentState.data
        val height = formData.heightCm.toDoubleOrNull()
        val weight = formData.weightKg.toDoubleOrNull()

        val phoneError = when {
            formData.phoneNumber.isBlank() -> "Phone number is required"
            formData.phoneNumber.length < 10 -> "Enter a valid phone number"
            else -> null
        }

        val agreementError = when {
            !formData.agreedToTerms -> "You must agree to the terms and conditions"
            formData.agreementTimestamp == null -> "Agreement timestamp is missing"
            else -> null
        }

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

        val hasErrors = listOf(dateError, heightError, weightError, surfLevelError, imageError, phoneError, agreementError).any { it != null }

        updateForm {
            it.copy(
                phoneNumberError = phoneError,
                dateOfBirthError = dateError,
                heightError = heightError,
                weightError = weightError,
                surfLevelError = surfLevelError,
                profileImageError = imageError,
                termsError = agreementError
            )
        }

        if (hasErrors) return

        scope.launch {
            _onboardingState.value = OnboardingState.Loading
            val details = formData.selectedSurfLevel?.let {
                OnboardingProfileDetails(
                    phoneNumber = formData.phoneNumber,
                    dateOfBirth = formData.dateOfBirth,
                    heightCm = height ?: 0.0,
                    weightKg = weight ?: 0.0,
                    surfLevel = it.label,
                    profilePicture = formData.profileImageUrl,
                    agreedToTerms = formData.agreedToTerms,
                    agreementTimestamp = formData.agreementTimestamp ?: Clock.System.now().toString()
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