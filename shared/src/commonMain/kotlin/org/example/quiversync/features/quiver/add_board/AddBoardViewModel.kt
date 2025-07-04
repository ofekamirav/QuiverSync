package org.example.quiversync.features.quiver.add_board

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.domain.usecase.quiver.AddBoardUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.register.OnboardingState
import org.example.quiversync.features.user.UserState
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.local.Error

class AddBoardViewModel(
    private val addBoardUseCase: AddBoardUseCase,
    private val uploadSurfboardImageUseCase: UploadImageUseCase

): BaseViewModel() {
    private val _uiState = MutableStateFlow<AddBoardState>(AddBoardState.Idle(AddBoardFormData()))
    val uiState: StateFlow<AddBoardState> get() = _uiState

    fun onEvent(event: AddBoardEvent) {
        when (event) {
            is AddBoardEvent.ModelChanged -> updateState { it.copy(data = it.data.copy(model = event.value, modelError = null)) }
            is AddBoardEvent.CompanyChanged -> updateState { it.copy(data = it.data.copy(company = event.value, companyError = null)) }
            is AddBoardEvent.BoardTypeChanged -> updateState { it.copy(data = it.data.copy(boardType = event.value)) }
            is AddBoardEvent.HeightChanged -> updateState { it.copy(data = it.data.copy(height = event.value, heightError = null)) }
            is AddBoardEvent.WidthChanged -> updateState { it.copy(data = it.data.copy(width = event.value, widthError = null)) }
            is AddBoardEvent.VolumeChanged -> updateState { it.copy(data = it.data.copy(volume = event.value, volumeError = null)) }
            is AddBoardEvent.FinsSetupChanged -> updateState { it.copy(data = it.data.copy(finSetup = event.value)) }
            is AddBoardEvent.surfboardImageSelected -> onSurfboardImageSelected(event.bytes)
            AddBoardEvent.NextStepClicked -> {
                if (!validateCurrentStep()) {
                    return
                }
                val currentState = _uiState.value
                if (currentState is AddBoardState.Idle) {
                    if (currentState.data.currentStep < currentState.data.totalSteps) {
                        _uiState.update {
                            currentState.copy(data = currentState.data.copy(currentStep = currentState.data.currentStep + 1))
                        }
                    }
                }

            }
            AddBoardEvent.PreviousStepClicked -> {
                val currentState = _uiState.value
                if (currentState is AddBoardState.Idle) {
                    if (currentState.data.currentStep > 1) {
                        currentState.copy(currentState.data.copy(currentStep = currentState.data.currentStep - 1))
                    }
                }
            }
            AddBoardEvent.SubmitClicked -> {
                if (!validateCurrentStep()) {
                    return
                }
                val formData = (_uiState.value as? AddBoardState.Idle)?.data ?: return
                scope.launch {
                    _uiState.value = AddBoardState.Loading
                    try {
                        val surfboard = Surfboard(
                            id = "",
                            ownerId = "",
                            model = formData.model,
                            company = formData.company,
                            type = formData.boardType,
                            imageRes = formData.imageUrl ?: "",
                            height = formData.height,
                            volume = formData.volume,
                            width = formData.width,
                            addedDate = "",
                            isRentalPublished = false,
                            isRentalAvailable = false,
                            finSetup = formData.finSetup,
                        )

                        val result = addBoardUseCase(surfboard)
                        when(result){
                            is Result.Success -> {
                                result.data?.let { _uiState.emit(AddBoardState.Loaded) }
                                platformLogger("AddBoardViewModel", "Surfboard added successfully")
                            }
                            is Result.Failure -> {
                                _uiState.value = AddBoardState.Error(result.error?.message ?: "Unknown error")
                                platformLogger("AddBoardViewModel", "Error: ${result.error?.message}")
                            }
                        }
                    } catch (e: Exception) {
                        _uiState.value = AddBoardState.Error(e.message ?: "Unexpected error")
                    }
                }
            }
        }
    }


    private fun updateState(update: (AddBoardState.Idle) -> AddBoardState.Idle) {
        val currentState = _uiState.value
        if (currentState is AddBoardState.Idle) {
            _uiState.update { update(currentState) }
        }
    }


    fun onSurfboardImageSelected(imageBytes: ByteArray) {
        val currentIdleState = _uiState.value as? AddBoardState.Idle ?: return

        scope.launch {
            _uiState.value = currentIdleState.copy(data = currentIdleState.data.copy(isUploadingImage = true, imageUploadError = null))

            try {
                val result = uploadSurfboardImageUseCase(
                    bytes = imageBytes,
                    folder = UploadImageUseCase.Folder.SURFBOARDS
                )

                val updatedIdleState = _uiState.value as? AddBoardState.Idle ?: return@launch

                result.onSuccess { imageUrl ->
                    _uiState.value = updatedIdleState.copy(data = updatedIdleState.data.copy(
                        isUploadingImage = false,
                        surfboardImageError = null,
                    ))
                    platformLogger("AddBoardViewModel","Success: $imageUrl")
                }.onFailure { error ->
                    _uiState.value = updatedIdleState.copy(data = updatedIdleState.data.copy(
                        isUploadingImage = false,
                        imageUploadError = "Upload failed: ${error.message}"
                    ))
                    platformLogger("AddBoardViewModel","Error: ${error.message}")
                }

            } catch (e: Exception) {
                val fallbackIdle = _uiState.value as? AddBoardState.Idle ?: return@launch
                _uiState.value = fallbackIdle.copy(data = fallbackIdle.data.copy(
                    isUploadingImage = false,
                    imageUploadError = "Unexpected error: ${e.message}"
                ))
            }
        }
    }

    private fun validateCurrentStep(): Boolean {
        val currentState = _uiState.value as? AddBoardState.Idle ?: return false
        val data = currentState.data
        var isValid = true

        val modelError = if (data.model.isBlank()) {
            isValid = false
            "Model is required"
        } else null

        val companyError = if (data.company.isBlank()) {
            isValid = false
            "Company is required"
        } else null

        val heightError = if (data.height.isBlank() || data.height.toDouble() > 14.0 || data.height.toDouble() < 0.0) {
            isValid = false
            "Valid height is required"
        } else null

        val widthError = if (data.width.isBlank()) {
            isValid = false
            "Width is required"
        } else null

        val volumeError = if (data.volume.isBlank()) {
            isValid = false
            "Volume is required"
        } else null

        updateState {
            it.copy(
                data = it.data.copy(
                    modelError = modelError,
                    companyError = companyError,
                    heightError = heightError,
                    widthError = widthError,
                    volumeError = volumeError
                )
            )
        }

        return isValid
    }



    fun resetStateToIdle() {
        _uiState.value = AddBoardState.Idle()
    }
}