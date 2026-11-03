package com.example.kotlin_login.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kotlin_login.core.jwt.JwtDecoder
import com.example.kotlin_login.ui.admin.AdminScreen
import com.example.kotlin_login.ui.beneficiario.BeneficiarioScreen
import com.example.kotlin_login.ui.docente.DocenteScreen
import com.example.kotlin_login.ui.home.HomeScreen
import com.example.kotlin_login.ui.login.LoginScreen
import com.example.kotlin_login.ui.voluntario.VoluntarioScreen

/**
 * NavHost centralizado de Jetpack Compose para la aplicación móvil SyncPass.
 * Enruta directamente desde Login hacia el módulo correspondiente al rol del usuario,
 * evitando solicitar la selección de rol más de una vez.
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
        startDestination = AppScreen.Login.route,
        modifier = modifier
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                isCognitoLoading = isCognitoLoading,
                onCognitoClick = onCognitoClick,
                onLoginSuccess = { newToken ->
                    onLoginSuccess(newToken)
                    val user = JwtDecoder.decode(newToken).getOrNull()
                    val targetRoute = when (user?.roles?.firstOrNull()?.lowercase()) {
                        "coordinator", "docente" -> AppScreen.Docente.route
                        "volunteer", "voluntario" -> AppScreen.Voluntario.route
                        "attendee", "beneficiario", "alumno", "estudiante" -> AppScreen.Beneficiario.route
                        "admin", "administrador" -> AppScreen.Admin.route
                        else -> AppScreen.Docente.route
                    }
                    navController.navigate(targetRoute) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                },
                onRoleSelected = { newToken, targetRoute ->
                    onLoginSuccess(newToken)
                    navController.navigate(targetRoute) {
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
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Docente.route) {
            DocenteScreen(
                onBackClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLogoutClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Voluntario.route) {
            VoluntarioScreen(
                onBackClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLogoutClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Beneficiario.route) {
            BeneficiarioScreen(
                onBackClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLogoutClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Admin.route) {
            AdminScreen(
                onBackClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLogoutClick = {
                    onLogout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
