package org.example.quiversync.features.register

import org.example.quiversync.domain.model.SurfLevel

sealed interface OnboardingEvent {
    data class DateOfBirthChanged(val value: String) : OnboardingEvent
    data class HeightChanged(val value: Double) : OnboardingEvent
    data class WeightChanged(val value: Double) : OnboardingEvent
    data class SurfLevelChanged(val level: SurfLevel) : OnboardingEvent
    data class ProfileImageSelected(val bytes: ByteArray) : OnboardingEvent
    object ContinueClicked : OnboardingEvent
}