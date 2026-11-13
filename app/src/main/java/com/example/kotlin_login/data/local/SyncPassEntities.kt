package com.example.kotlin_login.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kotlin_login.domain.model.AsistenciaRegistro
import com.example.kotlin_login.domain.model.EncuestaSatisfaccion
import com.example.kotlin_login.domain.model.TallerEvento

/**
 * Entidades locales Room/SQLite para persistencia Offline-First en Android.
 */

@Entity(tableName = "evento_taller_local")
data class TallerEntity(
    @PrimaryKey val id: String,
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
) {
    fun toDomain() = TallerEvento(
        id = id,
        proyectoId = proyectoId,
        nombre = nombre,
        fechaHora = fechaHora,
        lugar = lugar,
        cuposMaximos = cuposMaximos,
        cuposOcupados = cuposOcupados,
        codigoQr = codigoQr,
        estado = estado,
        latitudRecinto = latitudRecinto,
        longitudRecinto = longitudRecinto,
        radioGeofenceMetros = radioGeofenceMetros,
        sincronizado = sincronizado
    )
}

fun TallerEvento.toEntity() = TallerEntity(
    id = id,
    proyectoId = proyectoId,
    nombre = nombre,
    fechaHora = fechaHora,
    lugar = lugar,
    cuposMaximos = cuposMaximos,
    cuposOcupados = cuposOcupados,
    codigoQr = codigoQr,
    estado = estado,
    latitudRecinto = latitudRecinto,
    longitudRecinto = longitudRecinto,
    radioGeofenceMetros = radioGeofenceMetros,
    sincronizado = sincronizado
)

@Entity(tableName = "registro_asistencia_local")
data class AsistenciaEntity(
    @PrimaryKey val id: String,
    val tallerId: String,
    val rut: String,
    val nombreBeneficiario: String,
    val timestamp: Long,
    val metodo: String,
    val sincronizado: Boolean,
    val latitud: Double,
    val longitud: Double,
    val distanciaMetros: Int,
    val estadoValidacion: String,
    val deviceId: String
) {
    fun toDomain() = AsistenciaRegistro(
        id = id,
        tallerId = tallerId,
        rut = rut,
        nombreBeneficiario = nombreBeneficiario,
        timestamp = timestamp,
        metodo = metodo,
        sincronizado = sincronizado,
        latitud = latitud,
        longitud = longitud,
        distanciaMetros = distanciaMetros,
        estadoValidacion = estadoValidacion,
        deviceId = deviceId
    )
}

fun AsistenciaRegistro.toEntity() = AsistenciaEntity(
    id = id,
    tallerId = tallerId,
    rut = rut,
    nombreBeneficiario = nombreBeneficiario,
    timestamp = timestamp,
    metodo = metodo,
    sincronizado = sincronizado,
    latitud = latitud,
    longitud = longitud,
    distanciaMetros = distanciaMetros,
    estadoValidacion = estadoValidacion,
    deviceId = deviceId
)

@Entity(tableName = "encuesta_valoracion_local")
data class EncuestaEntity(
    @PrimaryKey val id: String,
    val tallerId: String,
    val rutBeneficiario: String,
    val calificacionEstrellas: Int,
    val utilidad: Int,
    val comentarios: String,
    val timestamp: Long,
    val sincronizado: Boolean,
    val deviceId: String
) {
    fun toDomain() = EncuestaSatisfaccion(
        id = id,
        tallerId = tallerId,
        rutBeneficiario = rutBeneficiario,
        calificacionEstrellas = calificacionEstrellas,
        utilidad = utilidad,
        comentarios = comentarios,
        timestamp = timestamp,
        sincronizado = sincronizado,
        deviceId = deviceId
    )
}

fun EncuestaSatisfaccion.toEntity() = EncuestaEntity(
    id = id,
    tallerId = tallerId,
    rutBeneficiario = rutBeneficiario,
    calificacionEstrellas = calificacionEstrellas,
    utilidad = utilidad,
    comentarios = comentarios,
    timestamp = timestamp,
    sincronizado = sincronizado,
    deviceId = deviceId
)
