package org.example.quiversync.features.user.edit_user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.user.UserUseCases
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.data.local.Result
import org.example.quiversync.features.quiver.add_board.AddBoardEvent
import org.example.quiversync.utils.event.AppEvent
import org.example.quiversync.utils.event.EventBus


class EditProfileDetailsViewModel(
    private val userUseCases: UserUseCases,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<EditUserState>(EditUserState.Loading)
    val uiState: StateFlow<EditUserState> get() = _uiState

    private val _saveLoading = MutableStateFlow(false)
    val saveLoading: StateFlow<Boolean> get() = _saveLoading

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        scope.launch {
            _uiState.value = EditUserState.Loading

            val result = userUseCases.getUserProfileUseCase()


            when (result) {
                is Result.Success -> {
                        _uiState.value = EditUserState.Editing(
                            form = EditUserFormData(
                                name = result.data?.name,
                                height = result.data?.heightCm ?: 0.0,
                                weight = result.data?.weightKg ?: 0.0,
                                surfLevel = result.data?.surfLevel,
                                profilePicture = result.data?.profilePicture ?: "",
                            )
                        )
                    }
                is Result.Failure -> {
                    _uiState.value =
                        EditUserState.Error(result.error?.message ?: "Unknown error")
                }
            }
        }
    }


    fun onEvent(event: EditUserDetailsEvent) {
        when (event) {
            is EditUserDetailsEvent.onNameChange -> updateName(event.name)
            is EditUserDetailsEvent.onHeightChange -> updateHeight(event.height)
            is EditUserDetailsEvent.onWeightChange -> updateWeight(event.weight)
            is EditUserDetailsEvent.ProfileImageSelected -> onProfileImageSelected(event.bytes)
            is EditUserDetailsEvent.profileImageIOSChanged -> updateImgUrlIOS(event.imageURL)
            EditUserDetailsEvent.onSubmit -> submitChanges()
            is EditUserDetailsEvent.onSurfLevelChange -> updateSurfLevel(event.surfLevel)

        }
    }

    private fun updateName(name: String) {
        val currentState = _uiState.value
        if (currentState is EditUserState.Editing) {
            _uiState.value = currentState.copy(form = currentState.form.copy(name = name))
        }
    }

    private fun updateSurfLevel(surfLevel: String) {
        val currentState = _uiState.value
        if (currentState is EditUserState.Editing) {
            _uiState.value =
                currentState.copy(form = currentState.form.copy(surfLevel = surfLevel))
        }
    }

    private fun updateHeight(height: Double) {
        val currentState = _uiState.value
        if (currentState is EditUserState.Editing) {
            _uiState.value = currentState.copy(form = currentState.form.copy(height = height))
        }
    }

    private fun updateWeight(weight: Double) {
        val currentState = _uiState.value
        if (currentState is EditUserState.Editing) {
            _uiState.value = currentState.copy(form = currentState.form.copy(weight = weight))
        }
    }

    private fun updateImgUrlIOS(url: String) {
        val currentState = _uiState.value
        if (currentState is EditUserState.Editing) {
            _uiState.value = currentState.copy(form = currentState.form.copy(profilePicture = url , uploadFromIOS = false))
        }
    }

    private fun onProfileImageSelected(imageBytes: ByteArray) {
        val currentIdleState = _uiState.value as? EditUserState.Editing
        if (currentIdleState == null) {
            platformLogger("EditProfileDetailsViewModel", "❌ onProfileImageSelected: current state is not Editing")
            return
        }

        platformLogger("EditProfileDetailsViewModel", "📤 Image upload started (Android or iOS)")

        scope.launch {
            _uiState.value = currentIdleState.copy(
                form = currentIdleState.form.copy(
                    isUploadingImage = true,
                    imageUploadError = null
                )
            )

            try {
                val result = userUseCases.uploadImageUseCase(
                    bytes = imageBytes,
                    folder = UploadImageUseCase.Folder.PROFILES
                )

                val updatedIdleState = _uiState.value as? EditUserState.Editing
                if (updatedIdleState == null) {
                    platformLogger("EditProfileDetailsViewModel", "❌ State changed unexpectedly during image upload")
                    return@launch
                }

                var finalUrl: String? = null

                when (result) {
                    is Result.Success -> {
                        platformLogger("EditProfileDetailsViewModel", "✅ uploadImageUseCase success: ${result.data}")

                        if (result.data == "IOS uploader is been activated") {
                            platformLogger("EditProfileDetailsViewModel", "📱 iOS flow activated — waiting for SwiftUI upload")

                            _uiState.value = updatedIdleState.copy(
                                form = updatedIdleState.form.copy(
                                    uploadFromIOS = true
                                )
                            )

                            platformLogger("EditProfileDetailsViewModel", " this is the updated state: ${_uiState.value}")


                            finalUrl = uiState
                                .filterIsInstance<EditUserState.Editing>()
                                .map { it.form }
                                .first { form -> !form.uploadFromIOS}
                                .profilePicture


                            platformLogger("EditProfileDetailsViewModel", "📥 iOS uploaded URL received: $finalUrl")

                            if (finalUrl == "failed to upload") {
                                _uiState.value = updatedIdleState.copy(
                                    form = updatedIdleState.form.copy(
                                        imageUploadError = "Upload failed on iOS",
                                        isUploadingImage = false,
                                        uploadFromIOS = false
                                    )
                                )
                                platformLogger("EditProfileDetailsViewModel", "❌ iOS upload failed (URL = failed to upload)")
                                return@launch
                            }
                        } else {
                            // Android case
                            finalUrl = result.data
                        }

                        _uiState.value = updatedIdleState.copy(
                            form = updatedIdleState.form.copy(
                                profilePicture = finalUrl,
                                isUploadingImage = false,
                                uploadFromIOS = false,
                                imageUploadError = null
                            )
                        )

                        platformLogger("EditProfileDetailsViewModel", "✅ Final profilePicture set: $finalUrl")
                    }

                    is Result.Failure -> {
                        _uiState.value = updatedIdleState.copy(
                            form = updatedIdleState.form.copy(
                                imageUploadError = "Upload failed: ${result.error?.message}",
                                isUploadingImage = false,
                                uploadFromIOS = false
                            )
                        )
                        platformLogger("EditProfileDetailsViewModel", "❌ Upload failed: ${result.error?.message}")
                    }
                }

            } catch (e: Exception) {
                val fallbackIdle = _uiState.value as? EditUserState.Editing
                if (fallbackIdle != null) {
                    _uiState.value = fallbackIdle.copy(
                        form = fallbackIdle.form.copy(
                            imageUploadError = "Unexpected error: ${e.message}",
                            isUploadingImage = false,
                            uploadFromIOS = false
                        )
                    )
                    platformLogger("EditProfileDetailsViewModel", "❌ Exception during upload: ${e.message}")
                }
            }
        }
    }

    private fun submitChanges() {

        val currentState = _uiState.value
        platformLogger(
            "EditProfileDetailsViewModel",
            "hello from submitChanges"

        )
        if (currentState is EditUserState.Editing) {
            scope.launch {
                _saveLoading.value = true
                platformLogger(
                    "EditProfileDetailsViewModel",
                    "${currentState.form}"
                )
                val result = userUseCases.updateProfileDetailsUseCase(currentState.form)
                when (result) {
                    is Result.Success -> {
                        platformLogger(
                            "EditProfileDetailsViewModel",
                            "Profile updated successfully"
                        )
                        EventBus.postEvent(AppEvent.ProfileUpdated)
                        _uiState.emit(EditUserState.Success)
                        _saveLoading.value = false
                    }

                    is Result.Failure -> {
                        platformLogger(
                            "EditProfileDetailsViewModel",
                            "Profile update failed: ${result.error?.message}"
                        )
                        _uiState.emit(
                            EditUserState.Error(
                                result.error?.message ?: "Unknown error"
                            )
                        )
                        _saveLoading.value = false
                    }
                }
            }
        }
    }
}