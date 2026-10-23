package com.example.kotlin_login

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cognitoauth.CognitoAuthManager
import com.cognitoauth.CognitoAuthViewModel
import com.example.kotlin_login.core.config.CognitoAppConfig
import com.example.kotlin_login.core.jwt.JwtDecoder
import com.example.kotlin_login.feature.notificaciones.NotificationService
import com.example.kotlin_login.ui.navigation.AppNavHost
import com.example.kotlin_login.ui.navigation.AppScreen
import com.example.kotlin_login.ui.theme.KotlinloginTheme

/**
 * MainActivity con arquitectura MVVM, soporte para Amazon Cognito (Hosted UI / PKCE)
 * y navegación centralizada mediante Jetpack Compose NavHost.
 */
class MainActivity : ComponentActivity() {

    private val cognitoManager: CognitoAuthManager? by lazy {
        if (CognitoAppConfig.isConfigured) CognitoAuthManager(this, CognitoAppConfig.config)
        else null
    }

    private val cognitoVm: CognitoAuthViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val manager = cognitoManager
                    ?: throw IllegalStateException("Cognito no configurado (.env)")
                return CognitoAuthViewModel(manager) as T
            }
        }
    }

    private val authLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res -> cognitoVm.onAuthResponse(res.data) }

    private val logoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { cognitoVm.onLogoutCompleted() }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permiso concedido o denegado por el usuario */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (!CognitoAppConfig.isConfigured) {
            Toast.makeText(
                this,
                "Cognito no configurado: completa .env (ver docs/03_cognito.md)",
                Toast.LENGTH_LONG,
            ).show()
        }
        enableEdgeToEdge()
        setContent {
            KotlinloginTheme {
                val navController = rememberNavController()
                var token: String? by rememberSaveable { mutableStateOf<String?>(null) }
                val cognitoState by cognitoVm.uiState.collectAsState()

                // Sincronizar servicio en segundo plano de notificaciones push ntfy.sh
                LaunchedEffect(token) {
                    val currentToken = token
                    if (!currentToken.isNullOrBlank()) {
                        val sessionUser = JwtDecoder.decode(currentToken).getOrNull()
                        val email = sessionUser?.username?.takeUnless { it.isBlank() }
                            ?: "profesor.coordinador@duocuc.cl"
                        NotificationService.start(this@MainActivity, email)
                    } else {
                        NotificationService.stop(this@MainActivity)
                    }
                }

                // Intents pendientes de Cognito para abrir Custom Tabs en el navegador
                LaunchedEffect(cognitoState.pendingAuthIntent) {
                    cognitoState.pendingAuthIntent?.let { intent ->
                        runCatching { authLauncher.launch(intent) }
                            .onFailure { e -> cognitoVm.reportError("Cognito: error al abrir navegador (${e.message})") }
                        cognitoVm.consumeAuthIntent()
                    }
                }

                LaunchedEffect(cognitoState.pendingLogoutIntent) {
                    cognitoState.pendingLogoutIntent?.let { intent ->
                        runCatching { logoutLauncher.launch(intent) }
                            .onFailure { e -> cognitoVm.reportError("Cognito: error al abrir navegador (${e.message})") }
                        cognitoVm.consumeLogoutIntent()
                    }
                }

                // Al completar auth en Cognito con éxito, sincronizar token y navegar a Home
                LaunchedEffect(cognitoState.tokens) {
                    cognitoState.tokens?.accessToken?.let { cognitoToken ->
                        token = cognitoToken
                        navController.navigate(AppScreen.Home.route) {
                            popUpTo(AppScreen.Login.route) { inclusive = true }
                        }
                    }
                }

                // NavHost de Compose
                AppNavHost(
                    navController = navController,
                    token = token,
                    isCognitoLoading = cognitoState.isLoading,
                    onCognitoClick = { cognitoVm.startLogin() },
                    onLoginSuccess = { newToken ->
                        token = newToken
                    },
                    onLogout = {
                        NotificationService.stop(this@MainActivity)
                        token = null
                        if (cognitoState.tokens != null) {
                            cognitoVm.startLogout()
                        }
                    }
                )
            }
        }
    }
}