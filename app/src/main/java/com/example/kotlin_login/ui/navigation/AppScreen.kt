package com.example.kotlin_login.ui.navigation

/**
 * Destinos de navegación tipados para el NavHost de Compose (SyncPass).
 */
sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Home : AppScreen("home")
    object Docente : AppScreen("docente")
    object Voluntario : AppScreen("voluntario")
    object Beneficiario : AppScreen("beneficiario")
    object Admin : AppScreen("admin")
}
