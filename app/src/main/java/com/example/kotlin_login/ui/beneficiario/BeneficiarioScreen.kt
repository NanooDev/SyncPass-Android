package com.example.kotlin_login.ui.beneficiario

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.components.QrCodeView
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassBlueLight
import com.example.kotlin_login.ui.theme.SyncPassBluePrimary
import com.example.kotlin_login.ui.theme.SyncPassBorder
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldLight
import com.example.kotlin_login.ui.theme.SyncPassSurfaceVariant
import com.example.kotlin_login.ui.theme.SyncPassTextPrimary
import com.example.kotlin_login.ui.theme.SyncPassTextSecondary

@Composable
fun BeneficiarioScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var utilityLevel by remember { mutableIntStateOf(4) }
    var comments by remember { mutableStateOf("") }
    var surveySent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SyncPassTopBar(
                title = "Portal del Beneficiario",
                subtitle = "Pase de Acceso y Valoración Ciudadana",
                currentRole = UserRole.BENEFICIARIO,
                onBackClick = onBackClick,
                onLogoutClick = onLogoutClick
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
            // Pase Digital Personal
            QrCodeView(
                data = "SYNCPASS-BENEFICIARIO-12345678-9",
                title = "Su Pase Digital SyncPass",
                modifier = Modifier.fillMaxWidth()
            )

            // Ficha de Inscripción al Taller
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, SyncPassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Actividad Comunitaria Asignada",
                        style = MaterialTheme.typography.titleSmall,
                        color = SyncPassBluePrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Alfabetización Digital para Adultos Mayores",
                        style = MaterialTheme.typography.titleMedium,
                        color = SyncPassTextPrimary
                    )
                    Text(
                        text = "Sede: Junta de Vecinos N°14 Villa El Roble",
                        style = MaterialTheme.typography.bodySmall,
                        color = SyncPassTextSecondary
                    )
                    Text(
                        text = "Horario: Hoy de 15:00 a 17:30 hrs",
                        style = MaterialTheme.typography.bodySmall,
                        color = SyncPassTextSecondary
                    )
                }
            }

            // Encuesta de Satisfacción Rápida
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, SyncPassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Encuesta Inmediata de Impacto y Calidad",
                        style = MaterialTheme.typography.titleSmall,
                        color = SyncPassBluePrimary
                    )
                    Text(
                        text = "Su opinión permite a Duoc UC perfeccionar las futuras iniciativas comunitarias.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SyncPassTextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (surveySent) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SyncPassEmeraldLight, RoundedCornerShape(8.dp))
                                .border(1.dp, SyncPassEmerald, RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Evaluación Registrada con Éxito",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = SyncPassEmerald
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Agradecemos su participación en esta jornada territorial.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SyncPassTextPrimary
                                )
                            }
                        }
                    } else {
                        // Calificación General (Selector 1 a 5 numérico y limpio, sin emojis)
                        Text(
                            text = "Calificación general del taller (1 a 5):",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SyncPassTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (1..5).forEach { star ->
                                val isSelected = star <= rating
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .background(
                                            if (isSelected) SyncPassBlueLight else SyncPassSurfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) SyncPassBluePrimary else SyncPassBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { rating = star },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$star",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (isSelected) SyncPassBluePrimary else SyncPassTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nivel de Utilidad Práctica
                        Text(
                            text = "¿Qué tan útil resultó el contenido entregado?:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SyncPassTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Bajo" to 1, "Medio" to 2, "Alto" to 3, "Excelente" to 4).forEach { (label, value) ->
                                val isSelected = utilityLevel == value
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .background(
                                            if (isSelected) SyncPassBlueLight else SyncPassSurfaceVariant,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) SyncPassBluePrimary else SyncPassBorder,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { utilityLevel = value },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) SyncPassBluePrimary else SyncPassTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = comments,
                            onValueChange = { comments = it },
                            label = { Text("Comentarios o sugerencias (Opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { surveySent = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Enviar Encuesta de Satisfacción")
                        }
                    }
                }
            }
        }
    }
}
