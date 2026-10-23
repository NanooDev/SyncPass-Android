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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_login.domain.model.AsistenciaRegistro
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.components.QrScannerView
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassAmberLight
import com.example.kotlin_login.ui.theme.SyncPassBluePrimary
import com.example.kotlin_login.ui.theme.SyncPassBorder
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldLight
import com.example.kotlin_login.ui.theme.SyncPassTextPrimary
import com.example.kotlin_login.ui.theme.SyncPassTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VoluntarioScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var isOffline by remember { mutableStateOf(false) }
    var pendingSyncCount by remember { mutableIntStateOf(3) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val asistencias = remember {
        mutableStateListOf(
            AsistenciaRegistro("1", "T-101", "12.345.678-9", "María González", System.currentTimeMillis() - 600000, "QR", true),
            AsistenciaRegistro("2", "T-101", "14.567.890-K", "Juan Pérez Morales", System.currentTimeMillis() - 300000, "QR", true),
            AsistenciaRegistro("3", "T-101", "10.987.654-3", "Rosa Morales Silva", System.currentTimeMillis() - 60000, "MANUAL", false)
        )
    }

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "Acreditación en Terreno",
                subtitle = "Estudiante Voluntario • Modo Operativo",
                currentRole = UserRole.VOLUNTARIO,
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
            // Panel de Control de Conectividad y Sincronización
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOffline) SyncPassAmberLight else SyncPassEmeraldLight
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isOffline) SyncPassAmber else SyncPassEmerald
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isOffline) "Modo Sin Conexión (Offline)" else "Conexión a la Nube Activa",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isOffline) SyncPassAmber else SyncPassEmerald
                                )
                                Text(
                                    text = if (isOffline)
                                        "$pendingSyncCount registros almacenados localmente"
                                    else
                                        "Sincronización en tiempo real habilitada",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SyncPassTextPrimary
                                )
                            }
                            Switch(
                                checked = isOffline,
                                onCheckedChange = { isOffline = it }
                            )
                        }

                        if (isOffline && pendingSyncCount > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    pendingSyncCount = 0
                                    feedbackMessage = "Todos los registros locales fueron sincronizados con la nube."
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = SyncPassAmber)
                            ) {
                                Text("Sincronizar $pendingSyncCount Registros Pendientes")
                            }
                        }
                    }
                }
            }

            if (feedbackMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SyncPassEmeraldLight, RoundedCornerShape(8.dp))
                            .border(1.dp, SyncPassEmerald, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = feedbackMessage ?: "",
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
                        val nuevo = AsistenciaRegistro(
                            id = (asistencias.size + 1).toString(),
                            tallerId = "T-101",
                            rut = scannedCode,
                            nombreBeneficiario = "Beneficiario Acreditado",
                            timestamp = System.currentTimeMillis(),
                            metodo = if (scannedCode.startsWith("SYNCPASS")) "QR" else "MANUAL",
                            sincronizado = !isOffline
                        )
                        asistencias.add(0, nuevo)
                        if (isOffline) {
                            pendingSyncCount++
                            feedbackMessage = "Asistencia guardada en almacenamiento local (sin red)."
                        } else {
                            feedbackMessage = "Asistencia registrada y validada en el servidor central."
                        }
                    }
                )
            }

            // Lista de Asistencias del Día
            item {
                Text(
                    text = "Acreditaciones Recientes (${asistencias.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = SyncPassBluePrimary
                )
            }

            items(asistencias) { item ->
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
                                text = item.nombreBeneficiario,
                                style = MaterialTheme.typography.titleSmall,
                                color = SyncPassTextPrimary
                            )
                            Text(
                                text = "RUT: ${item.rut} • Método: ${item.metodo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SyncPassTextSecondary
                            )
                            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(item.timestamp))
                            Text(
                                text = "Hora: $timeStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = SyncPassTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (item.sincronizado) SyncPassEmeraldLight else SyncPassAmberLight,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (item.sincronizado) "Sincronizado" else "Pendiente",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (item.sincronizado) SyncPassEmerald else SyncPassAmber
                            )
                        }
                    }
                }
            }
        }
    }
}
