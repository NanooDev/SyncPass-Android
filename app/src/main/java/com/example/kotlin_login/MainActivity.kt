package com.example.kotlin_login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.kotlin_login.ui.home.HomeScreen
import com.example.kotlin_login.ui.login.LoginScreen
import com.example.kotlin_login.ui.theme.KotlinloginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinloginTheme {
                var token: String? by rememberSaveable { mutableStateOf<String?>(null) }
                val currentToken: String? = token
                if (currentToken == null) {
                    LoginScreen(onLoginSuccess = { t -> token = t })
                } else {
                    HomeScreen(accessToken = currentToken, onLogout = { token = null })
                }
            }
        }
    }
}