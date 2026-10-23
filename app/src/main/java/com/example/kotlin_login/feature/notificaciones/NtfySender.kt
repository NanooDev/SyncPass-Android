package com.example.kotlin_login.feature.notificaciones

import com.example.kotlin_login.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Utilidad para publicar notificaciones push en ntfy.sh directamente desde Android o pruebas.
 */
object NtfySender {

    private val client = OkHttpClient()

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
}
