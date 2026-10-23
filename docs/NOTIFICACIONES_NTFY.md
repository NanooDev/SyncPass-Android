# Notificaciones Push con ntfy.sh y BFF Spring Boot (SyncPass Android)

La aplicacion SyncPass para Android recibe notificaciones push en tiempo real sin requerir Firebase ni Google Play Services, utilizando la infraestructura de **ntfy.sh** y el backend BFF **Spring Boot** (`fs2-api`).

La arquitectura se basa en suscripcion por **Server-Sent Events (SSE)** en segundo plano hacia un topico secreto derivado criptograficamente del correo electronico del usuario autenticado.

---

## 1. Algoritmo de Derivacion de Topico

Tanto el cliente Android (`NtfyTopics.kt`), la plataforma Web (`src/services/ntfy.ts`) y el backend BFF (`NotificationService.java`) comparten la misma logica:

1. Normalizacion del email: `email.trim().lowercase()`.
2. Calculo del hash **SHA-256** del email en codificacion UTF-8.
3. Extraccion de los primeros **20 caracteres hexadecimales**.
4. Concatenacion del prefijo `app_vibe_`.

Formato resultante:
`app_vibe_[20_caracteres_hex]`

### Ejemplos:
- `docente.dev@gmail.com` -> `app_vibe_8e63635b0c5401804b4b`
- `admin.syncpass@duocuc.cl` -> `app_vibe_...`

Implementado en: `com.example.kotlin_login.feature.notificaciones.NtfyTopics`.

---

## 2. Componentes de la Arquitectura Movil

1. **Permisos de Android (AndroidManifest.xml)**:
   - `android.permission.INTERNET`
   - `android.permission.POST_NOTIFICATIONS` (Android 13+ / API 33+)
   - `android.permission.FOREGROUND_SERVICE`
   - `android.permission.FOREGROUND_SERVICE_DATA_SYNC`

2. **Servicio en Primer Plano (NotificationService)**:
   - Declarado con `foregroundServiceType="dataSync"`.
   - Se inicia automaticamente en `MainActivity` al detectar una sesion activa.
   - Crea dos canales de notificacion:
     - `syncpass_foreground` (Importancia baja): Notificacion persistente requerida por el sistema operativo para mantener el socket SSE activo.
     - `syncpass_messages` (Importancia alta): Notificaciones emergentes visibles con sonido y vibracion al recibir eventos.
   - Se desconecta y finaliza limpiamente al cerrar sesion (`onLogout`).

3. **Publicador de Notificaciones (NtfySender)**:
   - `sendPushNotification(...)`: Emision directa hacia `https://ntfy.sh/<topico>`.
   - `sendPushNotificationViaBff(...)`: Emision a traves del endpoint BFF de Spring Boot `POST /api/notificar`.

---

## 3. Endpoints del Backend BFF (fs2-api)

El backend Spring Boot expone:
- `POST /api/notificar`: Emite la notificacion hacia ntfy.sh con titulo, cuerpo y prioridad.
- `GET /api/notificar/topic/{email}`: Retorna el topico secreto calculado para un usuario.

Ambos endpoints estan configurados con `permitAll()` en `SecurityConfig.java` para permitir pruebas sin restriccion de token.
