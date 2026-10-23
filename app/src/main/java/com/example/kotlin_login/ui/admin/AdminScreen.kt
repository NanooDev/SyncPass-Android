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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.feature.notificaciones.NtfySender
import com.example.kotlin_login.feature.notificaciones.NtfyTopics
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import kotlinx.coroutines.launch

private data class AdminMetric(val label: String, val value: String, val detail: String)
private data class SystemUser(val email: String, val role: String, val status: String)

private val DEMO_METRICS = listOf(
    AdminMetric("Proyectos Activos", "3", "En 3 comunas metropolitanas"),
    AdminMetric("Talleres Realizados", "12", "Semestre en curso"),
    AdminMetric("Asistencias Registradas", "128", "Mediante pases QR"),
    AdminMetric("Tasa de Satisfacción", "94%", "Promedio en encuestas")
)

private val DEMO_USERS = listOf(
    SystemUser("docente.coordinador@duocuc.cl", "Docente / Coordinador", "Activo"),
    SystemUser("voluntario.estudiante@duocuc.cl", "Estudiante Voluntario", "Activo"),
    SystemUser("maria.gonzalez@comunidad.cl", "Beneficiario Comunitario", "Activo"),
    SystemUser("admin.syncpass@duocuc.cl", "Administrador General", "Activo")
)

@Composable
fun AdminScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var isSendingNotification by remember { mutableStateOf(false) }
    var notificationFeedback by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
                Text(
                    text = "Métricas Institucionales de Impacto",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Grid de Métricas
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DEMO_METRICS.chunked(2).forEach { rowMetrics ->
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

            items(DEMO_USERS) { user ->
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
                                text = user.email,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Rol asignado: ${user.role}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(SyncPassEmerald.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                .border(1.dp, SyncPassEmerald.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = user.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = SyncPassEmerald
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
                            text = "Centro de Notificaciones Push (ntfy.sh)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Emisión directa de eventos SSE hacia dispositivos móviles suscritos. Compatible con la arquitectura de microtópicos por usuario.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val adminTopic = NtfyTopics.generate("admin.syncpass@duocuc.cl")
                        Text(
                            text = "Tópico activo: $adminTopic",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                isSendingNotification = true
                                notificationFeedback = null
                                coroutineScope.launch {
                                    val res = NtfySender.sendPushNotification(
                                        email = "admin.syncpass@duocuc.cl",
                                        title = "SyncPass Gobernanza",
                                        message = "Notificación push verificada correctamente vía ntfy.sh"
                                    )
                                    isSendingNotification = false
                                    notificationFeedback = if (res.isSuccess) {
                                        "Notificación enviada al tópico $adminTopic"
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
                            Text("Emitir Notificación Push de Prueba", fontWeight = FontWeight.Bold)
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
