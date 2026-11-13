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
 * Consume la API REST (/api/talleres y /api/usuarios) conectada directamente a PostgreSQL (Supabase).
 */
object TallerRemoteRepository {

    private const val WEB_API_BASE = "https://sync-pass-web-rho.vercel.app"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .build()

    val initialTalleres = listOf(
        TallerEvento(
            id = "00000000-0000-0000-0002-000000000001",
            proyectoId = "00000000-0000-0000-0001-000000000001",
            nombre = "Taller: Uso de WhatsApp y videollamadas",
            fechaHora = "15 oct, 09:00 hrs",
            lugar = "Sede Social Villa El Roble",
            cuposMaximos = 40,
            cuposOcupados = 0,
            codigoQr = "EVT-2026-001",
            estado = "En curso"
        )
    )

    suspend fun fetchTalleres(): List<TallerEvento> = withContext(Dispatchers.IO) {
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
        initialTalleres
    }

    suspend fun createTaller(
        nombre: String,
        lugar: String,
        fechaHora: String,
        cuposMaximos: Int,
        currentList: List<TallerEvento>
    ): List<TallerEvento> = withContext(Dispatchers.IO) {
        runCatching {
            val payload = JSONObject().apply {
                put("name", nombre.trim())
                put("location", lugar.trim())
                put("date", fechaHora.trim())
                put("capacity", cuposMaximos)
                put("status", "Activo")
            }
            val req = Request.Builder()
                .url("$WEB_API_BASE/api/talleres")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()
            httpClient.newCall(req).execute().use { res ->
                if (res.isSuccessful) {
                    val json = JSONObject(res.body?.string().orEmpty())
                    val arr = json.optJSONArray("talleres")
                    if (arr != null && arr.length() > 0) {
                        return@withContext parseTalleresArray(arr)
                    }
                }
            }
        }
        currentList
    }

    suspend fun deleteTaller(
        tallerId: String,
        currentList: List<TallerEvento>
    ): List<TallerEvento> = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder()
                .url("$WEB_API_BASE/api/talleres?id=$tallerId")
                .delete()
                .build()
            httpClient.newCall(req).execute().use { res ->
                if (res.isSuccessful) {
                    val json = JSONObject(res.body?.string().orEmpty())
                    val arr = json.optJSONArray("talleres")
                    if (arr != null) {
                        return@withContext parseTalleresArray(arr)
                    }
                }
            }
        }
        currentList.filter { it.id != tallerId }
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

        listOf(
            RemoteUserItem("00000000-0000-0000-0000-000000000001", "Nelson Melo (Ficticio)", "nmelo.ficticio@duocuc.cl", "Docente / Coordinador", true),
            RemoteUserItem("00000000-0000-0000-0000-000000000002", "Mariano R. (Ficticio)", "mrodzf.ficticio@duocuc.cl", "Estudiante Voluntario", true)
        )
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
