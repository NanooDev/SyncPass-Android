package com.example.kotlin_login.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_login.di.RepositoryModule
import com.example.kotlin_login.domain.repository.SyncPassRepository
import com.example.kotlin_login.feature.notificaciones.NtfySender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de Administración General y Control RBAC.
 */
class AdminViewModel(
    private val repository: SyncPassRepository = RepositoryModule.provideSyncPassRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        syncAdminData()
    }

    fun syncAdminData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val remoteUsers = repository.getUsuarios()
            val remoteTalleres = repository.getTalleres(forceRemote = true)
            val asistencias = repository.getAsistencias(forceRemote = false)
            _uiState.update { current ->
                current.copy(
                    isSyncing = false,
                    usersList = remoteUsers.ifEmpty { current.usersList },
                    talleresActivosCount = if (remoteTalleres.isNotEmpty()) remoteTalleres.size else current.talleresActivosCount,
                    asistenciasRegistradasCount = 125 + asistencias.size
                )
            }
        }
    }

    fun onSendPushNotification() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingNotification = true, notificationFeedback = null) }
            val res = NtfySender.sendPushNotification(
                email = "admin.syncpass@duocuc.cl",
                title = "SyncPass Gobernanza",
                message = "Notificación institucional verificada en terreno"
            )
            _uiState.update {
                it.copy(
                    isSendingNotification = false,
                    notificationFeedback = if (res.isSuccess) {
                        "Notificación enviada exitosamente a los equipos en terreno"
                    } else {
                        "Error al enviar: ${res.exceptionOrNull()?.message}"
                    }
                )
            }
        }
    }
}
