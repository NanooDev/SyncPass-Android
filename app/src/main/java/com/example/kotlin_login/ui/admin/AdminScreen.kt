package com.example.kotlin_login.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kotlin_login.data.repository.RemoteUserItem
import com.example.kotlin_login.data.repository.TallerRemoteRepository
import com.example.kotlin_login.feature.notificaciones.NtfySender
import com.example.kotlin_login.feature.notificaciones.NtfyTopics
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import kotlinx.coroutines.launch

private data class AdminMetric(val label: String, val value: String, val detail: String)

@Composable
fun AdminScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var isSendingNotification by remember { mutableStateOf(false) }
    var notificationFeedback by remember { mutableStateOf<String?>(null) }
    var isSyncing by remember { mutableStateOf(false) }
    var talleresActivosCount by remember { mutableIntStateOf(3) }
    var usersList by remember {
        mutableStateOf(
            listOf(
                RemoteUserItem("usr-1", "Administrador SyncPass", "admin.syncpass@duocuc.cl", "Administrador General", true),
                RemoteUserItem("usr-2", "Prof. Mariano Acevedo", "docente.coordinador@duocuc.cl", "Docente / Coordinador", true),
                RemoteUserItem("usr-3", "Álvaro León", "voluntario.estudiante@duocuc.cl", "Estudiante Voluntario", true),
                RemoteUserItem("usr-4", "María González", "maria.gonzalez@comunidad.cl", "Beneficiario Comunitario", true)
            )
        )
    }
    val coroutineScope = rememberCoroutineScope()

    fun syncAdminData() {
        coroutineScope.launch {
            isSyncing = true
            val remoteUsers = TallerRemoteRepository.fetchUsuarios()
            if (remoteUsers.isNotEmpty()) {
                usersList = remoteUsers
            }
            val remoteTalleres = TallerRemoteRepository.fetchTalleres()
            if (remoteTalleres.isNotEmpty()) {
                talleresActivosCount = remoteTalleres.size
            }
            isSyncing = false
        }
    }

    LaunchedEffect(Unit) {
        syncAdminData()
    }

    val metrics = listOf(
        AdminMetric("Talleres en Base de Datos", "$talleresActivosCount", "Sincronizados vía /api/talleres"),
        AdminMetric("Usuarios Registrados", "${usersList.size}", "Sincronizados vía /api/usuarios"),
        AdminMetric("Asistencias Registradas", "128", "Mediante pases QR"),
        AdminMetric("Tasa de Satisfacción", "94%", "Promedio en encuestas")
    )

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "Administración General",
                subtitle = "Gobernanza Territorial y Control RBAC",
                onBackClick = onBackClick,
                onLogoutClick = onLogoutClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Métricas Institucionales de Impacto",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedButton(
                        onClick = { syncAdminData() },
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isSyncing) "Sincronizando..." else "Actualizar")
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    metrics.chunked(2).forEach { rowMetrics ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowMetrics.forEach { metric ->
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = metric.value,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = metric.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = metric.detail,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gestión de Accesos y Roles de Usuario (RBAC)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(usersList, key = { it.id }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = user.nombre,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${user.email} · ${user.role}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        val statusColor = if (user.activo) SyncPassEmerald else MaterialTheme.colorScheme.error
                        Box(
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (user.activo) "Activo" else "De baja",
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Centro de Notificaciones Institucionales",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Envío de avisos en tiempo real hacia los dispositivos móviles de los equipos en terreno.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val adminTopic = NtfyTopics.generate("admin.syncpass@duocuc.cl")

                        Button(
                            onClick = {
                                isSendingNotification = true
                                notificationFeedback = null
                                coroutineScope.launch {
                                    val res = NtfySender.sendPushNotification(
                                        email = "admin.syncpass@duocuc.cl",
                                        title = "SyncPass Gobernanza",
                                        message = "Notificación institucional verificada en terreno"
                                    )
                                    isSendingNotification = false
                                    notificationFeedback = if (res.isSuccess) {
                                        "Notificación enviada exitosamente a los equipos en terreno"
                                    } else {
                                        "Error al enviar: ${res.exceptionOrNull()?.message}"
                                    }
                                }
                            },
                            enabled = !isSendingNotification,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isSendingNotification) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                            }
                            Text("Emitir Notificación Push", fontWeight = FontWeight.Bold)
                        }

                        notificationFeedback?.let { feedback ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = feedback,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (feedback.startsWith("Error")) MaterialTheme.colorScheme.error else SyncPassEmerald
                            )
                        }
                    }
                }
            }
        }
    }
}
