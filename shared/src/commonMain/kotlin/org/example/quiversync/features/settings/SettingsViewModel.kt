package org.example.quiversync.features.settings

import androidx.compose.runtime.mutableStateOf
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

    private val _isImperial = MutableStateFlow(false)
    val isImperial: StateFlow<Boolean> = _isImperial

    init {
        checkUserAuthMethod()
        scope.launch {
            _isImperial.value = sessionManager.getUnitsPreference() == "imperial"
        }
    }

    fun toggleUnits() {
        val next = !_isImperial.value
        scope.launch {
            sessionManager.setUnitsPreference(if (next) "imperial" else "metric")
        }
        _isImperial.value = next
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