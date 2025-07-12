package org.example.quiversync.features.register

import org.example.quiversync.domain.model.SurfLevel

sealed class OnboardingState {
    data class Idle(val data: OnboardingFormData) : OnboardingState()
    object Loading : OnboardingState()
    object Success : OnboardingState()
    data class Error(val message: String) : OnboardingState()
}