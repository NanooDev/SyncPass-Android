package com.example.kotlin_login.ui.voluntario

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.ui.components.QrScannerView
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VoluntarioScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: VoluntarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "Acreditación en Terreno",
                subtitle = "Estudiante Voluntario · Modo Operativo",
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
            // Panel de Control de Conectividad y Sincronización Offline-First
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isOffline) SyncPassAmber.copy(alpha = 0.12f) else SyncPassEmerald.copy(alpha = 0.12f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (uiState.isOffline) SyncPassAmber else SyncPassEmerald
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (uiState.isOffline) "Modo Sin Conexión (Offline-First)" else "Conexión a la Nube Activa",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (uiState.isOffline) SyncPassAmber else SyncPassEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (uiState.pendingSyncCount > 0)
                                        "${uiState.pendingSyncCount} registro(s) pendientes en Room/SQLite local"
                                    else
                                        "Todos los registros locales sincronizados con Supabase",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = uiState.isOffline,
                                onCheckedChange = { viewModel.onToggleOfflineMode(it) }
                            )
                        }

                        if (uiState.pendingSyncCount > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.onSyncPendingClick() },
                                enabled = !uiState.isSyncing,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = SyncPassAmber)
                            ) {
                                Text(
                                    if (uiState.isSyncing)
                                        "Sincronizando con Supabase..."
                                    else
                                        "Sincronizar ${uiState.pendingSyncCount} Registro(s) Pendientes"
                                )
                            }
                        }
                    }
                }
            }

            // Panel de Hardware Nativo: Geolocalización GPS & Geocerca (API Nativa #1) y Cámara (API Nativa #2)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Geocerca GPS & Sensor Óptico",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Coord: ${uiState.latitudActual}, ${uiState.longitudActual} · Dist: ${uiState.distanciaRecintoMetros}m / 150m",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.onRefreshHardwareSensors(context) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Validar GPS")
                            }
                        }
                    }
                }
            }

            if (uiState.feedbackMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SyncPassEmerald.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .border(1.dp, SyncPassEmerald, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = uiState.feedbackMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = SyncPassEmerald
                        )
                    }
                }
            }

            // Escáner QR y Registro
            item {
                QrScannerView(
                    onCodeScanned = { scannedCode ->
                        viewModel.onCodeScanned(scannedCode)
                    }
                )
            }

            // Lista de Asistencias del Día
            item {
                Text(
                    text = "Acreditaciones en Dispositivo (${uiState.asistencias.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(uiState.asistencias, key = { it.id }) { item ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.nombreBeneficiario,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ID: ${item.rut} · Método: ${item.metodo} · GPS: ${item.distanciaMetros}m",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(item.timestamp))
                            Text(
                                text = "Hora: $timeStr · Validación: ${item.estadoValidacion}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        val isSync = item.sincronizado
                        val badgeColor = if (isSync) SyncPassEmerald else SyncPassAmber
                        Box(
                            modifier = Modifier
                                .background(
                                    badgeColor.copy(alpha = 0.12f),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(1.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isSync) "Sincronizado" else "Pendiente",
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeColor
                            )
                        }
                    }
                }
            }
        }
    }
}
