package com.example.kotlin_login.data.repository

import com.example.kotlin_login.data.datasource.SyncPassLocalDataSource
import com.example.kotlin_login.data.datasource.SyncPassRemoteDataSource
import com.example.kotlin_login.data.local.AsistenciaEntity
import com.example.kotlin_login.data.local.EncuestaEntity
import com.example.kotlin_login.data.local.TallerEntity
import com.example.kotlin_login.data.remote.AsistenciaRemoteDto
import com.example.kotlin_login.data.remote.CreateTallerRequestDto
import com.example.kotlin_login.data.remote.EncuestaRequestDto
import com.example.kotlin_login.data.remote.TallerRemoteDto
import com.example.kotlin_login.domain.model.AsistenciaRegistro
import com.example.kotlin_login.domain.model.EncuestaSatisfaccion
import com.example.kotlin_login.domain.model.TallerEvento
import com.example.kotlin_login.domain.repository.SyncPassRepository
import java.util.UUID

/**
 * Implementación del Repositorio Unificado de SyncPass (Offline-First).
 * Coordina el almacenamiento local (Room/SQLite vía SyncPassLocalDataSource) y el
 * servicio HTTP remoto conectado a Supabase (vía SyncPassRemoteDataSource).
 */
class SyncPassRepositoryImpl(
    private val localDataSource: SyncPassLocalDataSource,
    private val remoteDataSource: SyncPassRemoteDataSource
) : SyncPassRepository {

    override suspend fun getTalleres(forceRemote: Boolean): List<TallerEvento> {
        if (forceRemote) {
            runCatching {
                val response = remoteDataSource.getTalleres()
                val remoteItems = response.datos.ifEmpty { response.talleres }
                if (remoteItems.isNotEmpty()) {
                    val entities = remoteItems.map { it.toEntity() }
                    localDataSource.saveTalleres(entities)
                }
            }
        }
        return localDataSource.getTalleres().map { it.toDomain() }
    }

    override suspend fun createTaller(
        nombre: String,
        lugar: String,
        fechaHora: String,
        cuposMaximos: Int,
        isOffline: Boolean
    ): List<TallerEvento> {
        val localId = UUID.randomUUID().toString()
        val localEntity = TallerEntity(
            id = localId,
            proyectoId = "00000000-0000-0000-0001-000000000001",
            nombre = nombre.trim(),
            fechaHora = fechaHora.trim(),
            lugar = lugar.trim(),
            cuposMaximos = cuposMaximos.coerceAtLeast(1),
            cuposOcupados = 0,
            codigoQr = "EVT-${localId.take(8).uppercase()}",
            estado = "Activo",
            sincronizado = !isOffline
        )
        localDataSource.saveTaller(localEntity)

        if (!isOffline) {
            runCatching {
                val res = remoteDataSource.createTaller(
                    CreateTallerRequestDto(
                        nombre = nombre.trim(),
                        lugar = lugar.trim(),
                        fechaHora = fechaHora.trim(),
                        cuposMaximos = cuposMaximos.coerceAtLeast(1),
                        estado = "Activo"
                    )
                )
                val list = res.talleres.ifEmpty { res.datos }
                if (list.isNotEmpty()) {
                    localDataSource.saveTalleres(list.map { it.toEntity() })
                }
            }
        }
        return localDataSource.getTalleres().map { it.toDomain() }
    }

    override suspend fun deleteTaller(tallerId: String): List<TallerEvento> {
        localDataSource.deleteTaller(tallerId)
        runCatching {
            val res = remoteDataSource.deleteTaller(tallerId)
            val list = res.talleres.ifEmpty { res.datos }
            if (list.isNotEmpty()) {
                localDataSource.saveTalleres(list.map { it.toEntity() })
            }
        }
        return localDataSource.getTalleres().map { it.toDomain() }
    }

    override suspend fun getAsistencias(forceRemote: Boolean): List<AsistenciaRegistro> {
        if (forceRemote) {
            runCatching {
                val res = remoteDataSource.getAsistencias()
                if (res.datos.isNotEmpty()) {
                    val entities = res.datos.map { dto ->
                        AsistenciaEntity(
                            id = dto.id,
                            tallerId = dto.tallerId,
                            rut = dto.rut,
                            nombreBeneficiario = dto.nombreBeneficiario,
                            timestamp = dto.timestamp,
                            metodo = dto.metodo,
                            sincronizado = true,
                            latitud = dto.latitud,
                            longitud = dto.longitud,
                            distanciaMetros = dto.distanciaMetros,
                            estadoValidacion = dto.estadoValidacion,
                            deviceId = dto.deviceId
                        )
                    }
                    localDataSource.saveAsistencias(entities)
                }
            }
        }
        return localDataSource.getAsistencias().map { it.toDomain() }
    }

    override suspend fun registrarAsistencia(
        tallerId: String,
        rutOrCode: String,
        nombreBeneficiario: String,
        metodo: String,
        latitud: Double,
        longitud: Double,
        distanciaMetros: Int,
        estadoValidacion: String,
        isOffline: Boolean
    ): AsistenciaRegistro {
        val id = UUID.randomUUID().toString()
        val entity = AsistenciaEntity(
            id = id,
            tallerId = tallerId,
            rut = rutOrCode.trim(),
            nombreBeneficiario = nombreBeneficiario.trim(),
            timestamp = System.currentTimeMillis(),
            metodo = metodo,
            sincronizado = !isOffline,
            latitud = latitud,
            longitud = longitud,
            distanciaMetros = distanciaMetros,
            estadoValidacion = estadoValidacion,
            deviceId = "ANDROID-SYNCPASS-01"
        )
        localDataSource.saveAsistencia(entity)

        if (!isOffline) {
            runCatching {
                remoteDataSource.syncAsistencias(listOf(entity.toRemoteDto()))
                localDataSource.markAsistenciasSynced(listOf(id))
            }.onFailure {
                // Si falla la red en el momento del envío, queda en almacenamiento local como pendiente
                localDataSource.saveAsistencia(entity.copy(sincronizado = false))
            }
        }

        return localDataSource.getAsistencias()
            .firstOrNull { it.id == id }
            ?.toDomain()
            ?: entity.toDomain()
    }

    override suspend fun getPendingAsistenciasCount(): Int =
        localDataSource.getPendingAsistencias().size

    override suspend fun syncPendingAsistencias(): Int {
        val pending = localDataSource.getPendingAsistencias()
        if (pending.isEmpty()) return 0

        runCatching {
            remoteDataSource.syncAsistencias(pending.map { it.toRemoteDto() })
        }
        val ids = pending.map { it.id }
        localDataSource.markAsistenciasSynced(ids)
        return ids.size
    }

    override suspend fun registrarEncuesta(
        tallerId: String,
        rutBeneficiario: String,
        estrellas: Int,
        utilidad: Int,
        comentarios: String,
        isOffline: Boolean
    ): EncuestaSatisfaccion {
        val id = UUID.randomUUID().toString()
        val entity = EncuestaEntity(
            id = id,
            tallerId = tallerId,
            rutBeneficiario = rutBeneficiario,
            calificacionEstrellas = estrellas.coerceIn(1, 5),
            utilidad = utilidad.coerceIn(1, 5),
            comentarios = comentarios.trim(),
            timestamp = System.currentTimeMillis(),
            sincronizado = !isOffline,
            deviceId = "ANDROID-SYNCPASS-01"
        )
        localDataSource.saveEncuesta(entity)

        if (!isOffline) {
            runCatching {
                remoteDataSource.submitEncuesta(
                    EncuestaRequestDto(
                        id = id,
                        tallerId = tallerId,
                        rutBeneficiario = rutBeneficiario,
                        calificacionEstrellas = entity.calificacionEstrellas,
                        utilidad = entity.utilidad,
                        comentarios = entity.comentarios,
                        deviceId = entity.deviceId
                    )
                )
                localDataSource.markEncuestasSynced(listOf(id))
            }.onFailure {
                localDataSource.saveEncuesta(entity.copy(sincronizado = false))
            }
        }

        return entity.toDomain()
    }

    override suspend fun syncPendingEncuestas(): Int {
        val pending = localDataSource.getPendingEncuestas()
        if (pending.isEmpty()) return 0

        pending.forEach { entity ->
            runCatching {
                remoteDataSource.submitEncuesta(
                    EncuestaRequestDto(
                        id = entity.id,
                        tallerId = entity.tallerId,
                        rutBeneficiario = entity.rutBeneficiario,
                        calificacionEstrellas = entity.calificacionEstrellas,
                        utilidad = entity.utilidad,
                        comentarios = entity.comentarios,
                        deviceId = entity.deviceId
                    )
                )
            }
        }
        val ids = pending.map { it.id }
        localDataSource.markEncuestasSynced(ids)
        return ids.size
    }

    override suspend fun getUsuarios(): List<RemoteUserItem> {
        runCatching {
            val res = remoteDataSource.getUsuarios()
            if (res.datos.isNotEmpty()) {
                return res.datos.map { dto ->
                    val roleLabel = when (dto.role.lowercase()) {
                        "admin", "administrador" -> "Administrador General"
                        "coordinator", "docente" -> "Docente / Coordinador"
                        "volunteer", "voluntario", "estudiante_voluntario" -> "Estudiante Voluntario"
                        else -> "Beneficiario Comunitario"
                    }
                    RemoteUserItem(
                        id = dto.id.ifBlank { UUID.randomUUID().toString() },
                        nombre = dto.nombre.ifBlank { dto.email },
                        email = dto.email,
                        role = roleLabel,
                        activo = dto.activo
                    )
                }
            }
        }
        return TallerRemoteRepository.fetchUsuarios()
    }

    private fun TallerRemoteDto.toEntity() = TallerEntity(
        id = id.ifBlank { UUID.randomUUID().toString() },
        proyectoId = "00000000-0000-0000-0001-000000000001",
        nombre = nombre.ifBlank { "Taller Comunitario" },
        fechaHora = fechaHora.ifBlank { "Hoy, 15:00 hrs" },
        lugar = lugar.ifBlank { "Sede Vecinal" },
        cuposMaximos = cuposMaximos.coerceAtLeast(1),
        cuposOcupados = cuposOcupados.coerceAtLeast(0),
        codigoQr = codigoQr.ifBlank { "EVT-${id.take(8).uppercase()}" },
        estado = estado.ifBlank { "Activo" },
        sincronizado = true
    )

    private fun AsistenciaEntity.toRemoteDto() = AsistenciaRemoteDto(
        id = id,
        tallerId = tallerId,
        rut = rut,
        nombreBeneficiario = nombreBeneficiario,
        timestamp = timestamp,
        metodo = metodo,
        sincronizado = true,
        latitud = latitud,
        longitud = longitud,
        distanciaMetros = distanciaMetros,
        estadoValidacion = estadoValidacion,
        deviceId = deviceId
    )
}
