package com.example.kotlin_login.data.repository

import com.example.kotlin_login.data.remote.AuthApiService
import com.example.kotlin_login.data.remote.LoginRequest
import com.example.kotlin_login.data.remote.LoginResponse

class AuthRepository(private val api: AuthApiService) {
    suspend fun login(username: String, password: String): Result<LoginResponse> =
        runCatching {
            api.login(LoginRequest(username.trim(), password))
        }
}
