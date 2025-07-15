package org.example.quiversync.features.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.user.UserUseCases

class SettingsViewModel(
    private val userUseCases: UserUseCases,
    private val sessionManager: SessionManager
): BaseViewModel() {

    private val _showSecurityAndPrivacy = MutableStateFlow(false)
    val showSecurityAndPrivacy: StateFlow<Boolean> = _showSecurityAndPrivacy

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isImperialUnits = MutableStateFlow(false)
    val isImperialUnits: StateFlow<Boolean> = _isImperialUnits

    init {
        checkUserAuthMethod()
        scope.launch {
            _isImperialUnits.value = sessionManager.getUnitsPreference() == "imperial"
        }
    }

    fun toggleUnits() {
        scope.launch {
            val newPreference = if (_isImperialUnits.value) "metric" else "imperial"
            sessionManager.setUnitsPreference(newPreference)
            _isImperialUnits.value = !_isImperialUnits.value
        }
    }

    fun setUnits(isImperial: Boolean) {
        scope.launch {
            val newPreference = if (isImperial) "imperial" else "metric"
            sessionManager.setUnitsPreference(newPreference)
            _isImperialUnits.value = isImperial
        }
    }

    fun logout(onComplete: () -> Unit) {
        scope.launch {
            _isLoading.value = true
            userUseCases.logoutUseCase()
            _isLoading.value = false
            onComplete()
        }
    }

    private fun checkUserAuthMethod() {
        scope.launch {
            val result = userUseCases.checkUserAuthMethod()
            if (result) {
                _showSecurityAndPrivacy.value = true
            } else {
                _showSecurityAndPrivacy.value = false
            }
        }
    }

}