# ==============================================================================
# Script de Commit - Álvaro León (Offline Mode, Escáner QR, Beneficiario y DevOps)
# ==============================================================================

Write-Host "=== Staging y Commit de Álvaro León (alv.leon@duocuc.cl) ===" -ForegroundColor Cyan

# Configurar identidad de Álvaro
git config --local user.name "Alvaro Leon"
git config --local user.email "alv.leon@duocuc.cl"

# Staging de archivos asignados a Álvaro
git add app/src/main/java/com/example/kotlin_login/ui/components/QrComponents.kt
git add app/src/main/java/com/example/kotlin_login/ui/voluntario/VoluntarioScreen.kt
git add app/src/main/java/com/example/kotlin_login/ui/beneficiario/BeneficiarioScreen.kt
git add scripts/deploy_ec2.sh
git add scripts/sync_trello.ps1
git add scripts/commit_mariano.ps1
git add scripts/commit_alvaro.ps1
git add docs/08_plan_despliegue_ec2_api.md
git add docs/09_integracion_trello_automatizacion.md
git add docs/10_guia_colaboracion_dupla_git.md

# Commit
git commit -m "feat(offline): implementar modo sin conexion, componentes QR, encuestas y scripts devops"

Write-Host "Commit de Alvaro completado exitosamente!" -ForegroundColor Green
