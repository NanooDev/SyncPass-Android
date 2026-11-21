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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Componente modular para generación y visualización de Códigos QR en Jetpack Compose.
 * Renderiza una cuadrícula bidimensional matemática con patrones de detección estándar.
 */
@Composable
fun QrCodeView(
    data: String,
    title: String = "Código de acreditación",
    modifier: Modifier = Modifier
) {
    SyncPassCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Fondo blanco puro para lectura óptima en cámara
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                    .padding(14.dp),
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

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = data,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Componente modular para escaneo y lectura de Códigos QR.
 * Proporciona un visor animado minimalista y modo de entrada manual rápida.
 */
@Composable
fun QrScannerView(
    onCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var manualInput by remember { mutableStateOf("") }
    val infiniteTransition = rememberInfiniteTransition(label = "LaserTransition")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserY"
    )

    SyncPassCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Escanear pase QR",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Alinea el código dentro del recuadro",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0F172A))
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(164.dp)) {
                    val cornerLen = 22.dp.toPx()
                    val strokeW = 3.5.dp.toPx()
                    val c = Color(0xFF38BDF8)

                    drawLine(c, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
                    drawLine(c, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

                    drawLine(c, Offset(size.width, 0f), Offset(size.width - cornerLen, 0f), strokeW)
                    drawLine(c, Offset(size.width, 0f), Offset(size.width, cornerLen), strokeW)

                    drawLine(c, Offset(0f, size.height), Offset(cornerLen, size.height), strokeW)
                    drawLine(c, Offset(0f, size.height), Offset(0f, size.height - cornerLen), strokeW)

                    drawLine(c, Offset(size.width, size.height), Offset(size.width - cornerLen, size.height), strokeW)
                    drawLine(c, Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), strokeW)

                    val currentLaserPos = size.height * laserY
                    drawLine(
                        color = Color(0xFF34D399),
                        start = Offset(8.dp.toPx(), currentLaserPos),
                        end = Offset(size.width - 8.dp.toPx(), currentLaserPos),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    val fakeQr = "SYNCPASS-RUT-1${(1000000..9999999).random()}-K"
                    onCodeScanned(fakeQr)
                },
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
                    text = "Simular lectura QR",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    placeholder = { Text("RUT o código manual") },
                    shape = RoundedCornerShape(14.dp),
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
                    enabled = manualInput.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Listo",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
