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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlin_login.domain.model.TallerEvento
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.components.QrCodeView
import com.example.kotlin_login.ui.components.SyncPassTopBar
import com.example.kotlin_login.ui.theme.SyncPassEmerald

private val DEMO_TALLERES = listOf(
    TallerEvento(
        id = "T-101",
        proyectoId = "P-1",
        nombre = "Alfabetización Digital para Adultos Mayores",
        fechaHora = "Hoy, 15:00 hrs",
        lugar = "Junta de Vecinos N°14 Villa El Roble",
        cuposMaximos = 40,
        cuposOcupados = 28,
        codigoQr = "SYNCPASS-EVENT-T101-ELROBLE",
        estado = "En curso"
    ),
    TallerEvento(
        id = "T-102",
        proyectoId = "P-1",
        nombre = "Taller de Huertos Comunitarios Sostenibles",
        fechaHora = "Mañana, 10:30 hrs",
        lugar = "Sede Vecinal Los Pinos",
        cuposMaximos = 25,
        cuposOcupados = 15,
        codigoQr = "SYNCPASS-EVENT-T102-LOSPINOS",
        estado = "Programado"
    ),
    TallerEvento(
        id = "T-103",
        proyectoId = "P-2",
        nombre = "Primeros Auxilios y Prevención Barrial",
        fechaHora = "24 Octubre, 16:00 hrs",
        lugar = "Colegio Polivalente San Bernardo",
        cuposMaximos = 35,
        cuposOcupados = 35,
        codigoQr = "SYNCPASS-EVENT-T103-SANBERNARDO",
        estado = "Completado"
    )
)

@Composable
fun DocenteScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var selectedTaller by remember { mutableStateOf(DEMO_TALLERES.first()) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            text = "Resumen de Actividades Territoriales",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Seleccione un taller para consultar quórum o generar código QR de acreditación.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(DEMO_TALLERES) { taller ->
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
                            val isEnCurso = taller.estado == "En curso"
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
                            Text(
                                text = taller.fechaHora,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = taller.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Lugar: ${taller.lugar}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        val progress = taller.cuposOcupados.toFloat() / taller.cuposMaximos.toFloat()
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
                        onClick = { /* Exportar o imprimir */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Descargar Acta de Asistencia (PDF)")
                    }
                }
            }
        }
    }
}
