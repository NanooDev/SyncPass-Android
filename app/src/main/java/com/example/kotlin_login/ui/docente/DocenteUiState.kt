package com.example.kotlin_login.ui.docente

import com.example.kotlin_login.domain.model.TallerEvento

/**
 * Estado inmutable de la pantalla de Coordinación y Docencia (Docente / Coordinador).
 */
data class DocenteUiState(
    val talleres: List<TallerEvento> = emptyList(),
    val selectedTaller: TallerEvento? = null,
    val isSyncing: Boolean = false,
    val showCreateDialog: Boolean = false,
    val newNombre: String = "",
    val newLugar: String = "",
    val newFecha: String = "Hoy, 16:00 hrs",
    val newCupos: String = "30",
    val statusMessage: String? = null
)
