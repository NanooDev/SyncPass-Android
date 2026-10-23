package com.example.kotlin_login.feature.notificaciones

import com.example.kotlin_login.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Utilidad para publicar notificaciones push directamente en ntfy.sh
 * o a traves del endpoint BFF de Spring Boot (/api/notificar).
 */
object NtfySender {

    private val client = OkHttpClient()

    /**
     * Envia la notificacion directamente a ntfy.sh usando el topico derivado.
     */
    suspend fun sendPushNotification(
        email: String,
        title: String,
        message: String,
        priority: String = "high"
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val topic = NtfyTopics.generate(email)
            val url = "${BuildConfig.NTFY_BASE_URL.trimEnd('/')}/$topic"

            val body = message.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(url)
                .header("Title", title)
                .header("Priority", priority)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Error ntfy HTTP ${response.code}: ${response.message}")
                }
                true
            }
        }
    }

    /**
     * Envia la notificacion a traves del backend BFF de Spring Boot (/api/notificar).
     */
    suspend fun sendPushNotificationViaBff(
        email: String,
        title: String,
        message: String,
        priority: Int = 4
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val url = "${BuildConfig.API_BASE_URL.trimEnd('/')}/api/notificar"
            val json = JSONObject().apply {
                put("email", email)
                put("title", title)
                put("message", message)
                put("priority", priority)
            }.toString()

            val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Error BFF HTTP ${response.code}: ${response.message}")
                }
                true
            }
        }
    }
}
