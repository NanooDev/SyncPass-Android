package com.example.kotlin_login.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassCyan
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassIndigo

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
    val username = state.user?.username ?: "marianoacevedo1809@gmail.com"
    val initial = username.firstOrNull()?.uppercase() ?: "U"

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "SyncPass Móvil",
                subtitle = "API EC2: 54.164.69.142",
                currentRole = detectedRole,
                onLogoutClick = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Modern Session Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SyncPassIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = username,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SyncPassEmerald)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Rol: ${detectedRole.displayName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Text(
                text = "MÓDULOS DEL SISTEMA (RBAC)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            // 1. Docente / Coordinador
            ModernRoleNavCard(
                title = "Docente / Coordinador",
                subtitle = "Quórum en vivo y pases QR de taller",
                badgeText = "Planificación",
                accentColor = SyncPassIndigo,
                bgColor = SyncPassIndigo.copy(alpha = 0.12f),
                onClick = onNavigateToDocente
            )

            // 2. Estudiante Voluntario
            ModernRoleNavCard(
                title = "Estudiante Voluntario",
                subtitle = "Acreditación en terreno con modo offline",
                badgeText = "Terreno",
                accentColor = SyncPassEmerald,
                bgColor = SyncPassEmerald.copy(alpha = 0.12f),
                onClick = onNavigateToVoluntario
            )

            // 3. Beneficiario Comunitario
            ModernRoleNavCard(
                title = "Beneficiario Comunitario",
                subtitle = "Credencial digital QR y encuesta de 30s",
                badgeText = "Ciudadano",
                accentColor = SyncPassCyan,
                bgColor = SyncPassCyan.copy(alpha = 0.12f),
                onClick = onNavigateToBeneficiario
            )

            // 4. Administrador General
            ModernRoleNavCard(
                title = "Administrador General",
                subtitle = "Métricas institucionales y auditoría",
                badgeText = "Gobierno",
                accentColor = SyncPassAmber,
                bgColor = SyncPassAmber.copy(alpha = 0.12f),
                onClick = onNavigateToAdmin
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onLogout,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Cerrar Sesión Activa",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ModernRoleNavCard(
    title: String,
    subtitle: String,
    badgeText: String,
    accentColor: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(bgColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
