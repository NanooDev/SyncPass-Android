package com.example.kotlin_login.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.ui.components.SyncPassBottomBar
import com.example.kotlin_login.ui.components.SyncPassCard
import com.example.kotlin_login.ui.components.SyncPassKpiTile
import com.example.kotlin_login.ui.components.SyncPassNavTab
import com.example.kotlin_login.ui.components.SyncPassSettingsActionsCard
import com.example.kotlin_login.ui.components.SyncPassStatusPill
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldDark
import com.example.kotlin_login.ui.theme.ThemeState

@Composable
fun AdminScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val tabs = remember {
        listOf(
            SyncPassNavTab("Resumen", Icons.Rounded.BarChart),
            SyncPassNavTab("Equipo", Icons.Rounded.Group),
            SyncPassNavTab("Avisos", Icons.Rounded.NotificationsActive)
        )
    }

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = when (selectedTab) {
                    1 -> "Equipo"
                    2 -> "Avisos y Ajustes"
                    else -> "Administración"
                },
                onBackClick = onBackClick,
                onLogoutClick = onLogoutClick
            )
        },
        bottomBar = {
            SyncPassBottomBar(
                tabs = tabs,
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (selectedTab) {
            // TAB 1: Métricas 2x2 limpias
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Resumen semanal",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        OutlinedButton(
                            onClick = { viewModel.syncAdminData() },
                            enabled = !uiState.isSyncing,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isSyncing) "Actualizando..." else "Actualizar",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    uiState.metrics.chunked(2).forEach { rowMetrics ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowMetrics.forEach { metric ->
                                SyncPassKpiTile(
                                    value = metric.value,
                                    label = metric.label,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // TAB 2: Lista limpia de equipo (sin textos diminutos amontonados)
            1 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.usersList, key = { it.id }) { user ->
                        val displayName = user.nombre.replace(" (Ficticio)", "").replace(" (F)", "")
                        val statusColor = if (user.activo) {
                            if (ThemeState.isDarkTheme) SyncPassEmerald else SyncPassEmeraldDark
                        } else {
                            MaterialTheme.colorScheme.error
                        }

                        SyncPassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = displayName.firstOrNull()?.uppercase() ?: "U",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = displayName,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = user.role,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                SyncPassStatusPill(
                                    text = if (user.activo) "Activo" else "Inactivo",
                                    accentColor = statusColor
                                )
                            }
                        }
                    }
                }
            }

            // TAB 3: Notificaciones y Ajustes
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SyncPassCard {
                        Text(
                            text = "Avisos al equipo",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Envía una notificación instantánea a los voluntarios en terreno.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.onSendPushNotification() },
                            enabled = !uiState.isSendingNotification,
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(vertical = 14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isSendingNotification) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = "Enviar aviso push",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        uiState.notificationFeedback?.let { feedback ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = feedback,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (ThemeState.isDarkTheme) SyncPassEmerald else SyncPassEmeraldDark
                            )
                        }
                    }

                    SyncPassSettingsActionsCard(onLogoutClick = onLogoutClick)
                }
            }
        }
    }
}
