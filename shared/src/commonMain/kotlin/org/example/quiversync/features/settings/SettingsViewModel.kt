package org.example.quiversync.features.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.user.UserUseCases

class SettingsViewModel(
    private val userUseCases: UserUseCases
): BaseViewModel() {

    private val _showSecurityAndPrivacy = MutableStateFlow(false)
    val showSecurityAndPrivacy: StateFlow<Boolean> = _showSecurityAndPrivacy

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        checkUserAuthMethod()
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