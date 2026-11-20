package com.example.kotlin_login.ui.voluntario

import com.example.kotlin_login.fakes.FakeSyncPassRepository
import com.example.kotlin_login.rules.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas unitarias de VoluntarioViewModel siguiendo el patrón de la clase (20-11):
 * - Uso de MainDispatcherRule (@get:Rule) para simular el hilo principal de Android.
 * - Inyección de FakeSyncPassRepository para aislar exclusivamente el ViewModel.
 * - Verificación de estado inicial, caso de éxito Offline-First y caso de error controlado.
 */
class VoluntarioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeSyncPassRepository
    private lateinit var viewModel: VoluntarioViewModel

    @Before
    fun setup() {
        fakeRepository = FakeSyncPassRepository()
        viewModel = VoluntarioViewModel(fakeRepository)
    }

    @Test
    fun `test 1 - estado inicial carga asistencias desde FakeRepository sin pendientes ni sincronizando`() {
        val state = viewModel.uiState.value
        assertFalse(state.isSyncing)
        assertFalse(state.isOffline)
        assertEquals(0, state.pendingSyncCount)
        assertEquals(1, state.asistencias.size)
        assertEquals("María González", state.asistencias.first().nombreBeneficiario)
    }

    @Test
    fun `test 2 - escanear QR en modo offline guarda registro pendiente y sincronizar lo envia`() {
        viewModel.onToggleOfflineMode(true)
        assertTrue(viewModel.uiState.value.isOffline)

        viewModel.onCodeScanned("SYNCPASS-BENEFICIARIO-12345678-9")
        assertEquals(1, viewModel.uiState.value.pendingSyncCount)
        assertEquals(2, viewModel.uiState.value.asistencias.size)

        viewModel.onToggleOfflineMode(false)
        viewModel.onSyncPendingClick()

        val syncedState = viewModel.uiState.value
        assertFalse(syncedState.isSyncing)
        assertEquals(0, syncedState.pendingSyncCount)
        assertTrue(syncedState.feedbackMessage?.contains("Sincronización exitosa") == true)
    }

    @Test
    fun `test 3 - cuando falla el repositorio al sincronizar el ViewModel controla el error sin caer`() {
        viewModel.onToggleOfflineMode(true)
        viewModel.onCodeScanned("BEN-00102")
        assertEquals(1, viewModel.uiState.value.pendingSyncCount)

        fakeRepository.shouldThrowError = true
        fakeRepository.errorMessage = "Timeout 503 en Supabase"

        viewModel.onSyncPendingClick()

        val errorState = viewModel.uiState.value
        assertFalse(errorState.isSyncing)
        assertNotNull(errorState.feedbackMessage)
        assertTrue(errorState.feedbackMessage?.contains("Error al sincronizar") == true)
        assertTrue(errorState.feedbackMessage?.contains("Timeout 503 en Supabase") == true)
    }
}
