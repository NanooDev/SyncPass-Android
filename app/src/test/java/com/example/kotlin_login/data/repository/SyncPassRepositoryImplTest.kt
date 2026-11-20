package com.example.kotlin_login.data.repository

import com.example.kotlin_login.data.local.TallerEntity
import com.example.kotlin_login.fakes.FakeSyncPassLocalDataSource
import com.example.kotlin_login.fakes.FakeSyncPassRemoteDataSource
import com.example.kotlin_login.rules.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas unitarias de SyncPassRepositoryImpl inyectando fakes de ambas fuentes de datos:
 * FakeSyncPassLocalDataSource (simulando Room) y FakeSyncPassRemoteDataSource (simulando Retrofit).
 */
class SyncPassRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeLocalDataSource: FakeSyncPassLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeSyncPassRemoteDataSource
    private lateinit var repository: SyncPassRepositoryImpl

    @Before
    fun setup() {
        fakeLocalDataSource = FakeSyncPassLocalDataSource()
        fakeRemoteDataSource = FakeSyncPassRemoteDataSource()
        repository = SyncPassRepositoryImpl(
            localDataSource = fakeLocalDataSource,
            remoteDataSource = fakeRemoteDataSource
        )
    }

    @Test
    fun `test 6 - getTalleres transforma DTO remoto en entidades locales y modelos de dominio`() = runTest {
        val result = repository.getTalleres(forceRemote = true)

        assertEquals(1, result.size)
        assertEquals("REM-T-01", result.first().id)
        assertEquals("Taller Remoto Supabase", result.first().nombre)
        assertEquals(1, fakeLocalDataSource.talleres.size)
        assertTrue(result.first().sincronizado)
    }

    @Test
    fun `test 7 - cuando falla RemoteDataSource en modo offline el repositorio retorna cache local sin perder datos`() = runTest {
        fakeLocalDataSource.saveTaller(
            TallerEntity(
                id = "LOC-99",
                proyectoId = "P-1",
                nombre = "Taller Guardado en Room",
                fechaHora = "Hoy 20:00",
                lugar = "Sede Villa El Roble",
                cuposMaximos = 30,
                cuposOcupados = 10,
                codigoQr = "EVT-LOC-99",
                estado = "En curso",
                sincronizado = false
            )
        )
        fakeRemoteDataSource.shouldFail = true

        val result = repository.getTalleres(forceRemote = true)

        assertEquals(1, result.size)
        assertEquals("LOC-99", result.first().id)
        assertEquals("Taller Guardado en Room", result.first().nombre)
        assertFalse(result.first().sincronizado)
    }
}
