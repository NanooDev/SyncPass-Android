package com.example.kotlin_login.core.jwt

import android.util.Base64
import com.google.gson.Gson

object JwtDecoder {
    private val gson = Gson()

    fun decode(accessToken: String): Result<SessionUser> = runCatching {
        val parts = accessToken.split(".")
        require(parts.size == 3) { "Token sin formato JWT" }
        val json = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
        gson.fromJson(json, SessionUser::class.java)
    }
}
