package com.example.kotlin_login.ui.docente

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kotlin_login.data.repository.TallerRemoteRepository
import com.example.kotlin_login.domain.model.TallerEvento
import com.example.kotlin_login.ui.components.QrCodeView
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import kotlinx.coroutines.launch

@Composable
fun DocenteScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var talleres by remember { mutableStateOf(TallerRemoteRepository.initialTalleres) }
    var selectedTaller by remember { mutableStateOf(talleres.first()) }
    var isSyncing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Estados del modal de creación de taller (Mantenedor Móvil <-> Web)
    var showCreateDialog by remember { mutableStateOf(false) }
    var newNombre by remember { mutableStateOf("") }
    var newLugar by remember { mutableStateOf("") }
    var newFecha by remember { mutableStateOf("Hoy, 16:00 hrs") }
    var newCupos by remember { mutableStateOf("30") }

    fun syncTalleres(showFeedback: Boolean = false) {
        coroutineScope.launch {
            isSyncing = true
            val remote = TallerRemoteRepository.fetchTalleres()
            if (remote.isNotEmpty()) {
                talleres = remote
                selectedTaller = remote.find { it.id == selectedTaller.id } ?: remote.first()
                if (showFeedback) {
                    statusMessage = "Talleres sincronizados en tiempo real con la Web (/api/talleres)"
                }
            }
            isSyncing = false
        }
    }

    LaunchedEffect(Unit) {
        syncTalleres(showFeedback = false)
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "Crear Nuevo Taller",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newNombre,
                        onValueChange = { newNombre = it },
                        label = { Text("Nombre del taller") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newLugar,
                        onValueChange = { newLugar = it },
                        label = { Text("Sede o recinto vecinal") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newFecha,
                        onValueChange = { newFecha = it },
                        label = { Text("Fecha y horario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCupos,
                        onValueChange = { newCupos = it },
                        label = { Text("Cupos máximos") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNombre.isNotBlank() && newLugar.isNotBlank()) {
                            val cupos = newCupos.toIntOrNull()?.coerceAtLeast(1) ?: 30
                            val nombreCreado = newNombre.trim()
                            val lugarCreado = newLugar.trim()
                            val fechaCreada = newFecha.trim()
                            showCreateDialog = false
                            newNombre = ""
                            newLugar = ""
                            coroutineScope.launch {
                                isSyncing = true
                                val updated = TallerRemoteRepository.createTaller(
                                    nombre = nombreCreado,
                                    lugar = lugarCreado,
                                    fechaHora = fechaCreada,
                                    cuposMaximos = cupos,
                                    currentList = talleres
                                )
                                talleres = updated
                                selectedTaller = updated.first()
                                isSyncing = false
                                statusMessage = "Taller \"$nombreCreado\" guardado y sincronizado con la Web"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SyncPassEmerald)
                ) {
                    Text("Guardar Taller")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "Coordinación y Docencia",
                subtitle = "Vinculación con el Medio · Duoc UC",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Mantenedor de Talleres Territoriales",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sincronizado en tiempo real con la plataforma Web (/api/talleres).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showCreateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SyncPassEmerald),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+ Nuevo Taller", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { syncTalleres(showFeedback = true) },
                                enabled = !isSyncing,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (isSyncing) "Sincronizando..." else "Actualizar API")
                            }
                        }
                        statusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall,
                                color = SyncPassEmerald
                            )
                        }
                    }
                }
            }

            items(talleres, key = { it.id }) { taller ->
                val isSelected = taller.id == selectedTaller.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTaller = taller },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isEnCurso = taller.estado.equals("En curso", ignoreCase = true)
                            val badgeColor = if (isEnCurso) SyncPassEmerald else MaterialTheme.colorScheme.primary
                            Box(
                                modifier = Modifier
                                    .background(
                                        badgeColor.copy(alpha = 0.12f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = taller.estado,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = badgeColor
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = taller.fechaHora,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Eliminar",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.clickable {
                                        coroutineScope.launch {
                                            val updated = TallerRemoteRepository.deleteTaller(taller.id, talleres)
                                            if (updated.isNotEmpty()) {
                                                talleres = updated
                                                selectedTaller = updated.first()
                                            }
                                            statusMessage = "Taller eliminado de la base de datos"
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = taller.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lugar: ${taller.lugar}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        val safeMax = taller.cuposMaximos.coerceAtLeast(1)
                        val progress = (taller.cuposOcupados.toFloat() / safeMax.toFloat()).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Acreditados: ${taller.cuposOcupados} / ${taller.cuposMaximos}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(progress * 100).toInt()}% Quórum",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QrCodeView(
                        data = selectedTaller.codigoQr,
                        title = "Pase QR del Taller: ${selectedTaller.nombre}",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { statusMessage = "Acta de asistencia exportada para ${selectedTaller.nombre}" },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Descargar Acta de Asistencia (PDF)")
                    }
                }
            }
        }
    }
}
