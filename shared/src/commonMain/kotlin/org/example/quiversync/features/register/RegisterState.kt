package org.example.quiversync.features.register


sealed class RegisterState {
    data class Idle(val data: RegisterFormData = RegisterFormData()) : RegisterState()
    data object Loading : RegisterState()
    data object Loaded : RegisterState()
    data class Error(val message: String) : RegisterState()
}
