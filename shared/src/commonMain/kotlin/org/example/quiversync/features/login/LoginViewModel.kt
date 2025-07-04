package org.example.quiversync.features.login

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.domain.usecase.loginUseCases.LoginUserUseCase
import org.example.quiversync.features.BaseViewModel

class LoginViewModel (
    private val  loginUseCase : LoginUserUseCase
): BaseViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle(LoginData()))
    val loginState: StateFlow<LoginState> = _loginState

    fun onEvent(event: LoginEvent){
        when(event){
            is LoginEvent.EmailChanged ->{
                updateState { it.copy(email = event.value , emailError = null) }
            }
            is LoginEvent.PasswordChanged -> {
                updateState{it.copy(password = event.value, passwordError = null)}

            }
            LoginEvent.SignUpClicked -> {
                validateAndLogin()
            }
        }
    }

    private fun validateAndLogin(){
        val currentState = _loginState.value as? LoginState.Idle ?: return

        val emailError = if (currentState.data.email.isBlank() && currentState.data.email =="") "Email is required" else null
        val passwordError = if (currentState.data.password.isBlank() && currentState.data.password =="") "Password is required" else null

        val hasErrors = listOf( emailError, passwordError).any { it != null }

        updateState {
            it.copy(
                emailError = emailError,
                passwordError = passwordError
            )
        }
        if (hasErrors) return

        scope.launch {
            _loginState.value = LoginState.Loading
            val result = loginUseCase(
                email = currentState.data.email,
                password = currentState.data.password
            )
            result.onSuccess {
                _loginState.value = LoginState.Loaded
            }.onFailure { error ->
                _loginState.value = LoginState.Error(error.message ?: "Unknown error")
            }
        }

    }

    private fun updateState(update: (LoginData)->LoginData){
        val currentState = _loginState.value
        if (currentState is LoginState.Idle) {
            _loginState.update {
                LoginState.Idle(update(currentState.data))
            }
        }
    }
}
