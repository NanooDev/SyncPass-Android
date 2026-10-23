# Notificaciones Push con ntfy.sh (SyncPass Android)

La aplicacion SyncPass para Android recibe notificaciones push en tiempo real sin requerir Firebase ni Google Play Services, utilizando la infraestructura abierta de **ntfy.sh**.

La arquitectura se basa en suscripcion por **Server-Sent Events (SSE)** en segundo plano hacia un topico secreto derivado criptograficamente del correo electronico del usuario autenticado.

---

## 1. Algoritmo de Derivacion de Topico

Para garantizar aislamiento y privacidad entre los usuarios del sistema sin configuraciones complejas:

1. Normalizacion del email: `email.trim().lowercase()`.
2. Calculo del hash **SHA-256** del email en codificacion UTF-8.
3. Extraccion de los primeros **20 caracteres hexadecimales**.
4. Concatenacion del prefijo `app_vibe_`.

Formato resultante:
`app_vibe_[20_caracteres_hex]`

### Ejemplos:
- `docente.coordinador@duocuc.cl` -> `app_vibe_a8f9c...`
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
   - Utilidad HTTP para emitir eventos hacia `https://ntfy.sh/<topico>` con cabeceras `Title` y `Priority`.

---

## 3. Pruebas y Validacion

### A. Desde la Aplicacion Movil (Pantalla Admin / Docente):
Ingresar con rol Administrador y presionar el boton **Emitir Notificacion Push de Prueba**. El servicio SSE capturara el evento de inmediato y desplegara la alerta en la barra de estado de Android.

### B. Mediante cURL directo en terminal:
```bash
curl -H "Title: SyncPass Alerta" \
     -H "Priority: high" \
     -d "Acreditacion registrada para taller de alfabetizacion digital" \
     https://ntfy.sh/app_vibe_a8f9c12e4d567890abcd
```

### C. Desde la Plataforma Web SyncPass:
El modulo Web incorpora la funcion `sendPushNotification` que calcula el topico del destinatario y emite la alerta hacia ntfy.sh via HTTP POST.
