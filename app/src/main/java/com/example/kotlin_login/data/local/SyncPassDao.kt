package com.example.kotlin_login.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) con anotaciones Room para el almacenamiento local SQLite.
 * Única capa que define consultas locales sobre las tablas del dispositivo.
 */
@Dao
interface SyncPassDao {

    @Query("SELECT * FROM evento_taller_local")
    suspend fun getAllTalleres(): List<TallerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTalleres(items: List<TallerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTaller(item: TallerEntity)

    @Query("DELETE FROM evento_taller_local WHERE id = :id")
    suspend fun deleteTallerById(id: String)

    @Query("SELECT * FROM registro_asistencia_local ORDER BY timestamp DESC")
    suspend fun getAllAsistencias(): List<AsistenciaEntity>

    @Query("SELECT * FROM registro_asistencia_local WHERE sincronizado = 0")
    suspend fun getPendingAsistencias(): List<AsistenciaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAsistencia(item: AsistenciaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAsistencias(items: List<AsistenciaEntity>)

    @Query("UPDATE registro_asistencia_local SET sincronizado = 1 WHERE id IN (:ids)")
    suspend fun markAsistenciasSynced(ids: List<String>)

    @Query("SELECT * FROM encuesta_valoracion_local ORDER BY timestamp DESC")
    suspend fun getAllEncuestas(): List<EncuestaEntity>

    @Query("SELECT * FROM encuesta_valoracion_local WHERE sincronizado = 0")
    suspend fun getPendingEncuestas(): List<EncuestaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEncuesta(item: EncuestaEntity)

    @Query("UPDATE encuesta_valoracion_local SET sincronizado = 1 WHERE id IN (:ids)")
    suspend fun markEncuestasSynced(ids: List<String>)
}
