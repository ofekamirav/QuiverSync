package org.example.quiversync.features.login

sealed class LoginState {
    data class Idle(val data : LoginData) : LoginState()
    data object Loading : LoginState()
    data object Loaded : LoginState()
    data class Error(val message: String) : LoginState()
    object NavigateToOnboarding : LoginState()
}