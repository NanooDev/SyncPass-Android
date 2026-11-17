package com.example.kotlin_login.ui.admin

import com.example.kotlin_login.data.repository.RemoteUserItem

data class AdminMetric(val label: String, val value: String, val detail: String)

/**
 * Estado inmutable de la vista de Administración General (Gobernanza y RBAC).
 */
data class AdminUiState(
    val isSyncing: Boolean = false,
    val isSendingNotification: Boolean = false,
    val talleresActivosCount: Int = 3,
    val asistenciasRegistradasCount: Int = 128,
    val usersList: List<RemoteUserItem> = listOf(
        RemoteUserItem("usr-1", "Administrador SyncPass", "admin.syncpass@duocuc.cl", "Administrador General", true),
        RemoteUserItem("usr-2", "Prof. Mariano Acevedo", "docente.coordinador@duocuc.cl", "Docente / Coordinador", true),
        RemoteUserItem("usr-3", "Álvaro León", "voluntario.estudiante@duocuc.cl", "Estudiante Voluntario", true),
        RemoteUserItem("usr-4", "María González", "maria.gonzalez@comunidad.cl", "Beneficiario Comunitario", true)
    ),
    val notificationFeedback: String? = null
) {
    val metrics: List<AdminMetric>
        get() = listOf(
            AdminMetric("Talleres de la semana", "$talleresActivosCount", "Sedes Maipú y Lo Espejo"),
            AdminMetric("Equipo activo", "${usersList.size}", "Docentes y voluntarios"),
            AdminMetric("Vecinos acreditados", "$asistenciasRegistradasCount", "Con código QR y RUT"),
            AdminMetric("Valoración media", "4.8 / 5", "Encuestas al cierre")
        )
}
