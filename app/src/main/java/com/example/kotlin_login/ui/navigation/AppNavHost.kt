package com.example.kotlin_login.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kotlin_login.ui.admin.AdminScreen
import com.example.kotlin_login.ui.beneficiario.BeneficiarioScreen
import com.example.kotlin_login.ui.docente.DocenteScreen
import com.example.kotlin_login.ui.home.HomeScreen
import com.example.kotlin_login.ui.login.LoginScreen
import com.example.kotlin_login.ui.voluntario.VoluntarioScreen

/**
 * NavHost centralizado de Jetpack Compose para la aplicación móvil SyncPass.
 * Gestiona la pila de navegación y el acceso a los 4 roles del sistema.
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
                onNavigateToDocente = { navController.navigate(AppScreen.Docente.route) },
                onNavigateToVoluntario = { navController.navigate(AppScreen.Voluntario.route) },
                onNavigateToBeneficiario = { navController.navigate(AppScreen.Beneficiario.route) },
                onNavigateToAdmin = { navController.navigate(AppScreen.Admin.route) },
                onLogout = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Docente.route) {
            DocenteScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Voluntario.route) {
            VoluntarioScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Beneficiario.route) {
            BeneficiarioScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Admin.route) {
            AdminScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
