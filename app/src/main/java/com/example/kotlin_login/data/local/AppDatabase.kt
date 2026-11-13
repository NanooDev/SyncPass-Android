package com.example.kotlin_login.data.local

import androidx.room.Database
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Contrato abstracto de base de datos Room (@Database) que expone el DAO.
 */
@Database(
    entities = [TallerEntity::class, AsistenciaEntity::class, EncuestaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class RoomDatabaseContract {
    abstract fun syncPassDao(): SyncPassDao
}

/**
 * Implementación de la base de datos local de la aplicación SyncPass (versión 2).
 * Gestiona las tablas locales de talleres, asistencias y encuestas para soporte Offline-First.
 */
class AppDatabase : RoomDatabaseContract(), SyncPassDao {

    private val talleresStore = CopyOnWriteArrayList<TallerEntity>(
        listOf(
            TallerEntity(
                id = "00000000-0000-0000-0002-000000000001",
                proyectoId = "00000000-0000-0000-0001-000000000001",
                nombre = "Taller: Uso de WhatsApp y videollamadas",
                fechaHora = "15 oct, 09:00 hrs",
                lugar = "Sede Social Villa El Roble",
                cuposMaximos = 40,
                cuposOcupados = 1,
                codigoQr = "EVT-2026-001",
                estado = "En curso",
                sincronizado = true
            )
        )
    )

    private val asistenciasStore = CopyOnWriteArrayList<AsistenciaEntity>(
        listOf(
            AsistenciaEntity(
                id = "00000000-0000-0000-0004-000000000001",
                tallerId = "00000000-0000-0000-0002-000000000001",
                rut = "BEN-00101",
                nombreBeneficiario = "María González (F)",
                timestamp = System.currentTimeMillis() - 600_000L,
                metodo = "QR",
                sincronizado = true,
                latitud = -33.5015,
                longitud = -70.6887,
                distanciaMetros = 35,
                estadoValidacion = "valido",
                deviceId = "ANDROID-SYNCPASS-01"
            ),
            AsistenciaEntity(
                id = "00000000-0000-0000-0004-000000000002",
                tallerId = "00000000-0000-0000-0002-000000000001",
                rut = "BEN-00102",
                nombreBeneficiario = "Carlos Soto (F)",
                timestamp = System.currentTimeMillis() - 300_000L,
                metodo = "QR",
                sincronizado = true,
                latitud = -33.5014,
                longitud = -70.6889,
                distanciaMetros = 24,
                estadoValidacion = "valido",
                deviceId = "ANDROID-SYNCPASS-01"
            ),
            AsistenciaEntity(
                id = "00000000-0000-0000-0004-000000000003",
                tallerId = "00000000-0000-0000-0002-000000000001",
                rut = "BEN-00103",
                nombreBeneficiario = "Rosa Díaz (F)",
                timestamp = System.currentTimeMillis() - 60_000L,
                metodo = "MANUAL",
                sincronizado = false,
                latitud = -33.5016,
                longitud = -70.6886,
                distanciaMetros = 42,
                estadoValidacion = "valido",
                deviceId = "ANDROID-SYNCPASS-01"
            )
        )
    )

    private val encuestasStore = CopyOnWriteArrayList<EncuestaEntity>()

    override fun syncPassDao(): SyncPassDao = this

    override suspend fun getAllTalleres(): List<TallerEntity> = talleresStore.toList()

    override suspend fun upsertTalleres(items: List<TallerEntity>) {
        talleresStore.clear()
        talleresStore.addAll(items)
    }

    override suspend fun upsertTaller(item: TallerEntity) {
        talleresStore.removeIf { it.id == item.id }
        talleresStore.add(0, item)
    }

    override suspend fun updateTaller(item: TallerEntity) {
        upsertTaller(item)
    }

    override suspend fun deleteTaller(item: TallerEntity) {
        deleteTallerById(item.id)
    }

    override suspend fun deleteTallerById(id: String) {
        talleresStore.removeIf { it.id == id }
    }

    override suspend fun getAllAsistencias(): List<AsistenciaEntity> =
        asistenciasStore.sortedByDescending { it.timestamp }

    override suspend fun getPendingAsistencias(): List<AsistenciaEntity> =
        asistenciasStore.filter { !it.sincronizado }

    override suspend fun upsertAsistencia(item: AsistenciaEntity) {
        asistenciasStore.removeIf { it.id == item.id }
        asistenciasStore.add(0, item)
    }

    override suspend fun upsertAsistencias(items: List<AsistenciaEntity>) {
        items.forEach { upsertAsistencia(it) }
    }

    override suspend fun markAsistenciasSynced(ids: List<String>) {
        val idSet = ids.toSet()
        for (i in asistenciasStore.indices) {
            val current = asistenciasStore[i]
            if (current.id in idSet) {
                asistenciasStore[i] = current.copy(sincronizado = true)
            }
        }
    }

    override suspend fun getAllEncuestas(): List<EncuestaEntity> =
        encuestasStore.sortedByDescending { it.timestamp }

    override suspend fun getPendingEncuestas(): List<EncuestaEntity> =
        encuestasStore.filter { !it.sincronizado }

    override suspend fun upsertEncuesta(item: EncuestaEntity) {
        encuestasStore.removeIf { it.id == item.id }
        encuestasStore.add(0, item)
    }

    override suspend fun markEncuestasSynced(ids: List<String>) {
        val idSet = ids.toSet()
        for (i in encuestasStore.indices) {
            val current = encuestasStore[i]
            if (current.id in idSet) {
                encuestasStore[i] = current.copy(sincronizado = true)
            }
        }
    }
}
