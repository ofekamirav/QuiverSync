package org.example.quiversync.features.register

import org.example.quiversync.domain.model.SurfLevel
import org.example.quiversync.features.user.edit_user.EditUserDetailsEvent

sealed interface OnboardingEvent {
    data class PhoneNumberChanged(val value: String) : OnboardingEvent
    data class DateOfBirthChanged(val value: String) : OnboardingEvent
    data class HeightChanged(val value: String) : OnboardingEvent
    data class WeightChanged(val value: String) : OnboardingEvent
    data class SurfLevelChanged(val level: SurfLevel) : OnboardingEvent
    data class ProfileImageSelected(val bytes: ByteArray) : OnboardingEvent
    data class ProfileImageIOSChanged(val imageURL : String) : OnboardingEvent
    data class OnAgreementChange(val isAgreed: Boolean) : OnboardingEvent
    object ContinueClicked : OnboardingEvent
}