package com.example.kotlin_login.ui.voluntario

import com.example.kotlin_login.domain.model.AsistenciaRegistro

/**
 * Estado inmutable de la vista de Acreditación en Terreno (Estudiante Voluntario).
 */
data class VoluntarioUiState(
    val isOffline: Boolean = false,
    val isSyncing: Boolean = false,
    val pendingSyncCount: Int = 1,
    val asistencias: List<AsistenciaRegistro> = emptyList(),
    val latitudActual: Double = -33.5015,
    val longitudActual: Double = -70.6887,
    val distanciaRecintoMetros: Int = 35,
    val dentroDeGeocerca: Boolean = true,
    val camaraTraseraActiva: Boolean = true,
    val feedbackMessage: String? = null
)
