package com.example.kotlin_login.ui.beneficiario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_login.di.RepositoryModule
import com.example.kotlin_login.domain.repository.SyncPassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del Portal del Beneficiario.
 * Maneja la lógica de valoración del taller comunitario con soporte Offline-First.
 */
class BeneficiarioViewModel(
    private val repository: SyncPassRepository = RepositoryModule.provideSyncPassRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BeneficiarioUiState())
    val uiState: StateFlow<BeneficiarioUiState> = _uiState.asStateFlow()

    fun onRatingChange(stars: Int) {
        _uiState.update { it.copy(rating = stars.coerceIn(1, 5)) }
    }

    fun onUtilityChange(level: Int) {
        _uiState.update { it.copy(utilityLevel = level.coerceIn(1, 5)) }
    }

    fun onCommentsChange(text: String) {
        _uiState.update { it.copy(comments = text) }
    }

    fun onToggleOffline(enabled: Boolean) {
        _uiState.update { it.copy(isOffline = enabled) }
    }

    fun onSubmitSurvey() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = repository.registrarEncuesta(
                tallerId = "00000000-0000-0000-0002-000000000001",
                rutBeneficiario = "BEN-00101",
                estrellas = state.rating,
                utilidad = state.utilityLevel,
                comentarios = state.comments.ifBlank { "Excelente jornada comunitaria" },
                isOffline = state.isOffline
            )
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    surveySent = true,
                    statusMessage = if (result.sincronizado) {
                        "Encuesta registrada en Room local y sincronizada con PostgreSQL (Supabase)."
                    } else {
                        "Encuesta guardada en Room local (Modo Offline) para sincronización posterior."
                    }
                )
            }
        }
    }

    fun onResetSurvey() {
        _uiState.update { it.copy(surveySent = false, comments = "", statusMessage = null) }
    }
}
