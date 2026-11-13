package com.example.kotlin_login.domain.repository

import com.example.kotlin_login.data.repository.RemoteUserItem
import com.example.kotlin_login.domain.model.AsistenciaRegistro
import com.example.kotlin_login.domain.model.EncuestaSatisfaccion
import com.example.kotlin_login.domain.model.TallerEvento

/**
 * Interfaz del Repositorio en la capa de Dominio (Inversión de Dependencias).
 * El dominio y los ViewModels dependen únicamente de esta abstracción y no conocen
 * si los datos provienen de Room (Local) o de Retrofit/Supabase (Remoto).
 */
interface SyncPassRepository {
    suspend fun getTalleres(forceRemote: Boolean = true): List<TallerEvento>
    suspend fun createTaller(
        nombre: String,
        lugar: String,
        fechaHora: String,
        cuposMaximos: Int,
        isOffline: Boolean = false
    ): List<TallerEvento>
    suspend fun deleteTaller(tallerId: String): List<TallerEvento>

    suspend fun getAsistencias(forceRemote: Boolean = false): List<AsistenciaRegistro>
    suspend fun registrarAsistencia(
        tallerId: String,
        rutOrCode: String,
        nombreBeneficiario: String,
        metodo: String,
        latitud: Double,
        longitud: Double,
        distanciaMetros: Int,
        estadoValidacion: String,
        isOffline: Boolean
    ): AsistenciaRegistro
    suspend fun getPendingAsistenciasCount(): Int
    suspend fun syncPendingAsistencias(): Int

    suspend fun registrarEncuesta(
        tallerId: String,
        rutBeneficiario: String,
        estrellas: Int,
        utilidad: Int,
        comentarios: String,
        isOffline: Boolean = false
    ): EncuestaSatisfaccion
    suspend fun syncPendingEncuestas(): Int

    suspend fun getUsuarios(): List<RemoteUserItem>
}
