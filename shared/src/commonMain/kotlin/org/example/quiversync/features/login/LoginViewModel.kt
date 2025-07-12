package org.example.quiversync.features.login

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.domain.usecase.loginUseCases.LoginUserUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.usecase.loginUseCases.SignInWithGoogleUseCase
import org.example.quiversync.utils.extensions.platformLogger


class LoginViewModel (
    private val  loginUseCase : LoginUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
): BaseViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle(LoginData()))
    val loginState: StateFlow<LoginState> = _loginState

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                updateState { it.copy(email = event.value, emailError = null) }
            }

            is LoginEvent.PasswordChanged -> {
                updateState { it.copy(password = event.value, passwordError = null) }
            }

            LoginEvent.SignInClicked -> {
                validateAndLogin()
            }
        }
    }

    private fun validateAndLogin(){
        val currentState = _loginState.value as? LoginState.Idle ?: return

        val emailError = if (!isEmailValid(currentState.data.email)) "Invalid email address" else null
        val passwordError = validatePasswordStrength(currentState.data.password)

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
            platformLogger("LoginViewModel", "Logging in user with email: ${currentState.data.email}")
            val result = loginUseCase(
                email = currentState.data.email,
                password = currentState.data.password
            )
            when(result){
                is Result.Success -> {
                    _loginState.emit(LoginState.Loaded)
                }
                is Result.Failure -> {
                    val errorMessage = result.error?.message ?: "An unknown error occurred during login."
                    _loginState.emit(LoginState.Error(errorMessage))                }
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

    private fun isEmailValid(email: String): Boolean {
        val regex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return Regex(regex).matches(email)
    }

    private fun validatePasswordStrength(password: String): String? {
        if (password.length < 6) return "Password must be at least 6 characters"
        if (!password.any { it.isUpperCase() }) return "Password must contain at least one uppercase letter"
        return null
    }

    fun onGoogleSignInResult(idToken: String?) {
        if (idToken == null) {
            _loginState.value = LoginState.Error("Google Sign-In failed.")
            return
        }

        scope.launch {
            _loginState.value = LoginState.Loading
            when (val result = signInWithGoogleUseCase(idToken)) {
                is Result.Success -> {
                    result.data?.let {
                        if (it.isNewUser) {
                            _loginState.value = LoginState.NavigateToOnboarding
                            platformLogger(
                                "LoginViewModel",
                                "New user signed in with Google, navigating to onboarding."
                            )
                        } else {
                            _loginState.value = LoginState.Loaded
                            platformLogger(
                                "LoginViewModel",
                                "Existing user signed in with Google, loading main screen."
                            )
                        }
                    }
                }

                is Result.Failure -> {
                    _loginState.value = LoginState.Error(result.error?.message ?: "Unknown error")
                }
            }
        }
    }


    fun resetState() {
        _loginState.value = LoginState.Idle(LoginData())
    }
}
