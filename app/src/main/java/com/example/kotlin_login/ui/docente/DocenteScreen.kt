package com.example.kotlin_login.ui.docente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.ui.components.QrCodeView
import com.example.kotlin_login.ui.components.SyncPassBottomBar
import com.example.kotlin_login.ui.components.SyncPassCard
import com.example.kotlin_login.ui.components.SyncPassKpiTile
import com.example.kotlin_login.ui.components.SyncPassNavTab
import com.example.kotlin_login.ui.components.SyncPassSegmentedControl
import com.example.kotlin_login.ui.components.SyncPassSettingsActionsCard
import com.example.kotlin_login.ui.components.SyncPassStatusPill
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldDark
import com.example.kotlin_login.ui.theme.ThemeState

@Composable
fun DocenteScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: DocenteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var filterTab by rememberSaveable { mutableIntStateOf(0) }

    val filteredTalleres = remember(uiState.talleres, filterTab) {
        when (filterTab) {
            1 -> uiState.talleres.filter { it.estado.equals("En curso", ignoreCase = true) }
            2 -> uiState.talleres.filter { !it.estado.equals("En curso", ignoreCase = true) }
            else -> uiState.talleres
        }
    }

    val totalOcupados = uiState.talleres.sumOf { it.cuposOcupados }
    val totalMaximos = uiState.talleres.sumOf { it.cuposMaximos }.coerceAtLeast(1)
    val promedioQuorum = ((totalOcupados.toFloat() / totalMaximos.toFloat()) * 100).toInt().coerceIn(0, 100)

    if (uiState.showCreateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissCreateDialog() },
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    text = "Nuevo taller",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.newNombre,
                        onValueChange = { viewModel.onNewNombreChange(it) },
                        label = { Text("Nombre") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.newLugar,
                        onValueChange = { viewModel.onNewLugarChange(it) },
                        label = { Text("Sede o recinto") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.newFecha,
                        onValueChange = { viewModel.onNewFechaChange(it) },
                        label = { Text("Fecha y hora") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.newCupos,
                        onValueChange = { viewModel.onNewCuposChange(it) },
                        label = { Text("Cupos máximos") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onCreateTaller() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SyncPassEmeraldDark,
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissCreateDialog() }) {
                    Text("Cancelar", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    val tabs = remember {
        listOf(
            SyncPassNavTab("Talleres", Icons.AutoMirrored.Rounded.EventNote),
            SyncPassNavTab("Pase QR", Icons.Rounded.QrCode2),
            SyncPassNavTab("Ajustes", Icons.Rounded.Tune)
        )
    }

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = when (selectedTab) {
                    1 -> "Pase del Taller"
                    2 -> "Ajustes"
                    else -> "Docente"
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
            // TAB 1: Indicadores + Agenda de Talleres estilo calendario iOS
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SyncPassKpiTile(
                                value = "${uiState.talleres.size}",
                                label = "Talleres",
                                modifier = Modifier.weight(1f)
                            )
                            SyncPassKpiTile(
                                value = "$totalOcupados",
                                label = "Acreditados",
                                accentColor = if (ThemeState.isDarkTheme) SyncPassEmerald else SyncPassEmeraldDark,
                                modifier = Modifier.weight(1f)
                            )
                            SyncPassKpiTile(
                                value = "$promedioQuorum%",
                                label = "Quórum",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.onOpenCreateDialog() },
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(vertical = 14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Nuevo taller", style = MaterialTheme.typography.labelLarge)
                            }

                            OutlinedButton(
                                onClick = { viewModel.syncTalleres(showFeedback = true) },
                                enabled = !uiState.isSyncing,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                if (uiState.isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "Actualizar",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    uiState.statusMessage?.let { msg ->
                        item {
                            val accent = if (ThemeState.isDarkTheme) SyncPassEmerald else SyncPassEmeraldDark
                            SyncPassCard(borderColor = accent.copy(alpha = 0.55f)) {
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = accent
                                )
                            }
                        }
                    }

                    item {
                        SyncPassSegmentedControl(
                            options = listOf("Todos", "En curso", "Otros"),
                            selectedIndex = filterTab,
                            onSelect = { filterTab = it }
                        )
                    }

                    items(filteredTalleres, key = { it.id }) { taller ->
                        val isSelected = taller.id == uiState.selectedTaller?.id
                        val isEnCurso = taller.estado.equals("En curso", ignoreCase = true)
                        val isDark = ThemeState.isDarkTheme
                        val statusColor = if (isEnCurso) {
                            if (isDark) SyncPassEmerald else SyncPassEmeraldDark
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                        val safeMax = taller.cuposMaximos.coerceAtLeast(1)
                        val progress = (taller.cuposOcupados.toFloat() / safeMax.toFloat()).coerceIn(0f, 1f)
                        val badgeLabel = taller.fechaHora
                            .split(",", " ")
                            .firstOrNull { it.isNotBlank() }
                            ?.take(4)
                            ?.uppercase()
                            ?: "HOY"

                        SyncPassCard(
                            borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            onClick = { viewModel.onSelectTaller(taller) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Insignia de fecha estilo calendario (inspirada en Mario & María José)
                                Box(
                                    modifier = Modifier
                                        .size(width = 52.dp, height = 56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badgeLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SyncPassStatusPill(
                                            text = taller.estado,
                                            accentColor = statusColor
                                        )
                                        IconButton(
                                            onClick = { viewModel.onDeleteTaller(taller.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.DeleteOutline,
                                                contentDescription = "Eliminar taller",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = taller.nombre,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${taller.lugar} · ${taller.fechaHora}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${taller.cuposOcupados} de ${taller.cuposMaximos} cupos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // TAB 2: Pase QR y Exportación de Acta CSV
            1 -> {
                val selectedTaller = uiState.selectedTaller ?: uiState.talleres.firstOrNull()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (selectedTaller == null) {
                        SyncPassCard {
                            Text(
                                text = "Selecciona un taller",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Elige una jornada en la pestaña Talleres para ver su QR y exportar el acta.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        SyncPassCard {
                            Text(
                                text = selectedTaller.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${selectedTaller.lugar} · ${selectedTaller.fechaHora}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        QrCodeView(
                            data = selectedTaller.codigoQr,
                            title = "Pase del taller",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { viewModel.onExportActa(context) },
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(vertical = 15.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exportar acta CSV",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // TAB 3: Ajustes
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SyncPassCard(onClick = { viewModel.syncTalleres(showFeedback = true) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Sincronizar talleres",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (uiState.isSyncing) "Actualizando..." else "Traer datos recientes de la nube",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    SyncPassSettingsActionsCard(onLogoutClick = onLogoutClick)
                }
            }
        }
    }
}
