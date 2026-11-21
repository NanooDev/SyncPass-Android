package com.example.kotlin_login.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.components.SyncPassCard
import com.example.kotlin_login.ui.components.SyncPassSettingsActionsCard
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
    val username = state.user?.username ?: "usuario@duocuc.cl"
    val initial = username.firstOrNull()?.uppercase() ?: "U"

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "SyncPass",
                onLogoutClick = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SyncPassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = username,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = detectedRole.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            MinimalRoleNavRow(
                title = "Docente",
                subtitle = "Talleres y acta CSV",
                icon = Icons.AutoMirrored.Rounded.EventNote,
                accentColor = SyncPassIndigo,
                onClick = onNavigateToDocente
            )

            MinimalRoleNavRow(
                title = "Voluntario",
                subtitle = "Escáner QR y modo offline",
                icon = Icons.Rounded.QrCodeScanner,
                accentColor = SyncPassEmerald,
                onClick = onNavigateToVoluntario
            )

            MinimalRoleNavRow(
                title = "Beneficiario",
                subtitle = "Pase QR y encuesta",
                icon = Icons.Rounded.QrCode2,
                accentColor = SyncPassCyan,
                onClick = onNavigateToBeneficiario
            )

            MinimalRoleNavRow(
                title = "Administrador",
                subtitle = "Resumen y avisos",
                icon = Icons.Rounded.AdminPanelSettings,
                accentColor = SyncPassAmber,
                onClick = onNavigateToAdmin
            )

            SyncPassSettingsActionsCard(onLogoutClick = onLogout)
        }
    }
}

@Composable
private fun MinimalRoleNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    SyncPassCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
