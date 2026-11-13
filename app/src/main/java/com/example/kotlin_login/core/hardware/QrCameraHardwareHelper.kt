package com.example.kotlin_login.core.hardware

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

data class QrParseResult(
    val codigoNormalizado: String,
    val nombreSugerido: String,
    val metodo: String,
    val esQrInstitucional: Boolean
)

/**
 * API Nativa de Android #2: Hardware de Cámara (android.hardware.camera2.CameraManager)
 * y decodificación de pases ópticos QR SyncPass.
 */
object QrCameraHardwareHelper {

    fun detectarCamaraTraseraDisponible(context: Context?): Boolean {
        if (context == null) return true
        return runCatching {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
                ?: return true
            cameraManager.cameraIdList.any { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                chars.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }
        }.getOrDefault(true)
    }

    fun procesarCodigoEscaneado(rawInput: String): QrParseResult {
        val clean = rawInput.trim()
        val esQr = clean.startsWith("SYNCPASS", ignoreCase = true) ||
                clean.startsWith("QR-", ignoreCase = true) ||
                clean.startsWith("EVT-", ignoreCase = true) ||
                clean.startsWith("BEN-", ignoreCase = true)

        val codigo = when {
            clean.startsWith("SYNCPASS-BENEFICIARIO-", ignoreCase = true) ->
                clean.removePrefix("SYNCPASS-BENEFICIARIO-")
            clean.startsWith("QR-", ignoreCase = true) ->
                clean.removePrefix("QR-")
            clean.isNotBlank() -> clean
            else -> "BEN-00101"
        }

        val nombre = when {
            codigo.contains("00101") || codigo.contains("12345678") -> "María González (QR)"
            codigo.contains("00102") -> "Carlos Soto (QR)"
            codigo.contains("00103") -> "Rosa Díaz (QR)"
            esQr -> "Beneficiario Acreditado QR"
            else -> "Vecino Acreditado ($codigo)"
        }

        return QrParseResult(
            codigoNormalizado = codigo,
            nombreSugerido = nombre,
            metodo = if (esQr) "QR" else "MANUAL",
            esQrInstitucional = esQr
        )
    }
}
