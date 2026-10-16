package com.example.kotlin_login.ui.login

data class LoginUiState(
    val username: String = "",
    val usernameError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val authToken: String? = null,
    val isSuccess: Boolean = false
) {
    val canSubmit: Boolean =
        username.isNotBlank() && password.length >= 6 && !isLoading
}
