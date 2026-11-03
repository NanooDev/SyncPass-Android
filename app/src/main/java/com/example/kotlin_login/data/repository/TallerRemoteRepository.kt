package com.example.kotlin_login.data.repository

import com.example.kotlin_login.domain.model.TallerEvento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class RemoteUserItem(
    val id: String,
    val nombre: String,
    val email: String,
    val role: String,
    val activo: Boolean
)

/**
 * Repositorio remoto compartido entre SyncPass-Web y SyncPass-Android.
 * Consume la API REST (/api/talleres y /api/usuarios) y sincroniza el estado en la nube
 * para que cualquier taller o usuario creado en la Web se refleje inmediatamente en Android y viceversa.
 */
object TallerRemoteRepository {

    private const val WEB_API_BASE = "https://sync-pass-web-rho.vercel.app"
    private const val CLOUD_TALLERES_STORE = "https://ntfy.sh/syncpass_cloud_db_talleres_v3"
    private const val CLOUD_USUARIOS_STORE = "https://ntfy.sh/syncpass_cloud_db_usuarios_v3"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .writeTimeout(6, TimeUnit.SECONDS)
        .build()

    val initialTalleres = listOf(
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
            estado = "Activo"
        ),
        TallerEvento(
            id = "T-103",
            proyectoId = "P-2",
            nombre = "Primeros Auxilios y Prevención Barrial",
            fechaHora = "24 Oct, 16:00 hrs",
            lugar = "Colegio Polivalente San Bernardo",
            cuposMaximos = 35,
            cuposOcupados = 35,
            codigoQr = "SYNCPASS-EVENT-T103-SANBERNARDO",
            estado = "Finalizado"
        )
    )

    suspend fun fetchTalleres(): List<TallerEvento> = withContext(Dispatchers.IO) {
        // 1. Intentar vía endpoint REST /api/talleres desplegado
        runCatching {
            val req = Request.Builder()
                .url("$WEB_API_BASE/api/talleres")
                .header("Accept", "application/json")
                .get()
                .build()
            httpClient.newCall(req).execute().use { res ->
                if (res.isSuccessful) {
                    val bodyStr = res.body?.string().orEmpty()
                    val json = JSONObject(bodyStr)
                    val arr = json.optJSONArray("datos")
                    if (arr != null && arr.length() > 0) {
                        return@withContext parseTalleresArray(arr)
                    }
                }
            }
        }

        // 2. Respaldo directo contra el almacén compartido en la nube (funciona incluso con localhost:5173)
        runCatching {
            val req = Request.Builder()
                .url("$CLOUD_TALLERES_STORE/json?poll=1&since=48h")
                .get()
                .build()
            httpClient.newCall(req).execute().use { res ->
                if (res.isSuccessful) {
                    val lines = res.body?.string().orEmpty()
                        .split("\n")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    for (i in lines.indices.reversed()) {
                        runCatching {
                            val evt = JSONObject(lines[i])
                            if (evt.optString("event") == "message") {
                                val msg = JSONObject(evt.optString("message"))
                                if (msg.optString("type") == "talleres_snapshot") {
                                    val arr = msg.optJSONArray("items")
                                    if (arr != null && arr.length() > 0) {
                                        return@withContext parseTalleresArray(arr)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        initialTalleres
    }

    suspend fun createTaller(
        nombre: String,
        lugar: String,
        fechaHora: String,
        cuposMaximos: Int,
        currentList: List<TallerEvento>
    ): List<TallerEvento> = withContext(Dispatchers.IO) {
        val newId = "T-${(System.currentTimeMillis() % 10000)}"
        val nuevo = TallerEvento(
            id = newId,
            proyectoId = "P-1",
            nombre = nombre.trim(),
            fechaHora = fechaHora.trim(),
            lugar = lugar.trim(),
            cuposMaximos = cuposMaximos,
            cuposOcupados = 0,
            codigoQr = "SYNCPASS-EVENT-$newId",
            estado = "Activo"
        )
        val updated = listOf(nuevo) + currentList.filter { it.id != newId }

        // Enviar POST a /api/talleres y sincronizar snapshot compartido
        runCatching {
            val payload = JSONObject().apply {
                put("id", nuevo.id)
                put("name", nuevo.nombre)
                put("location", nuevo.lugar)
                put("date", nuevo.fechaHora)
                put("capacity", nuevo.cuposMaximos)
                put("attendeesCount", nuevo.cuposOcupados)
                put("status", nuevo.estado)
                put("coordinatorName", "Prof. Mariano Acevedo")
            }
            val req = Request.Builder()
                .url("$WEB_API_BASE/api/talleres")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()
            httpClient.newCall(req).execute().close()
        }

        saveSnapshotToCloud(updated)
        updated
    }

    suspend fun deleteTaller(
        tallerId: String,
        currentList: List<TallerEvento>
    ): List<TallerEvento> = withContext(Dispatchers.IO) {
        val updated = currentList.filter { it.id != tallerId }

        runCatching {
            val req = Request.Builder()
                .url("$WEB_API_BASE/api/talleres?id=$tallerId")
                .delete()
                .build()
            httpClient.newCall(req).execute().close()
        }

        saveSnapshotToCloud(updated)
        updated
    }

    suspend fun fetchUsuarios(): List<RemoteUserItem> = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder()
                .url("$WEB_API_BASE/api/usuarios")
                .header("Accept", "application/json")
                .get()
                .build()
            httpClient.newCall(req).execute().use { res ->
                if (res.isSuccessful) {
                    val json = JSONObject(res.body?.string().orEmpty())
                    val arr = json.optJSONArray("datos")
                    if (arr != null && arr.length() > 0) {
                        return@withContext parseUsuariosArray(arr)
                    }
                }
            }
        }

        runCatching {
            val req = Request.Builder()
                .url("$CLOUD_USUARIOS_STORE/json?poll=1&since=48h")
                .get()
                .build()
            httpClient.newCall(req).execute().use { res ->
                if (res.isSuccessful) {
                    val lines = res.body?.string().orEmpty()
                        .split("\n")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    for (i in lines.indices.reversed()) {
                        runCatching {
                            val evt = JSONObject(lines[i])
                            if (evt.optString("event") == "message") {
                                val msg = JSONObject(evt.optString("message"))
                                if (msg.optString("type") == "usuarios_snapshot") {
                                    val arr = msg.optJSONArray("items")
                                    if (arr != null && arr.length() > 0) {
                                        return@withContext parseUsuariosArray(arr)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        listOf(
            RemoteUserItem("usr-1", "Administrador SyncPass", "admin.syncpass@duocuc.cl", "Administrador", true),
            RemoteUserItem("usr-2", "Prof. Mariano Acevedo", "docente.coordinador@duocuc.cl", "Docente / Coordinador", true),
            RemoteUserItem("usr-3", "Álvaro León", "voluntario.estudiante@duocuc.cl", "Estudiante Voluntario", true),
            RemoteUserItem("usr-4", "María González", "maria.gonzalez@comunidad.cl", "Beneficiario Comunitario", true)
        )
    }

    private fun saveSnapshotToCloud(list: List<TallerEvento>) {
        runCatching {
            val itemsArr = JSONArray()
            list.forEach { t ->
                itemsArr.put(
                    JSONObject().apply {
                        put("id", t.id)
                        put("name", t.nombre)
                        put("location", t.lugar)
                        put("date", t.fechaHora)
                        put("attendeesCount", t.cuposOcupados)
                        put("capacity", t.cuposMaximos)
                        put("status", t.estado)
                        put("coordinatorName", "Prof. Mariano Acevedo")
                        put("qrCode", t.codigoQr)
                    }
                )
            }
            val snapshot = JSONObject().apply {
                put("type", "talleres_snapshot")
                put("items", itemsArr)
            }
            val req = Request.Builder()
                .url(CLOUD_TALLERES_STORE)
                .post(snapshot.toString().toRequestBody("text/plain; charset=utf-8".toMediaType()))
                .build()
            httpClient.newCall(req).execute().close()
        }
    }

    private fun parseTalleresArray(arr: JSONArray): List<TallerEvento> {
        val result = mutableListOf<TallerEvento>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val id = obj.optString("id", "T-${100 + i}")
            val nombre = obj.optString("name").ifBlank { obj.optString("nombre", "Taller Comunitario") }
            val lugar = obj.optString("location").ifBlank { obj.optString("lugar", "Sede Vecinal") }
            val fechaHora = obj.optString("date").ifBlank { obj.optString("fechaHora", "Hoy, 15:00 hrs") }
            val cuposMax = obj.optInt("capacity", obj.optInt("cuposMaximos", 30))
            val cuposOcup = obj.optInt("attendeesCount", obj.optInt("cuposOcupados", 0))
            val estado = obj.optString("status").ifBlank { obj.optString("estado", "Activo") }
            val qr = obj.optString("qrCode").ifBlank { "SYNCPASS-EVENT-$id" }
            result.add(
                TallerEvento(
                    id = id,
                    proyectoId = "P-1",
                    nombre = nombre,
                    fechaHora = fechaHora,
                    lugar = lugar,
                    cuposMaximos = cuposMax,
                    cuposOcupados = cuposOcup,
                    codigoQr = qr,
                    estado = estado
                )
            )
        }
        return result
    }

    private fun parseUsuariosArray(arr: JSONArray): List<RemoteUserItem> {
        val result = mutableListOf<RemoteUserItem>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val roleRaw = obj.optString("role", "volunteer")
            val roleLabel = when (roleRaw.lowercase()) {
                "admin", "administrador" -> "Administrador General"
                "coordinator", "docente" -> "Docente / Coordinador"
                "volunteer", "voluntario" -> "Estudiante Voluntario"
                else -> "Beneficiario Comunitario"
            }
            result.add(
                RemoteUserItem(
                    id = obj.optString("id", "usr-$i"),
                    nombre = obj.optString("nombre", obj.optString("email", "Usuario")),
                    email = obj.optString("email", "usuario@duocuc.cl"),
                    role = roleLabel,
                    activo = obj.optBoolean("activo", true)
                )
            )
        }
        return result
    }
}
