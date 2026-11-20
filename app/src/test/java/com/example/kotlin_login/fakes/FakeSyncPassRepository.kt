package com.example.kotlin_login.fakes

import com.example.kotlin_login.data.repository.RemoteUserItem
import com.example.kotlin_login.domain.model.AsistenciaRegistro
import com.example.kotlin_login.domain.model.EncuestaSatisfaccion
import com.example.kotlin_login.domain.model.TallerEvento
import com.example.kotlin_login.domain.repository.SyncPassRepository
import java.util.UUID

/**
 * Fake de SyncPassRepository para aislar las pruebas unitarias de los ViewModels.
 * Permite simular respuestas exitosas, listas vacías y escenarios de error controlado.
 */
class FakeSyncPassRepository(
    var shouldThrowError: Boolean = false,
    var errorMessage: String = "Fallo simulado en el repositorio"
) : SyncPassRepository {

    val talleresFake = mutableListOf(
        TallerEvento(
            id = "T-FAKE-01",
            proyectoId = "P-FAKE-01",
            nombre = "Taller Digital Sede Maipú",
            fechaHora = "Hoy 16:00 hrs",
            lugar = "Junta de Vecinos Villa El Roble",
            cuposMaximos = 40,
            cuposOcupados = 12,
            codigoQr = "EVT-FAKE-01",
            estado = "En curso",
            sincronizado = true
        )
    )

    val asistenciasFake = mutableListOf(
        AsistenciaRegistro(
            id = "A-FAKE-01",
            tallerId = "T-FAKE-01",
            rut = "12.345.678-9",
            nombreBeneficiario = "María González",
            metodo = "QR",
            sincronizado = true,
            distanciaMetros = 35,
            estadoValidacion = "valido"
        )
    )

    val encuestasFake = mutableListOf<EncuestaSatisfaccion>()

    override suspend fun getTalleres(forceRemote: Boolean): List<TallerEvento> {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        return talleresFake.toList()
    }

    override suspend fun createTaller(
        nombre: String,
        lugar: String,
        fechaHora: String,
        cuposMaximos: Int,
        isOffline: Boolean
    ): List<TallerEvento> {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        val created = TallerEvento(
            id = "T-${UUID.randomUUID().toString().take(6)}",
            proyectoId = "P-FAKE-01",
            nombre = nombre,
            fechaHora = fechaHora,
            lugar = lugar,
            cuposMaximos = cuposMaximos,
            cuposOcupados = 0,
            codigoQr = "EVT-NEW",
            estado = "Activo",
            sincronizado = !isOffline
        )
        talleresFake.add(0, created)
        return talleresFake.toList()
    }

    override suspend fun deleteTaller(tallerId: String): List<TallerEvento> {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        talleresFake.removeIf { it.id == tallerId }
        return talleresFake.toList()
    }

    override suspend fun getAsistencias(forceRemote: Boolean): List<AsistenciaRegistro> {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        return asistenciasFake.toList()
    }

    override suspend fun registrarAsistencia(
        tallerId: String,
        rutOrCode: String,
        nombreBeneficiario: String,
        metodo: String,
        latitud: Double,
        longitud: Double,
        distanciaMetros: Int,
        estadoValidacion: String,
        isOffline: Boolean
    ): AsistenciaRegistro {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        val reg = AsistenciaRegistro(
            id = "A-${UUID.randomUUID().toString().take(6)}",
            tallerId = tallerId,
            rut = rutOrCode,
            nombreBeneficiario = nombreBeneficiario,
            metodo = metodo,
            sincronizado = !isOffline,
            latitud = latitud,
            longitud = longitud,
            distanciaMetros = distanciaMetros,
            estadoValidacion = estadoValidacion
        )
        asistenciasFake.add(0, reg)
        return reg
    }

    override suspend fun getPendingAsistenciasCount(): Int {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        return asistenciasFake.count { !it.sincronizado }
    }

    override suspend fun syncPendingAsistencias(): Int {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        var count = 0
        for (i in asistenciasFake.indices) {
            if (!asistenciasFake[i].sincronizado) {
                asistenciasFake[i] = asistenciasFake[i].copy(sincronizado = true)
                count++
            }
        }
        return count
    }

    override suspend fun registrarEncuesta(
        tallerId: String,
        rutBeneficiario: String,
        estrellas: Int,
        utilidad: Int,
        comentarios: String,
        isOffline: Boolean
    ): EncuestaSatisfaccion {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        val enc = EncuestaSatisfaccion(
            id = "E-${UUID.randomUUID().toString().take(6)}",
            tallerId = tallerId,
            rutBeneficiario = rutBeneficiario,
            calificacionEstrellas = estrellas,
            utilidad = utilidad,
            comentarios = comentarios,
            sincronizado = !isOffline
        )
        encuestasFake.add(0, enc)
        return enc
    }

    override suspend fun syncPendingEncuestas(): Int {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        return encuestasFake.count { !it.sincronizado }
    }

    override suspend fun getUsuarios(): List<RemoteUserItem> {
        if (shouldThrowError) throw IllegalStateException(errorMessage)
        return listOf(
            RemoteUserItem("u1", "Mariano Acevedo", "docente@duocuc.cl", "coordinator", true)
        )
    }
}
