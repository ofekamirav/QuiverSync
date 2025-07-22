package org.example.quiversync.features.quiver.add_board

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.domain.usecase.quiver.AddBoardUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.data.local.Result
import org.example.quiversync.utils.extensions.getDefaultImageUrlForType

class AddBoardViewModel(
    private val addBoardUseCase: AddBoardUseCase,
    private val uploadSurfboardImageUseCase: UploadImageUseCase,
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
            is AddBoardEvent.surfboardImageIOSChanged -> updateState { it.copy(data = it.data.copy(imageUrl = event.imageURL , uploadFromIOS = false)) }
            is AddBoardEvent.surfboardImageSelected -> onSurfboardImageSelected(event.bytes)
            AddBoardEvent.NextStepClicked -> {
                if ((_uiState.value as? AddBoardState.Idle)?.data?.currentStep == 1) {
                    if (!validateStep1()) {
                        return
                    }
                }
                updateState { currentState ->
                    if (currentState.data.currentStep < currentState.data.totalSteps) {
                        currentState.copy(data = currentState.data.copy(currentStep = currentState.data.currentStep + 1))
                    } else {
                        currentState
                    }
                }
            }

            AddBoardEvent.PreviousStepClicked -> {
                updateState { currentState ->
                    if (currentState.data.currentStep > 1) {
                        currentState.copy(data = currentState.data.copy(currentStep = currentState.data.currentStep - 1))
                    } else {
                        currentState
                    }
                }
            }
            AddBoardEvent.SubmitClicked -> {
                platformLogger("AddBoardViewModel", "Submit clicked with current state: ${_uiState.value}")
                if (!validateAllSteps()) {
                    return
                }
                val formData = (_uiState.value as? AddBoardState.Idle)?.data ?: return
                scope.launch {
                    _uiState.value = AddBoardState.Loading
                    val finalImageUrl = if (!formData.imageUrl.isNullOrBlank()) {
                        formData.imageUrl
                    } else {
                        formData.boardType.let { getDefaultImageUrlForType(it) }
                    }
                    try {
                        val surfboard = Surfboard(
                            id = "",
                            ownerId = "",
                            model = formData.model,
                            company = formData.company,
                            type = formData.boardType,
                            imageRes = finalImageUrl,
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


    private fun onSurfboardImageSelected(imageBytes: ByteArray) {
        println("🐛 [onSurfboardImageSelected] Triggered with ${imageBytes.size} bytes")

        val currentIdleState = _uiState.value as? AddBoardState.Idle
        if (currentIdleState == null) {
            println("❌ Not in Idle state: ${_uiState.value}")
            return
        }

        scope.launch {
            println("🚀 Upload coroutine started")
            _uiState.value = currentIdleState.copy(
                data = currentIdleState.data.copy(
                    isUploadingImage = true,
                    imageUploadError = null
                )
            )

            try {
                val updatedIdleState = _uiState.value as? AddBoardState.Idle ?: run {
                    println("❌ State changed unexpectedly: ${_uiState.value}")
                    return@launch
                }

                println("📤 Calling uploadSurfboardImageUseCase...")
                val result = uploadSurfboardImageUseCase(
                    bytes = imageBytes,
                    folder = UploadImageUseCase.Folder.SURFBOARDS
                )

                var finalUrl: String? = null


               when(result){
                    is Result.Success -> {
                        if (result.data == "IOS uploader is been activated") {
                            println("📱 iOS mode: waiting for SwiftUI to upload image")
                            _uiState.value = updatedIdleState.copy(
                                data = updatedIdleState.data.copy(uploadFromIOS = true)
                            )

                            finalUrl = uiState
                                .filterIsInstance<AddBoardState.Idle>()
                                .map { it.data.imageUrl }
                                .filterNotNull()
                                .first()

                            if (finalUrl == "failed to upload") {
                                println("❌ SwiftUI reported upload failure")
                                _uiState.value = updatedIdleState.copy(
                                    data = updatedIdleState.data.copy(
                                        isUploadingImage = false,
                                        uploadFromIOS = false,
                                        imageUploadError = "Upload failed on iOS"
                                    )
                                )
                                return@launch
                            }
                        } else {
                            // Android: use returned URL
                            finalUrl = result.data
                        }

                        println("✅ Image upload complete. URL: $finalUrl")

                        _uiState.value = updatedIdleState.copy(
                            data = updatedIdleState.data.copy(
                                imageUrl = finalUrl,
                                isUploadingImage = false,
                                uploadFromIOS = false,
                                imageUploadError = null
                            )
                        )
                    }
                    is Result.Failure -> {
                        println("❌ Upload error: ${result.error?.message}")
                        _uiState.value = updatedIdleState.copy(
                            data = updatedIdleState.data.copy(
                                isUploadingImage = false,
                                uploadFromIOS = false,
                                imageUploadError = "Upload failed: ${result.error?.message}"
                            )
                        )

                    }
               }

            } catch (e: Exception) {
                println("💥 Exception during upload: ${e.message}")
                val fallbackIdle = _uiState.value as? AddBoardState.Idle
                if (fallbackIdle != null) {
                    _uiState.value = fallbackIdle.copy(
                        data = fallbackIdle.data.copy(
                            isUploadingImage = false,
                            imageUploadError = "Unexpected error: ${e.message}"
                        )
                    )
                } else {
                    println("❌ Could not recover UI state after exception.")
                }

            }
        }
    }


    private fun validateStep1(): Boolean {
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

        updateState {
            it.copy(
                data = it.data.copy(
                    modelError = modelError,
                    companyError = companyError
                )
            )
        }

        return isValid
    }

    private fun validateAllSteps(): Boolean {
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

        val heightError = if (data.height.isBlank() || data.height.toDoubleOrNull()?.let { it > 14.0 || it < 0.0 } == true) {
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
        _uiState.value = AddBoardState.Idle(AddBoardFormData())
    }
}
