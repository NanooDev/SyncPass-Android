package com.example.kotlin_login.ui.docente

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_login.core.hardware.ActaStorageHelper
import com.example.kotlin_login.di.RepositoryModule
import com.example.kotlin_login.domain.model.TallerEvento
import com.example.kotlin_login.domain.repository.SyncPassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de Coordinación y Docencia.
 * Interactúa exclusivamente con SyncPassRepository (dominio) y expone DocenteUiState a la vista.
 */
class DocenteViewModel(
    private val repository: SyncPassRepository = RepositoryModule.provideSyncPassRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteUiState())
    val uiState: StateFlow<DocenteUiState> = _uiState.asStateFlow()

    init {
        syncTalleres(showFeedback = false)
    }

    fun syncTalleres(showFeedback: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val list = repository.getTalleres(forceRemote = true)
            _uiState.update { current ->
                val selected = list.find { it.id == current.selectedTaller?.id } ?: list.firstOrNull()
                current.copy(
                    talleres = list,
                    selectedTaller = selected,
                    isSyncing = false,
                    statusMessage = if (showFeedback) {
                        "Talleres sincronizados en tiempo real con PostgreSQL Supabase (/api/talleres)"
                    } else {
                        current.statusMessage
                    }
                )
            }
        }
    }

    fun onSelectTaller(taller: TallerEvento) {
        _uiState.update { it.copy(selectedTaller = taller) }
    }

    fun onOpenCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun onDismissCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }

    fun onNewNombreChange(value: String) {
        _uiState.update { it.copy(newNombre = value) }
    }

    fun onNewLugarChange(value: String) {
        _uiState.update { it.copy(newLugar = value) }
    }

    fun onNewFechaChange(value: String) {
        _uiState.update { it.copy(newFecha = value) }
    }

    fun onNewCuposChange(value: String) {
        _uiState.update { it.copy(newCupos = value) }
    }

    fun onCreateTaller() {
        val state = _uiState.value
        val nombre = state.newNombre.trim()
        val lugar = state.newLugar.trim()
        if (nombre.isBlank() || lugar.isBlank()) return

        val cupos = state.newCupos.toIntOrNull()?.coerceAtLeast(1) ?: 30
        val fecha = state.newFecha.trim().ifBlank { "Hoy, 16:00 hrs" }

        _uiState.update {
            it.copy(
                showCreateDialog = false,
                newNombre = "",
                newLugar = "",
                isSyncing = true
            )
        }

        viewModelScope.launch {
            val updated = repository.createTaller(
                nombre = nombre,
                lugar = lugar,
                fechaHora = fecha,
                cuposMaximos = cupos,
                isOffline = false
            )
            _uiState.update {
                it.copy(
                    talleres = updated,
                    selectedTaller = updated.firstOrNull(),
                    isSyncing = false,
                    statusMessage = "Taller \"$nombre\" guardado en Room local y PostgreSQL (Supabase)."
                )
            }
        }
    }

    fun onDeleteTaller(tallerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val updated = repository.deleteTaller(tallerId)
            _uiState.update {
                it.copy(
                    talleres = updated,
                    selectedTaller = updated.firstOrNull(),
                    isSyncing = false,
                    statusMessage = "Taller eliminado de la base de datos local y remota."
                )
            }
        }
    }

    fun onExportActa(context: Context?) {
        val selected = _uiState.value.selectedTaller ?: return
        viewModelScope.launch {
            val asistencias = repository.getAsistencias(forceRemote = false)
            val archivo = ActaStorageHelper.guardarActaEnDispositivo(
                context = context,
                taller = selected,
                asistencias = asistencias
            )
            _uiState.update {
                it.copy(
                    statusMessage = "Acta exportada en almacenamiento local: ${archivo.name} (${asistencias.size} registros)"
                )
            }
        }
    }
}
