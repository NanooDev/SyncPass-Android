package com.example.kotlin_login.ui.voluntario

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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.ui.components.QrScannerView
import com.example.kotlin_login.ui.components.SyncPassBottomBar
import com.example.kotlin_login.ui.components.SyncPassCard
import com.example.kotlin_login.ui.components.SyncPassNavTab
import com.example.kotlin_login.ui.components.SyncPassOfflineCard
import com.example.kotlin_login.ui.components.SyncPassSegmentedControl
import com.example.kotlin_login.ui.components.SyncPassSettingsActionsCard
import com.example.kotlin_login.ui.components.SyncPassStatusPill
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassAmberDark
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldDark
import com.example.kotlin_login.ui.theme.ThemeState
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
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var filterTab by rememberSaveable { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredAsistencias = remember(uiState.asistencias, searchQuery, filterTab) {
        uiState.asistencias.filter { item ->
            val matchesFilter = when (filterTab) {
                1 -> item.sincronizado
                2 -> !item.sincronizado
                else -> true
            }
            val matchesQuery = searchQuery.isBlank() ||
                item.nombreBeneficiario.contains(searchQuery, ignoreCase = true) ||
                item.rut.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    val tabs = remember {
        listOf(
            SyncPassNavTab("Inicio", Icons.Rounded.Home),
            SyncPassNavTab("Acreditar", Icons.Rounded.QrCodeScanner),
            SyncPassNavTab("Ajustes", Icons.Rounded.Tune)
        )
    }

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = when (selectedTab) {
                    1 -> "Acreditar"
                    2 -> "Ajustes y Red"
                    else -> "Voluntario"
                },
                isOffline = uiState.isOffline,
                onStatusClick = { selectedTab = 2 },
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
            // 1. INICIO: Hero + Buscador + Lista limpia de asistencias
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        SyncPassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${uiState.asistencias.size} acreditados",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (uiState.pendingSyncCount > 0)
                                            "${uiState.pendingSyncCount} pendiente(s) de sincronizar"
                                        else
                                            "Sede validada · ${uiState.distanciaRecintoMetros} m",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                SyncPassStatusPill(
                                    text = if (uiState.dentroDeGeocerca) "En sede" else "Fuera de radio",
                                    accentColor = if (uiState.dentroDeGeocerca) {
                                        if (ThemeState.isDarkTheme) SyncPassEmerald else SyncPassEmeraldDark
                                    } else {
                                        if (ThemeState.isDarkTheme) SyncPassAmber else SyncPassAmberDark
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { selectedTab = 1 },
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(vertical = 14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.QrCodeScanner,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Escanear pase QR",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Buscar por nombre o RUT") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            SyncPassSegmentedControl(
                                options = listOf("Todos", "Listos", "Pendientes"),
                                selectedIndex = filterTab,
                                onSelect = { filterTab = it }
                            )
                        }
                    }

                    if (filteredAsistencias.isEmpty()) {
                        item {
                            SyncPassCard {
                                Text(
                                    text = "Sin registros en esta vista",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Escanea un QR en la pestaña Acreditar para sumar asistencias.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(filteredAsistencias, key = { it.id }) { item ->
                            val isDark = ThemeState.isDarkTheme
                            val syncAccent = if (item.sincronizado) {
                                if (isDark) SyncPassEmerald else SyncPassEmeraldDark
                            } else {
                                if (isDark) SyncPassAmber else SyncPassAmberDark
                            }
                            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(Date(item.timestamp))

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
                                                text = item.nombreBeneficiario.firstOrNull()?.uppercase() ?: "A",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = item.nombreBeneficiario,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${item.rut} · $timeStr hrs",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    SyncPassStatusPill(
                                        text = if (item.sincronizado) "Listo" else "Pendiente",
                                        accentColor = syncAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. ACREDITAR: Escáner QR enfocado + confirmación clara
            1 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (uiState.feedbackMessage != null) {
                        val accent = if (ThemeState.isDarkTheme) SyncPassEmerald else SyncPassEmeraldDark
                        SyncPassCard(borderColor = accent.copy(alpha = 0.6f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = uiState.feedbackMessage ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    QrScannerView(
                        onCodeScanned = { scannedCode ->
                            viewModel.onCodeScanned(scannedCode)
                        }
                    )
                }
            }

            // 3. AJUSTES: Control Offline visible en oscuro + Sincronización + GPS + Apariencia
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SyncPassOfflineCard(
                        isOffline = uiState.isOffline,
                        pendingCount = uiState.pendingSyncCount,
                        syncedCount = uiState.asistencias.count { it.sincronizado },
                        isSyncing = uiState.isSyncing,
                        onToggleOffline = { viewModel.onToggleOfflineMode(it) },
                        onSyncClick = { viewModel.onSyncPendingClick() }
                    )

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
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "GPS del recinto",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${uiState.distanciaRecintoMetros} m · Radio 150 m",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { viewModel.onRefreshHardwareSensors(context) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Validar",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    SyncPassSettingsActionsCard(onLogoutClick = onLogoutClick)
                }
            }
        }
    }
}
