package com.example.kotlin_login.core.jwt

import com.google.gson.annotations.SerializedName

data class SessionUser(
    @SerializedName("cognito:username", alternate = ["username", "email"])
    val username: String = "",
    @SerializedName("cognito:groups", alternate = ["roles"])
    val roles: List<String> = emptyList(),
    val iss: String = "",
    val sub: String = "",
    val iat: Long = 0,
    val exp: Long = 0
) {
    val secondsRemaining: Long get() = exp - (System.currentTimeMillis() / 1000)
    val isExpired: Boolean get() = secondsRemaining <= 0

    val primaryRole: String get() {
        val lower = roles.map { it.lowercase() }
        return when {
            lower.contains("admin") || lower.contains("administrador") -> "admin"
            lower.contains("volunteer") || lower.contains("voluntario") -> "volunteer"
            lower.contains("attendee") || lower.contains("beneficiario") -> "attendee"
            lower.contains("coordinator") || lower.contains("coordinador") -> "coordinator"
            else -> "coordinator"
        }
    }
}