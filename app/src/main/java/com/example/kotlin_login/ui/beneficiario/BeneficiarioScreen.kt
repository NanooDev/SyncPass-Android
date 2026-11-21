package com.example.kotlin_login.ui.beneficiario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.ui.components.QrCodeView
import com.example.kotlin_login.ui.components.SyncPassBottomBar
import com.example.kotlin_login.ui.components.SyncPassCard
import com.example.kotlin_login.ui.components.SyncPassNavTab
import com.example.kotlin_login.ui.components.SyncPassStatusPill
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassAmberDark
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldDark
import com.example.kotlin_login.ui.theme.ThemeState

@Composable
fun BeneficiarioScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: BeneficiarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val tabs = remember {
        listOf(
            SyncPassNavTab("Mi Pase", Icons.Rounded.QrCode2),
            SyncPassNavTab("Encuesta", Icons.Rounded.RateReview)
        )
    }

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = if (selectedTab == 0) "Mi Pase Digital" else "Evaluar Taller",
                isOffline = uiState.isOffline,
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
            // TAB 1: Pase QR limpio estilo Apple Wallet + Resumen del Taller
            0 -> {
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SyncPassStatusPill(
                                text = "Inscrito",
                                accentColor = if (ThemeState.isDarkTheme) SyncPassEmerald else SyncPassEmeraldDark
                            )
                            Icon(
                                imageVector = Icons.Rounded.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiState.nombreActividad,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.sedeActividad.removePrefix("Sede: "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = uiState.horarioActividad.removePrefix("Horario: "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    QrCodeView(
                        data = uiState.qrPersonal,
                        title = "Pase SyncPass",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { selectedTab = 1 },
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.surveySent) "Ver evaluación enviada" else "Responder encuesta",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // TAB 2: Encuesta de Satisfacción con Estrellas (inspirada en Jorge Cisternas)
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (uiState.surveySent) {
                        val successColor = if (ThemeState.isDarkTheme) SyncPassEmerald else SyncPassEmeraldDark
                        SyncPassCard {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(successColor.copy(alpha = 0.16f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = successColor,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "¡Gracias por evaluar!",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = uiState.statusMessage ?: "Tu valoración quedó registrada.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Button(
                                    onClick = { selectedTab = 0 },
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "Volver a mi pase",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    } else {
                        SyncPassCard {
                            Text(
                                text = "Satisfacción general",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "¿Qué te pareció el taller de hoy?",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            val starActiveColor = if (ThemeState.isDarkTheme) SyncPassAmber else SyncPassAmberDark
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (1..5).forEach { star ->
                                    val filled = star <= uiState.rating
                                    IconButton(
                                        onClick = { viewModel.onRatingChange(star) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                            contentDescription = "$star estrellas",
                                            tint = if (filled) starActiveColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Utilidad del contenido",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Bajo" to 1, "Medio" to 2, "Alto" to 3, "Excelente" to 4).forEach { (label, value) ->
                                    val isSelected = uiState.utilityLevel == value
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .border(
                                                width = if (isSelected) 1.25.dp else 0.75.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { viewModel.onUtilityChange(value) }
                                            .padding(vertical = 11.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = uiState.comments,
                                onValueChange = { viewModel.onCommentsChange(it) },
                                label = { Text("Comentario (opcional)") },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = { viewModel.onSubmitSurvey() },
                                enabled = !uiState.isSubmitting,
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(vertical = 15.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (uiState.isSubmitting) "Enviando..." else "Enviar encuesta",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
