package com.example.kotlin_login.ui.home

import com.example.kotlin_login.core.jwt.SessionUser

data class HomeUiState(
    val user: SessionUser? = null,
    val decodeError: String? = null
)
