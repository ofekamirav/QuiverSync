package org.example.quiversync.features.register

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.OnboardingProfileDetails
import org.example.quiversync.features.BaseViewModel

class OnboardingViewModel(
    private val registerUseCases: RegisterUseCases,
): BaseViewModel() {

    private val _onboardingState = MutableStateFlow<OnboardingState>(OnboardingState.Idle)
    val onboardingState: StateFlow<OnboardingState> get() = _onboardingState

    fun completeProfile(details: OnboardingProfileDetails) {

        if (details.heightCm.isBlank() || details.weightKg.isBlank() || details.dateOfBirth.isBlank()) {
            _onboardingState.value = OnboardingState.Error("Please fill all fields.")
            return
        }

        val height = details.heightCm.toIntOrNull()
        val weight = details.weightKg.toIntOrNull()

        if (height == null || weight == null) {
            _onboardingState.value = OnboardingState.Error("Height and Weight must be valid numbers.")
            return
        }

        scope.launch {
            _onboardingState.value = OnboardingState.Loading

            val result = registerUseCases.updateUserProfile(details)

            result.onSuccess {
                _onboardingState.value = OnboardingState.Success
            }.onFailure { exception ->
                _onboardingState.value = OnboardingState.Error(exception.message ?: "An unknown error occurred.")
            }
        }
    }
    fun resetState() {
        _onboardingState.value = OnboardingState.Idle
    }

}