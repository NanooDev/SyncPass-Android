package com.example.kotlin_login.fakes

import com.example.kotlin_login.data.datasource.SyncPassLocalDataSource
import com.example.kotlin_login.data.local.AsistenciaEntity
import com.example.kotlin_login.data.local.EncuestaEntity
import com.example.kotlin_login.data.local.TallerEntity

/**
 * Fake de SyncPassLocalDataSource para probar SyncPassRepositoryImpl de forma aislada
 * sin depender de SQLite / Room en disco.
 */
class FakeSyncPassLocalDataSource : SyncPassLocalDataSource {

    val talleres = mutableListOf<TallerEntity>()
    val asistencias = mutableListOf<AsistenciaEntity>()
    val encuestas = mutableListOf<EncuestaEntity>()

    override suspend fun getTalleres(): List<TallerEntity> = talleres.toList()

    override suspend fun saveTalleres(items: List<TallerEntity>) {
        talleres.clear()
        talleres.addAll(items)
    }

    override suspend fun saveTaller(item: TallerEntity) {
        talleres.removeIf { it.id == item.id }
        talleres.add(0, item)
    }

    override suspend fun deleteTaller(id: String) {
        talleres.removeIf { it.id == id }
    }

    override suspend fun getAsistencias(): List<AsistenciaEntity> = asistencias.toList()

    override suspend fun getPendingAsistencias(): List<AsistenciaEntity> =
        asistencias.filter { !it.sincronizado }

    override suspend fun saveAsistencia(item: AsistenciaEntity) {
        asistencias.removeIf { it.id == item.id }
        asistencias.add(0, item)
    }

    override suspend fun saveAsistencias(items: List<AsistenciaEntity>) {
        items.forEach { saveAsistencia(it) }
    }

    override suspend fun markAsistenciasSynced(ids: List<String>) {
        val set = ids.toSet()
        for (i in asistencias.indices) {
            if (asistencias[i].id in set) {
                asistencias[i] = asistencias[i].copy(sincronizado = true)
            }
        }
    }

    override suspend fun getEncuestas(): List<EncuestaEntity> = encuestas.toList()

    override suspend fun getPendingEncuestas(): List<EncuestaEntity> =
        encuestas.filter { !it.sincronizado }

    override suspend fun saveEncuesta(item: EncuestaEntity) {
        encuestas.removeIf { it.id == item.id }
        encuestas.add(0, item)
    }

    override suspend fun markEncuestasSynced(ids: List<String>) {
        val set = ids.toSet()
        for (i in encuestas.indices) {
            if (encuestas[i].id in set) {
                encuestas[i] = encuestas[i].copy(sincronizado = true)
            }
        }
    }
}
