# ==============================================================================
# Script de Commit - Mariano Acevedo (Arquitectura, Cognito y Docente)
# ==============================================================================

Write-Host "=== Staging y Commit de Mariano Acevedo (NanooDev) ===" -ForegroundColor Cyan

# Configurar identidad de Mariano
git config --local user.name "NanooDev"
git config --local user.email "marianoacevedo1809@gmail.com"

# Staging de archivos base, arquitectura, colores, navegación y docente
git add README.md
git add app/src/main/java/com/example/kotlin_login/core/jwt/JwtDecoder.kt
git add app/src/main/java/com/example/kotlin_login/domain/model/UserRole.kt
git add app/src/main/java/com/example/kotlin_login/domain/model/SyncPassModels.kt
git add app/src/main/java/com/example/kotlin_login/ui/theme/Color.kt
git add app/src/main/java/com/example/kotlin_login/ui/theme/Theme.kt
git add app/src/main/java/com/example/kotlin_login/ui/components/SyncPassTopBar.kt
git add app/src/main/java/com/example/kotlin_login/ui/navigation/AppNavHost.kt
git add app/src/main/java/com/example/kotlin_login/ui/login/LoginScreen.kt
git add app/src/main/java/com/example/kotlin_login/ui/home/HomeScreen.kt
git add app/src/main/java/com/example/kotlin_login/ui/docente/DocenteScreen.kt
git add app/src/main/java/com/example/kotlin_login/ui/admin/AdminScreen.kt
git add docs/04_navhost.md
git add docs/05_identidad_visual_y_colores.md
git add docs/06_roles_y_pantallas_syncpass.md
git add docs/07_explicacion_cognito_app_client.md

# Commit
git commit -m "feat(auth): implementar inicio de sesion con AWS Cognito, navegacion NavHost y vistas de docente"

Write-Host "Commit de Mariano completado exitosamente!" -ForegroundColor Green
