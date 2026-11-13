package com.example.kotlin_login.ui.voluntario

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_login.core.hardware.LocationHardwareHelper
import com.example.kotlin_login.core.hardware.QrCameraHardwareHelper
import com.example.kotlin_login.di.RepositoryModule
import com.example.kotlin_login.domain.repository.SyncPassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de Acreditación en Terreno (Estudiante Voluntario).
 * Gestiona la lógica de negocio Offline-First, la validación de geocerca GPS (Haversine),
 * el escaneo óptico QR y la sincronización por lotes hacia Supabase.
 */
class VoluntarioViewModel(
    private val repository: SyncPassRepository = RepositoryModule.provideSyncPassRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoluntarioUiState())
    val uiState: StateFlow<VoluntarioUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            val list = repository.getAsistencias(forceRemote = false)
            val pending = repository.getPendingAsistenciasCount()
            _uiState.update {
                it.copy(
                    asistencias = list,
                    pendingSyncCount = pending
                )
            }
        }
    }

    fun onToggleOfflineMode(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isOffline = enabled,
                feedbackMessage = if (enabled) {
                    "Modo Offline activado: las acreditaciones se guardarán en Room/SQLite local."
                } else {
                    "Conexión restaurada: listo para sincronizar pendientes con Supabase."
                }
            )
        }
    }

    fun onRefreshHardwareSensors(context: Context?) {
        val gps = LocationHardwareHelper.obtenerUbicacionDispositivo(context)
        val camaraOk = QrCameraHardwareHelper.detectarCamaraTraseraDisponible(context)
        _uiState.update {
            it.copy(
                latitudActual = gps.latitud,
                longitudActual = gps.longitud,
                distanciaRecintoMetros = gps.distanciaMetros,
                dentroDeGeocerca = gps.dentroDeGeocerca,
                camaraTraseraActiva = camaraOk,
                feedbackMessage = "GPS validado a ${gps.distanciaMetros}m del recinto (${if (gps.dentroDeGeocerca) "Dentro de geocerca" else "Fuera de geocerca"})."
            )
        }
    }

    fun onSimulateCoordinateChange(latitud: Double, longitud: Double) {
        val gps = LocationHardwareHelper.evaluarGeocerca(latitud, longitud)
        _uiState.update {
            it.copy(
                latitudActual = gps.latitud,
                longitudActual = gps.longitud,
                distanciaRecintoMetros = gps.distanciaMetros,
                dentroDeGeocerca = gps.dentroDeGeocerca
            )
        }
    }

    fun onCodeScanned(rawCode: String) {
        val state = _uiState.value
        val parsed = QrCameraHardwareHelper.procesarCodigoEscaneado(rawCode)
        val gps = LocationHardwareHelper.evaluarGeocerca(state.latitudActual, state.longitudActual)

        viewModelScope.launch {
            repository.registrarAsistencia(
                tallerId = "00000000-0000-0000-0002-000000000001",
                rutOrCode = parsed.codigoNormalizado,
                nombreBeneficiario = parsed.nombreSugerido,
                metodo = parsed.metodo,
                latitud = gps.latitud,
                longitud = gps.longitud,
                distanciaMetros = gps.distanciaMetros,
                estadoValidacion = gps.estadoValidacion,
                isOffline = state.isOffline
            )
            val updatedList = repository.getAsistencias(forceRemote = false)
            val pending = repository.getPendingAsistenciasCount()
            _uiState.update {
                it.copy(
                    asistencias = updatedList,
                    pendingSyncCount = pending,
                    feedbackMessage = if (state.isOffline) {
                        "Asistencia guardada en base de datos local Room (Pendiente de sincronización)."
                    } else {
                        "Asistencia acreditada (${gps.distanciaMetros}m del recinto) y sincronizada con Supabase."
                    }
                )
            }
        }
    }

    fun onSyncPendingClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, feedbackMessage = null) }
            val syncedCount = repository.syncPendingAsistencias()
            val updatedList = repository.getAsistencias(forceRemote = false)
            val remainingPending = repository.getPendingAsistenciasCount()
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    asistencias = updatedList,
                    pendingSyncCount = remainingPending,
                    feedbackMessage = "Sincronización exitosa: $syncedCount registro(s) enviados a PostgreSQL (Supabase)."
                )
            }
        }
    }
}
