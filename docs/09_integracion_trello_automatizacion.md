# Guía de Integración y Automatización con Trello — SyncPass

> **Proyecto:** SyncPass (Gestión Ágil de Requerimientos)  
> **Herramienta:** Trello REST API v1  
> **Autor:** Mariano Acevedo ([@NanooDev](https://github.com/NanooDev))

---

## 1. Estructura del Tablero Kanban Recomendado

Para reflejar una gestión profesional ante el profesor y cumplir con las buenas prácticas de desarrollo ágil, el tablero de Trello debe contar con las siguientes listas:

1. **Backlog de Requerimientos:** Requerimientos solicitados por la cátedra y especificación del caso SyncPass.
2. **Arquitectura y Modelado:** Separación de capas, esquemas DDL de base de datos y matriz de roles RBAC.
3. **Desarrollo Móvil (Android / Kotlin):** Vistas Compose, módulo de escáner QR y modo offline.
4. **Desarrollo Web (React 19 / TypeScript):** Portal institucional, inicio de sesión y panel por roles.
5. **Infraestructura Cloud (AWS):** User Pool de Cognito, App Clients y despliegue de la API en EC2.
6. **Completado / Verificado:** Tareas probadas y documentadas listas para la evaluación.

---

## 2. Cómo Obtener tus Credenciales de Trello (2 minutos)

1. Inicia sesión en [Trello](https://trello.com/).
2. Entra a [Trello Power-Ups Admin](https://trello.com/power-ups/admin).
3. Haz clic en **Nuevo** (o *New Power-Up/Integration*).
   - Nombre: `SyncPass-Automation`
   - Workspace: Selecciona tu espacio de trabajo personal.
4. Haz clic en **Generar una nueva clave de API**. Copia tu **API Key**.
5. Justo al lado del campo de la API Key, haz clic en el enlace que dice **Generar un token manualmente** (Token).
6. Autoriza el acceso y copia el **Token** resultante (es una cadena larga de letras y números).

---

## 3. Script de Automatización para Crear Tarjetas en Trello

He preparado el siguiente script en **PowerShell** (`scripts/sync_trello.ps1`) para que, una vez que ingreses tu API Key y Token, cree automáticamente todas las tarjetas del proyecto en tu tablero de Trello:

```powershell
# scripts/sync_trello.ps1
param (
    [Parameter(Mandatory=$true)]
    [string]$ApiKey,
    
    [Parameter(Mandatory=$true)]
    [string]$Token,
    
    [Parameter(Mandatory=$true)]
    [string]$BoardId
)

$headers = @{ "Content-Type" = "application/json" }

# 1. Obtener las listas del tablero
$listsUrl = "https://api.trello.com/1/boards/$BoardId/lists?key=$ApiKey&token=$Token"
$lists = Invoke-RestMethod -Uri $listsUrl -Method Get

$backlogList = $lists | Where-Object { $_.name -like "*Backlog*" -or $_.name -like "*Por hacer*" } | Select-Object -First 1

if (-not $backlogList) {
    Write-Host "Creando lista de Backlog en el tablero..."
    $createListUrl = "https://api.trello.com/1/boards/$BoardId/lists?name=Backlog%20de%20Requerimientos&key=$ApiKey&token=$Token"
    $backlogList = Invoke-RestMethod -Uri $createListUrl -Method Post
}

# 2. Tarjetas clave a generar
$cards = @(
    @{ name = "[ARQ] Separación en capas (Data, Domain, Presentation) en Android"; desc = "Garantizar que no existan llamadas a red en controladores ni lógica en composables." },
    @{ name = "[AUTH] Integración AWS Cognito con App Client y PKCE"; desc = "Configurar User Pool us-east-1_RlB8Ft9Z2 con Authorization Code grant para móvil y web." },
    @{ name = "[ROLES] Implementación de 4 perfiles RBAC (Docente, Voluntario, Beneficiario, Admin)"; desc = "Permitir el acceso y prueba de cada rol tanto en Android como en React." },
    @{ name = "[QR] Componentes modulares de generación y lectura QR"; desc = "Componente QrCodeView para visualización y QrScannerView para escaneo en terreno." },
    @{ name = "[OFFLINE] Modo Acreditación en Terreno sin conexión"; desc = "Persistencia local de asistencias y sincronización automática al recuperar red." },
    @{ name = "[SURVEY] Módulo de encuestas inmediatas de satisfacción"; desc = "Formulario de 30 segundos con calificación 1 a 5 y captura de percepción comunitaria." },
    @{ name = "[CLOUD] Despliegue de la API Java (fs2-api) en AWS EC2"; desc = "Aprovisionar instancia Ubuntu, configurar Java 21, systemd daemon y Nginx reverse proxy." },
    @{ name = "[DOCS] Documentación técnica de arquitectura, identidad visual y casos de uso"; desc = "Completar suite de documentación en docs/ para la entrega evaluada." }
)

foreach ($c in $cards) {
    $cardUrl = "https://api.trello.com/1/cards?idList=$($backlogList.id)&name=$([uri]::EscapeDataString($c.name))&desc=$([uri]::EscapeDataString($c.desc))&key=$ApiKey&token=$Token"
    $res = Invoke-RestMethod -Uri $cardUrl -Method Post
    Write-Host "Tarjeta creada: $($c.name)"
}

Write-Host "Sincronización con Trello completada con éxito."
```

---

## 4. Instrucciones para Ejecutarlo

1. Para encontrar el `BoardId` de tu tablero:
   - Abre tu tablero en el navegador (ejemplo: `https://trello.com/b/AbCd1234/syncpass`).
   - El código de 8 caracteres entre `/b/` y `/syncpass` (`AbCd1234`) es tu Board ID.
2. Ejecuta en PowerShell:
   ```powershell
   cd C:\dev\android\SyncPass-Android
   .\scripts\sync_trello.ps1 -ApiKey "TU_API_KEY" -Token "TU_TOKEN" -BoardId "TU_BOARD_ID"
   ```
Todas las historias de usuario y tareas de arquitectura quedarán automáticamente creadas y organizadas en tu tablero.
