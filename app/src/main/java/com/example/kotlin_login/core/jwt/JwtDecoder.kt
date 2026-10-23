package com.example.kotlin_login.core.jwt

import android.util.Base64
import com.google.gson.Gson

object JwtDecoder {
    private val gson = Gson()

    fun decode(accessToken: String): Result<SessionUser> = runCatching {
        if (accessToken.startsWith("DEMO_")) {
            val roleSlug = accessToken.removePrefix("DEMO_")
            return@runCatching SessionUser(
                username = when (roleSlug) {
                    "coordinator" -> "profesor.coordinador@duocuc.cl"
                    "volunteer" -> "voluntario.estudiante@duocuc.cl"
                    "attendee" -> "beneficiario.comunitario@comunidad.cl"
                    "admin" -> "admin.syncpass@duocuc.cl"
                    else -> "usuario@duocuc.cl"
                },
                roles = listOf(roleSlug),
                iss = "https://syncpass.duocuc.cl",
                sub = "demo-user-id",
                iat = System.currentTimeMillis() / 1000,
                exp = (System.currentTimeMillis() / 1000) + 86400
            )
        }
        val parts = accessToken.split(".")
        require(parts.size == 3) { "Token sin formato JWT" }
        val json = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
        gson.fromJson(json, SessionUser::class.java)
    }

    fun createDemoToken(roleSlug: String): String = "DEMO_$roleSlug"
}
