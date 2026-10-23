param (
    [Parameter(Mandatory=$true)]
    [string]$ApiKey,
    
    [Parameter(Mandatory=$true)]
    [string]$Token,
    
    [Parameter(Mandatory=$true)]
    [string]$BoardId
)

$headers = @{ "Content-Type" = "application/json" }

Write-Host "Consultando listas del tablero $BoardId..."
$listsUrl = "https://api.trello.com/1/boards/$BoardId/lists?key=$ApiKey&token=$Token"
try {
    $lists = Invoke-RestMethod -Uri $listsUrl -Method Get
} catch {
    Write-Error "Error al conectar con Trello: $_"
    exit 1
}

$backlogList = $lists | Where-Object { $_.name -like "*Backlog*" -or $_.name -like "*Por hacer*" -or $_.name -like "*To Do*" } | Select-Object -First 1

if (-not $backlogList) {
    Write-Host "Creando lista de Backlog en el tablero..."
    $createListUrl = "https://api.trello.com/1/boards/$BoardId/lists?name=Backlog%20SyncPass&key=$ApiKey&token=$Token"
    $backlogList = Invoke-RestMethod -Uri $createListUrl -Method Post
}

$cards = @(
    @{ name = "[ARQ] Separación de capas (Data, Domain, Presentation) en Android"; desc = "Garantizar clean architecture y MVVM con desacoplamiento total de configuraciones." },
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
    try {
        $res = Invoke-RestMethod -Uri $cardUrl -Method Post
        Write-Host "Tarjeta creada: $($c.name)"
    } catch {
        Write-Warning "No se pudo crear tarjeta: $($c.name) ($($_))"
    }
}

Write-Host "Sincronización con Trello finalizada con éxito."
