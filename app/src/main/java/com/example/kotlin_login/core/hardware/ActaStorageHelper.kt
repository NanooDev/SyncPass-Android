package com.example.kotlin_login.core.hardware

import android.content.Context
import com.example.kotlin_login.domain.model.AsistenciaRegistro
import com.example.kotlin_login.domain.model.TallerEvento
import java.io.File

/**
 * API Nativa de Android #3: Almacenamiento de Archivos (File Storage / Context.filesDir)
 * para exportar actas locales de asistencia territorial en formato CSV.
 */
object ActaStorageHelper {

    fun generarContenidoCsv(
        taller: TallerEvento,
        asistencias: List<AsistenciaRegistro>
    ): String {
        val sb = StringBuilder()
        sb.appendLine("ID_REGISTRO,ID_TALLER,NOMBRE_TALLER,IDENTIFICADOR,BENEFICIARIO,METODO,LATITUD,LONGITUD,DISTANCIA_M,VALIDACION,SINCRONIZADO")
        asistencias.forEach { a ->
            sb.appendLine(
                "${a.id},${taller.id},\"${taller.nombre}\",${a.rut},\"${a.nombreBeneficiario}\",${a.metodo},${a.latitud},${a.longitud},${a.distanciaMetros},${a.estadoValidacion},${a.sincronizado}"
            )
        }
        return sb.toString()
    }

    fun guardarActaEnDispositivo(
        context: Context?,
        taller: TallerEvento,
        asistencias: List<AsistenciaRegistro>,
        targetDir: File? = null
    ): File {
        val dir = targetDir ?: context?.filesDir ?: File(System.getProperty("java.io.tmpdir", "."))
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val safeId = taller.id.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(20)
        val file = File(dir, "acta_asistencia_${safeId}.csv")
        file.writeText(generarContenidoCsv(taller, asistencias), Charsets.UTF_8)
        return file
    }
}
