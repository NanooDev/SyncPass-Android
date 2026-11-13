package com.example.kotlin_login.data.datasource

import com.example.kotlin_login.data.remote.AsistenciaRemoteDto
import com.example.kotlin_login.data.remote.AsistenciasResponseDto
import com.example.kotlin_login.data.remote.CreateTallerRequestDto
import com.example.kotlin_login.data.remote.EncuestaRequestDto
import com.example.kotlin_login.data.remote.EncuestaResponseDto
import com.example.kotlin_login.data.remote.SyncAsistenciasRequestDto
import com.example.kotlin_login.data.remote.SyncPassApiService
import com.example.kotlin_login.data.remote.TalleresResponseDto
import com.example.kotlin_login.data.remote.UsuariosResponseDto

/**
 * Fuente de datos remota (Remote DataSource).
 * Abstrae las llamadas HTTP de Retrofit para que el repositorio no dependa de anotaciones HTTP.
 */
interface SyncPassRemoteDataSource {
    suspend fun getTalleres(): TalleresResponseDto
    suspend fun createTaller(request: CreateTallerRequestDto): TalleresResponseDto
    suspend fun deleteTaller(id: String): TalleresResponseDto
    suspend fun getAsistencias(): AsistenciasResponseDto
    suspend fun syncAsistencias(items: List<AsistenciaRemoteDto>): AsistenciasResponseDto
    suspend fun submitEncuesta(request: EncuestaRequestDto): EncuestaResponseDto
    suspend fun getUsuarios(): UsuariosResponseDto
}

class SyncPassRemoteDataSourceImpl(
    private val apiService: SyncPassApiService
) : SyncPassRemoteDataSource {

    override suspend fun getTalleres(): TalleresResponseDto =
        apiService.getTalleres()

    override suspend fun createTaller(request: CreateTallerRequestDto): TalleresResponseDto =
        apiService.createTaller(request)

    override suspend fun deleteTaller(id: String): TalleresResponseDto =
        apiService.deleteTaller(id)

    override suspend fun getAsistencias(): AsistenciasResponseDto =
        apiService.getAsistencias()

    override suspend fun syncAsistencias(items: List<AsistenciaRemoteDto>): AsistenciasResponseDto =
        apiService.syncAsistencias(SyncAsistenciasRequestDto(registros = items))

    override suspend fun submitEncuesta(request: EncuestaRequestDto): EncuestaResponseDto =
        apiService.submitEncuesta(request)

    override suspend fun getUsuarios(): UsuariosResponseDto =
        apiService.getUsuarios()
}
