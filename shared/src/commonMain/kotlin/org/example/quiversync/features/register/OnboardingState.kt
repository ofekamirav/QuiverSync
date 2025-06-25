package org.example.quiversync.features.register

sealed class OnboardingState {
    object Idle : OnboardingState ()
    object Loading : OnboardingState ()
    object Success : OnboardingState()
    data class Error(val message: String) : OnboardingState()
}
