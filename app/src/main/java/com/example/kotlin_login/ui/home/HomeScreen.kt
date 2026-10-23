package com.example.kotlin_login.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassBlueLight
import com.example.kotlin_login.ui.theme.SyncPassBluePrimary
import com.example.kotlin_login.ui.theme.SyncPassBlueSecondary
import com.example.kotlin_login.ui.theme.SyncPassBorder
import com.example.kotlin_login.ui.theme.SyncPassTextPrimary
import com.example.kotlin_login.ui.theme.SyncPassTextSecondary

@Composable
fun HomeScreen(
    accessToken: String,
    onNavigateToDocente: () -> Unit,
    onNavigateToVoluntario: () -> Unit,
    onNavigateToBeneficiario: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(accessToken)
    )
    val state by viewModel.uiState.collectAsState()

    val detectedRole = state.user?.roles?.firstOrNull()?.let { UserRole.fromSlug(it) } ?: UserRole.DOCENTE

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "Portal Principal SyncPass",
                subtitle = "Duoc UC • Vinculación con el Medio",
                currentRole = detectedRole,
                onLogoutClick = onLogout
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Identidad de Sesión
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SyncPassBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sesión Activa",
                        style = MaterialTheme.typography.labelMedium,
                        color = SyncPassTextSecondary
                    )
                    Text(
                        text = state.user?.username ?: "usuario.institucional@duocuc.cl",
                        style = MaterialTheme.typography.titleMedium,
                        color = SyncPassTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Perfil activo: ${detectedRole.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SyncPassBluePrimary
                    )
                }
            }

            Text(
                text = "Módulos y Pantallas por Rol",
                style = MaterialTheme.typography.titleSmall,
                color = SyncPassBluePrimary
            )

            // 1. Módulo Docente / Profesor
            RoleNavigationCard(
                title = "1. Módulo Docente / Coordinador",
                description = "Planificación de talleres comunitarios, verificación de quórum en tiempo real y generación de códigos QR de asistencia.",
                badgeText = "Perfil Profesor",
                onClick = onNavigateToDocente
            )

            // 2. Módulo Estudiante Voluntario
            RoleNavigationCard(
                title = "2. Módulo Estudiante Voluntario",
                description = "Acreditación en terreno con escáner de pases QR, modo sin conexión (Offline) y sincronización con la nube.",
                badgeText = "Perfil Terreno",
                onClick = onNavigateToVoluntario
            )

            // 3. Módulo Beneficiario Comunitario
            RoleNavigationCard(
                title = "3. Módulo Beneficiario",
                description = "Pase digital QR personal para acceso a talleres y formulario de encuesta de satisfacción ciudadana de 30 segundos.",
                badgeText = "Perfil Ciudadano",
                onClick = onNavigateToBeneficiario
            )

            // 4. Módulo Administrador General
            RoleNavigationCard(
                title = "4. Módulo Administrador General",
                description = "Auditoría de quórum territorial, métricas de vinculación con el medio y asignación de roles de usuario (RBAC).",
                badgeText = "Perfil Gobierno",
                onClick = onNavigateToAdmin
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun RoleNavigationCard(
    title: String,
    description: String,
    badgeText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SyncPassBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = SyncPassTextPrimary
                )
                Box(
                    modifier = Modifier
                        .background(SyncPassBlueLight, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = SyncPassBluePrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = SyncPassTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Abrir pantalla →",
                    style = MaterialTheme.typography.labelMedium,
                    color = SyncPassBlueSecondary
                )
            }
        }
    }
}
