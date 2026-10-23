# 04 — Navegación con NavHost en Jetpack Compose

> Documentación de arquitectura de navegación para SyncPass (Desarrollo Móvil / Duoc UC).
> Este documento registra el diseño y la implementación del enrutador centralizado mediante `NavHost`.

---

## 1. Objetivo y Justificación

En fases iniciales, la navegación entre `Login` y `Home` se realizaba mediante condicionales en el estado (`if (token == null) ... else ...`).

A medida que el proyecto escala para soportar los **4 Roles** del caso SyncPass (Docente, Voluntario, Beneficiario, Administrador) y múltiples flujos (Escáner QR, Encuestas, Sincronización), se migró a la solución estándar recomendada por Google: **`androidx.navigation.compose.NavHost`**.

---

## 2. Dependencias (`gradle/libs.versions.toml`)

```toml
[versions]
navigationCompose = "2.8.8"

[libraries]
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
```

En `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation(libs.androidx.navigation.compose)
}
```

---

## 3. Rutas y Destinos (`ui/navigation/AppScreen.kt`)

Las rutas se definen mediante una clase sellada (`sealed class`) inmutable para evitar strings sueltos (magic strings):

```kotlin
sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Home : AppScreen("home")
    object Docente : AppScreen("docente")
    object Voluntario : AppScreen("voluntario")
    object Beneficiario : AppScreen("beneficiario")
    object Admin : AppScreen("admin")
}
```

---

## 4. Componente NavHost (`ui/navigation/AppNavHost.kt`)

El `AppNavHost` desacopla la definición de pantallas del controlador de ciclo de vida (`MainActivity`), permitiendo pruebas unitarias y previsualizaciones limpias:

```kotlin
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
            HomeScreen(
                accessToken = token ?: "",
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
```

---

## 5. Integración con Amazon Cognito y API REST

En `MainActivity.kt`:
1. Se inicializa el `rememberNavController()`.
2. Se gestiona `CognitoAuthViewModel` con launchers de `ActivityResultContracts.StartActivityForResult()`.
3. Al recibir un token exitoso (desde el formulario tradicional o desde el redirect de Cognito con AppAuth), se dispara la navegación hacia `Home` limpiando la pila anterior (`popUpTo(Login) { inclusive = true }`).
4. Al pulsar "Cerrar sesión", el `onLogout` borra el token en memoria, ejecuta el cierre de sesión en Cognito (si aplica) y navega de vuelta a `Login`.
