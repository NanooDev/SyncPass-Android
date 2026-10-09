package com.example.kotlin_login.core.config

import com.example.kotlin_login.BuildConfig

object AppConfig {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL.trim().let {
        if (it.endsWith("/")) it else "$it/"
    }
}