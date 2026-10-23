package com.example.kotlin_login.domain.model

/**
 * Entidades de Dominio del Caso SyncPass (Duoc UC • Vinculación con el Medio).
 */

data class ProyectoComunitario(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val areaTematica: String,
    val unidadVecinal: String,
    val cuposTotales: Int,
    val estado: String
)

data class TallerEvento(
    val id: String,
    val proyectoId: String,
    val nombre: String,
    val fechaHora: String,
    val lugar: String,
    val cuposMaximos: Int,
    val cuposOcupados: Int,
    val codigoQr: String,
    val estado: String
)

data class AsistenciaRegistro(
    val id: String,
    val tallerId: String,
    val rut: String,
    val nombreBeneficiario: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metodo: String = "QR", // QR o MANUAL
    val sincronizado: Boolean = true
)

data class EncuestaSatisfaccion(
    val id: String,
    val tallerId: String,
    val rutBeneficiario: String,
    val calificacionEstrellas: Int,
    val utilidad: Int,
    val comentarios: String,
    val timestamp: Long = System.currentTimeMillis()
)
