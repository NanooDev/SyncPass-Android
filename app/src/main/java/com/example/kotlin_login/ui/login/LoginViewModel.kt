package com.example.kotlin_login.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_login.data.remote.RetrofitClient
import com.example.kotlin_login.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(RetrofitClient.authApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, usernameError = null, loginError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, loginError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClick() {
        val current = _uiState.value
        val usernameError = if (current.username.isBlank()) "Ingresa tu usuario o correo" else null
        val passwordError = if (current.password.length < 6) "Mínimo 6 caracteres" else null
        if (usernameError != null || passwordError != null) {
            _uiState.update { it.copy(usernameError = usernameError, passwordError = passwordError) }
            return
        }

        _uiState.update { it.copy(isLoading = true, loginError = null) }

        viewModelScope.launch {
            val result = repository.login(current.username, current.password)
            result
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            authToken = response.access_token
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = "No se pudo iniciar sesión. Revisa tus datos o tu conexión."
                        )
                    }
                }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(loginError = null) }
    }
}
