package com.example.kotlin_login.fakes

import com.example.kotlin_login.data.datasource.SyncPassRemoteDataSource
import com.example.kotlin_login.data.remote.AsistenciaRemoteDto
import com.example.kotlin_login.data.remote.AsistenciasResponseDto
import com.example.kotlin_login.data.remote.CreateTallerRequestDto
import com.example.kotlin_login.data.remote.EncuestaRequestDto
import com.example.kotlin_login.data.remote.EncuestaResponseDto
import com.example.kotlin_login.data.remote.TallerRemoteDto
import com.example.kotlin_login.data.remote.TalleresResponseDto
import com.example.kotlin_login.data.remote.UsuariosResponseDto
import java.io.IOException

/**
 * Fake de SyncPassRemoteDataSource para simular respuestas HTTP de Supabase/Vercel
 * y fallas de red en los unit tests de SyncPassRepositoryImpl.
 */
class FakeSyncPassRemoteDataSource(
    var shouldFail: Boolean = false
) : SyncPassRemoteDataSource {

    val remoteTalleres = mutableListOf(
        TallerRemoteDto(
            id = "REM-T-01",
            nombre = "Taller Remoto Supabase",
            lugar = "Sede Lo Espejo",
            fechaHora = "Hoy 18:00",
            cuposOcupados = 5,
            cuposMaximos = 30,
            estado = "En curso",
            codigoQr = "QR-REM-01"
        )
    )

    val remoteAsistencias = mutableListOf<AsistenciaRemoteDto>()

    override suspend fun getTalleres(): TalleresResponseDto {
        if (shouldFail) throw IOException("Sin conexión a Supabase")
        return TalleresResponseDto(exito = true, mensaje = "OK", datos = remoteTalleres.toList())
    }

    override suspend fun createTaller(request: CreateTallerRequestDto): TalleresResponseDto {
        if (shouldFail) throw IOException("Sin conexión a Supabase")
        val item = TallerRemoteDto(
            id = "REM-T-NEW",
            nombre = request.nombre,
            lugar = request.lugar,
            fechaHora = request.fechaHora,
            cuposOcupados = 0,
            cuposMaximos = request.cuposMaximos,
            estado = request.estado
        )
        remoteTalleres.add(0, item)
        return TalleresResponseDto(exito = true, mensaje = "Creado", datos = remoteTalleres.toList())
    }

    override suspend fun deleteTaller(id: String): TalleresResponseDto {
        if (shouldFail) throw IOException("Sin conexión a Supabase")
        remoteTalleres.removeIf { it.id == id }
        return TalleresResponseDto(exito = true, mensaje = "Eliminado", datos = remoteTalleres.toList())
    }

    override suspend fun getAsistencias(): AsistenciasResponseDto {
        if (shouldFail) throw IOException("Sin conexión a Supabase")
        return AsistenciasResponseDto(exito = true, mensaje = "OK", datos = remoteAsistencias.toList())
    }

    override suspend fun syncAsistencias(items: List<AsistenciaRemoteDto>): AsistenciasResponseDto {
        if (shouldFail) throw IOException("Sin conexión a Supabase")
        remoteAsistencias.addAll(items)
        return AsistenciasResponseDto(exito = true, mensaje = "Sincronizado", sincronizados = items.size, datos = remoteAsistencias.toList())
    }

    override suspend fun submitEncuesta(request: EncuestaRequestDto): EncuestaResponseDto {
        if (shouldFail) throw IOException("Sin conexión a Supabase")
        return EncuestaResponseDto(exito = true, mensaje = "Encuesta registrada")
    }

    override suspend fun getUsuarios(): UsuariosResponseDto {
        if (shouldFail) throw IOException("Sin conexión a Supabase")
        return UsuariosResponseDto(exito = true, datos = emptyList())
    }
}
