package com.example.kotlin_login.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_login.ui.theme.SyncPassBluePrimary
import com.example.kotlin_login.ui.theme.SyncPassBorder
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassTextSecondary

/**
 * Componente modular para generación y visualización de Códigos QR en Jetpack Compose.
 * Renderiza una cuadrícula bidimensional matemática con patrones de detección estándar.
 */
@Composable
fun QrCodeView(
    data: String,
    title: String = "Código de Acreditación",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = SyncPassBluePrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Canvas de QR 15x15 matemático determinístico según el hash de data
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(Color.White)
                    .border(1.dp, SyncPassBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val matrixSize = 15
                val hashVal = data.hashCode()
                Canvas(modifier = Modifier.size(160.dp)) {
                    val cellSize = size.width / matrixSize
                    val darkColor = Color(0xFF0F172A)

                    for (r in 0 until matrixSize) {
                        for (c in 0 until matrixSize) {
                            val isCornerFinder =
                                (r < 4 && c < 4) || (r < 4 && c >= matrixSize - 4) || (r >= matrixSize - 4 && c < 4)
                            val isTiming = (r == 4 || c == 4)
                            val isDataCell = ((hashVal xor (r * 31 + c * 17)) % 2 == 0)

                            if (isCornerFinder || isTiming || isDataCell) {
                                drawRect(
                                    color = darkColor,
                                    topLeft = Offset(c * cellSize, r * cellSize),
                                    size = Size(cellSize * 0.92f, cellSize * 0.92f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data,
                style = MaterialTheme.typography.labelSmall,
                color = SyncPassTextSecondary
            )
        }
    }
}

/**
 * Componente modular para escaneo y lectura de Códigos QR.
 * Proporciona un visor animado de enfoque con retícula de precisión y modo de entrada manual.
 */
@Composable
fun QrScannerView(
    onCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var manualInput by remember { mutableStateOf("") }
    val infiniteTransition = rememberInfiniteTransition(label = "LaserTransition")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserY"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lector de Acreditación QR",
                style = MaterialTheme.typography.titleMedium,
                color = SyncPassBluePrimary
            )
            Text(
                text = "Alinee el código QR del pase dentro del visor de enfoque",
                style = MaterialTheme.typography.bodySmall,
                color = SyncPassTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Visor de Cámara y Enfoque Simulado
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .border(2.dp, SyncPassBluePrimary, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(190.dp)) {
                    // Retícula de esquinas
                    val cornerLen = 24.dp.toPx()
                    val strokeW = 4.dp.toPx()
                    val c = Color(0xFF38BDF8)

                    // Superior izquierda
                    drawLine(c, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
                    drawLine(c, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

                    // Superior derecha
                    drawLine(c, Offset(size.width, 0f), Offset(size.width - cornerLen, 0f), strokeW)
                    drawLine(c, Offset(size.width, 0f), Offset(size.width, cornerLen), strokeW)

                    // Inferior izquierda
                    drawLine(c, Offset(0f, size.height), Offset(cornerLen, size.height), strokeW)
                    drawLine(c, Offset(0f, size.height), Offset(0f, size.height - cornerLen), strokeW)

                    // Inferior derecha
                    drawLine(c, Offset(size.width, size.height), Offset(size.width - cornerLen, size.height), strokeW)
                    drawLine(c, Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), strokeW)

                    // Línea de escaneo láser animada
                    val currentLaserPos = size.height * laserY
                    drawLine(
                        color = Color(0xFF10B981),
                        start = Offset(12.dp.toPx(), currentLaserPos),
                        end = Offset(size.width - 12.dp.toPx(), currentLaserPos),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de simulación de lectura instantánea
            OutlinedButton(
                onClick = {
                    val fakeQr = "SYNCPASS-RUT-1${(1000000..9999999).random()}-K"
                    onCodeScanned(fakeQr)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simular Escaneo de Código QR")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de entrada manual para contingencias en terreno
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    placeholder = { Text("O ingresar RUT manual...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (manualInput.isNotBlank()) {
                            onCodeScanned(manualInput.trim())
                            manualInput = ""
                        }
                    },
                    enabled = manualInput.isNotBlank()
                ) {
                    Text("Acreditar")
                }
            }
        }
    }
}
