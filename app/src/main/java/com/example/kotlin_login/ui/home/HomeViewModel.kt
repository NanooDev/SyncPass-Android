package com.example.kotlin_login.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin_login.core.jwt.JwtDecoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(accessToken: String) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        JwtDecoder.decode(accessToken)
            .onSuccess { user -> _uiState.update { it.copy(user = user) } }
            .onFailure { _uiState.update { it.copy(decodeError = "Sesión inválida. Vuelve a ingresar.") } }
    }
}

class HomeViewModelFactory(private val accessToken: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(accessToken) as T
    }
}
