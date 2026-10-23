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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassBlueLight
import com.example.kotlin_login.ui.theme.SyncPassBluePrimary
import com.example.kotlin_login.ui.theme.SyncPassBorder
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldLight
import com.example.kotlin_login.ui.theme.SyncPassTextPrimary
import com.example.kotlin_login.ui.theme.SyncPassTextSecondary

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
    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "Administración General",
                subtitle = "Gobernanza Territorial y Control RBAC",
                currentRole = UserRole.ADMIN,
                onBackClick = onBackClick,
                onLogoutClick = onLogoutClick
            )
        }
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
                    color = SyncPassBluePrimary
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
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SyncPassBorder)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = metric.value,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = SyncPassBluePrimary
                                        )
                                        Text(
                                            text = metric.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = SyncPassTextPrimary
                                        )
                                        Text(
                                            text = metric.detail,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SyncPassTextSecondary
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
                    color = SyncPassBluePrimary
                )
            }

            items(DEMO_USERS) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SyncPassBorder)
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
                                color = SyncPassTextPrimary
                            )
                            Text(
                                text = "Rol asignado: ${user.role}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SyncPassTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(SyncPassEmeraldLight, RoundedCornerShape(6.dp))
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
        }
    }
}
