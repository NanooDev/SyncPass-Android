package com.example.kotlin_login.data.remote

import com.google.gson.annotations.SerializedName

/**
 * DTOs (Data Transfer Objects) para la capa remota HTTP (Retrofit + Gson).
 * Traducen las estructuras JSON de la API (/api/talleres, /api/asistencias, /api/encuestas, /api/usuarios)
 * conectada a PostgreSQL (Supabase) hacia tipos nativos de Kotlin.
 */

data class TallerRemoteDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val nombre: String = "",
    @SerializedName("location") val lugar: String = "",
    @SerializedName("date") val fechaHora: String = "",
    @SerializedName("attendeesCount") val cuposOcupados: Int = 0,
    @SerializedName("capacity") val cuposMaximos: Int = 30,
    @SerializedName("status") val estado: String = "Activo",
    @SerializedName("qrCode") val codigoQr: String = ""
)

data class CreateTallerRequestDto(
    @SerializedName("name") val nombre: String,
    @SerializedName("location") val lugar: String,
    @SerializedName("date") val fechaHora: String,
    @SerializedName("capacity") val cuposMaximos: Int,
    @SerializedName("status") val estado: String = "Activo"
)

data class TalleresResponseDto(
    @SerializedName("exito") val exito: Boolean = true,
    @SerializedName("mensaje") val mensaje: String = "",
    @SerializedName("datos") val datos: List<TallerRemoteDto> = emptyList(),
    @SerializedName("talleres") val talleres: List<TallerRemoteDto> = emptyList()
)

data class AsistenciaRemoteDto(
    @SerializedName("id") val id: String,
    @SerializedName("tallerId") val tallerId: String,
    @SerializedName("rut") val rut: String,
    @SerializedName("nombreBeneficiario") val nombreBeneficiario: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("metodo") val metodo: String,
    @SerializedName("sincronizado") val sincronizado: Boolean = true,
    @SerializedName("latitud") val latitud: Double = -33.5015,
    @SerializedName("longitud") val longitud: Double = -70.6887,
    @SerializedName("distanciaMetros") val distanciaMetros: Int = 25,
    @SerializedName("estadoValidacion") val estadoValidacion: String = "valido",
    @SerializedName("deviceId") val deviceId: String = "ANDROID-SYNCPASS-01"
)

data class SyncAsistenciasRequestDto(
    @SerializedName("registros") val registros: List<AsistenciaRemoteDto>
)

data class AsistenciasResponseDto(
    @SerializedName("exito") val exito: Boolean = true,
    @SerializedName("mensaje") val mensaje: String = "",
    @SerializedName("sincronizados") val sincronizados: Int = 0,
    @SerializedName("datos") val datos: List<AsistenciaRemoteDto> = emptyList()
)

data class EncuestaRequestDto(
    @SerializedName("id") val id: String,
    @SerializedName("tallerId") val tallerId: String,
    @SerializedName("rutBeneficiario") val rutBeneficiario: String,
    @SerializedName("calificacionEstrellas") val calificacionEstrellas: Int,
    @SerializedName("utilidad") val utilidad: Int,
    @SerializedName("comentarios") val comentarios: String,
    @SerializedName("deviceId") val deviceId: String = "ANDROID-SYNCPASS-01"
)

data class EncuestaResponseDto(
    @SerializedName("exito") val exito: Boolean = true,
    @SerializedName("mensaje") val mensaje: String = ""
)

data class UsuarioRemoteDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("role") val role: String = "volunteer",
    @SerializedName("activo") val activo: Boolean = true
)

data class UsuariosResponseDto(
    @SerializedName("exito") val exito: Boolean = true,
    @SerializedName("datos") val datos: List<UsuarioRemoteDto> = emptyList()
)
