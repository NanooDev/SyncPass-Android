package com.example.kotlin_login.domain.model

/**
 * Entidades de Dominio del Caso SyncPass (Duoc UC • Vinculación con el Medio).
 * Diseñadas con soporte Offline-First y validación de Geocerca GPS.
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
    val estado: String,
    val latitudRecinto: Double = -33.5012,
    val longitudRecinto: Double = -70.6890,
    val radioGeofenceMetros: Int = 150,
    val sincronizado: Boolean = true
)

data class AsistenciaRegistro(
    val id: String,
    val tallerId: String,
    val rut: String,
    val nombreBeneficiario: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metodo: String = "QR", // QR o MANUAL
    val sincronizado: Boolean = true,
    val latitud: Double = -33.5015,
    val longitud: Double = -70.6887,
    val distanciaMetros: Int = 35,
    val estadoValidacion: String = "valido", // valido, fuera_de_rango, duplicado
    val deviceId: String = "ANDROID-SYNCPASS-01"
)

data class EncuestaSatisfaccion(
    val id: String,
    val tallerId: String,
    val rutBeneficiario: String,
    val calificacionEstrellas: Int,
    val utilidad: Int,
    val comentarios: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = true,
    val deviceId: String = "ANDROID-SYNCPASS-01"
)
