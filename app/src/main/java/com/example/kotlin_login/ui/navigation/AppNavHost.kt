package com.example.kotlin_login.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kotlin_login.ui.home.HomeScreen
import com.example.kotlin_login.ui.login.LoginScreen

/**
 * NavHost centralizado de Jetpack Compose para la aplicación móvil SyncPass.
 * Gestiona la pila de navegación, transiciones y paso de tokens de sesión.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    token: String?,
    isCognitoLoading: Boolean = false,
    onCognitoClick: () -> Unit = {},
    onLoginSuccess: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if (token == null) AppScreen.Login.route else AppScreen.Home.route,
        modifier = modifier
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                isCognitoLoading = isCognitoLoading,
                onCognitoClick = onCognitoClick,
                onLoginSuccess = { newToken ->
                    onLoginSuccess(newToken)
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Home.route) {
            val currentToken = token ?: ""
            HomeScreen(
                accessToken = currentToken,
                onLogout = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
