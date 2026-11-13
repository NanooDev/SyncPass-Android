package com.example.kotlin_login.data.datasource

import com.example.kotlin_login.data.local.AsistenciaEntity
import com.example.kotlin_login.data.local.EncuestaEntity
import com.example.kotlin_login.data.local.SyncPassDao
import com.example.kotlin_login.data.local.TallerEntity

/**
 * Fuente de datos local (Local DataSource).
 * Abstrae el acceso al DAO de Room/SQLite para que el repositorio no dependa de la tecnología local.
 */
interface SyncPassLocalDataSource {
    suspend fun getTalleres(): List<TallerEntity>
    suspend fun saveTalleres(items: List<TallerEntity>)
    suspend fun saveTaller(item: TallerEntity)
    suspend fun deleteTaller(id: String)

    suspend fun getAsistencias(): List<AsistenciaEntity>
    suspend fun getPendingAsistencias(): List<AsistenciaEntity>
    suspend fun saveAsistencia(item: AsistenciaEntity)
    suspend fun saveAsistencias(items: List<AsistenciaEntity>)
    suspend fun markAsistenciasSynced(ids: List<String>)

    suspend fun getEncuestas(): List<EncuestaEntity>
    suspend fun getPendingEncuestas(): List<EncuestaEntity>
    suspend fun saveEncuesta(item: EncuestaEntity)
    suspend fun markEncuestasSynced(ids: List<String>)
}

class SyncPassLocalDataSourceImpl(
    private val dao: SyncPassDao
) : SyncPassLocalDataSource {

    override suspend fun getTalleres(): List<TallerEntity> = dao.getAllTalleres()

    override suspend fun saveTalleres(items: List<TallerEntity>) = dao.upsertTalleres(items)

    override suspend fun saveTaller(item: TallerEntity) = dao.upsertTaller(item)

    override suspend fun deleteTaller(id: String) = dao.deleteTallerById(id)

    override suspend fun getAsistencias(): List<AsistenciaEntity> = dao.getAllAsistencias()

    override suspend fun getPendingAsistencias(): List<AsistenciaEntity> = dao.getPendingAsistencias()

    override suspend fun saveAsistencia(item: AsistenciaEntity) = dao.upsertAsistencia(item)

    override suspend fun saveAsistencias(items: List<AsistenciaEntity>) = dao.upsertAsistencias(items)

    override suspend fun markAsistenciasSynced(ids: List<String>) = dao.markAsistenciasSynced(ids)

    override suspend fun getEncuestas(): List<EncuestaEntity> = dao.getAllEncuestas()

    override suspend fun getPendingEncuestas(): List<EncuestaEntity> = dao.getPendingEncuestas()

    override suspend fun saveEncuesta(item: EncuestaEntity) = dao.upsertEncuesta(item)

    override suspend fun markEncuestasSynced(ids: List<String>) = dao.markEncuestasSynced(ids)
}
