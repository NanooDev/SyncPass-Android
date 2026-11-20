package com.example.kotlin_login.ui.docente

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
 * Pruebas unitarias de DocenteViewModel usando MainDispatcherRule y FakeSyncPassRepository.
 */
class DocenteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeSyncPassRepository
    private lateinit var viewModel: DocenteViewModel

    @Before
    fun setup() {
        fakeRepository = FakeSyncPassRepository()
        viewModel = DocenteViewModel(fakeRepository)
    }

    @Test
    fun `test 4 - crear taller actualiza lista de UIState y cierra dialogo mediante FakeRepository`() {
        viewModel.onOpenCreateDialog()
        assertTrue(viewModel.uiState.value.showCreateDialog)

        viewModel.onNewNombreChange("Taller Alfabetización Lo Espejo")
        viewModel.onNewLugarChange("Sede Vecinal N°14")
        viewModel.onNewFechaChange("Viernes 19:00 hrs")
        viewModel.onNewCuposChange("35")
        viewModel.onCreateTaller()

        val state = viewModel.uiState.value
        assertFalse(state.showCreateDialog)
        assertFalse(state.isSyncing)
        assertEquals(2, state.talleres.size)
        assertEquals("Taller Alfabetización Lo Espejo", state.talleres.first().nombre)
        assertNotNull(state.selectedTaller)
    }

    @Test
    fun `test 5 - cuando falla la sincronizacion de talleres UIState informa error controlado y detiene carga`() {
        fakeRepository.shouldThrowError = true
        fakeRepository.errorMessage = "Error de red simulado"

        viewModel.syncTalleres(showFeedback = true)

        val state = viewModel.uiState.value
        assertFalse(state.isSyncing)
        assertNotNull(state.statusMessage)
        assertTrue(state.statusMessage?.contains("Error al cargar talleres") == true)
        assertTrue(state.statusMessage?.contains("Error de red simulado") == true)
    }
}
