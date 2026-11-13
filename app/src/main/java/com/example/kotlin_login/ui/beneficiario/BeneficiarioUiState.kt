package com.example.kotlin_login.ui.beneficiario

/**
 * Estado inmutable de la vista de Portal del Beneficiario.
 */
data class BeneficiarioUiState(
    val qrPersonal: String = "SYNCPASS-BENEFICIARIO-12345678-9",
    val nombreActividad: String = "Alfabetización Digital para Adultos Mayores",
    val sedeActividad: String = "Sede: Junta de Vecinos N°14 Villa El Roble",
    val horarioActividad: String = "Horario: Hoy de 15:00 a 17:30 hrs",
    val rating: Int = 5,
    val utilityLevel: Int = 4,
    val comments: String = "",
    val isOffline: Boolean = false,
    val isSubmitting: Boolean = false,
    val surveySent: Boolean = false,
    val statusMessage: String? = null
)
