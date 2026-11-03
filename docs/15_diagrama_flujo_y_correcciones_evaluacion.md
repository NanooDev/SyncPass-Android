# Diagrama de Flujo de Arquitectura, Base de Datos y Correcciones de Evaluación (SyncPass)

Este documento consolida el **Diagrama de Flujo de Interacción** basado en el repositorio oficial del docente (`https://github.com/docentedev/fs2-api/blob/main/docs/TROUBLESHOOTING.md`), la ubicación de la **Base de Datos PostgreSQL**, los **Mockups de Figma** y las respuestas técnicas exactas a las preguntas de evaluación.

---

## 1. Diagrama de Flujo General (Cómo Interactúan los Componentes)

Basado en `docs/TROUBLESHOOTING.md` y `docs/ARQUITECTURA.md` del repositorio `docentedev/fs2-api`:

```text
┌─────────────────────────────┐       ┌─────────────────────────────┐
│    Front React (Vercel)     │       │   App Android (Kotlin)      │
│  SPA con librería "wouter"  │       │ Jetpack Compose + ViewModel │
└──────────────┬──────────────┘       └──────────────┬──────────────┘
               │                                     │
               │        Peticiones HTTPS (Fetch /    │
               │        Retrofit + Header JWT)       │
               ▼                                     ▼
┌───────────────────────────────────────────────────────────────────┐
│                AWS API Gateway (HTTP API: 5xvspck6jb)             │
│  • Rutas públicas (sin Authorizer):                               │
│    GET /api/health, POST /api/auth/login, POST /api/auth/registro │
│  • Rutas protegidas (JWT Authorizer valida token con Cognito):    │
│    GET/POST/PUT/DELETE /api/talleres, GET/POST /api/usuarios      │
└─────────────────────────────────┬─────────────────────────────────┘
                                  │
                                  │ HTTP (Puerto 8080 / Proxy)
                                  ▼
┌───────────────────────────────────────────────────────────────────┐
│              Backend Spring Boot (AWS EC2 / Serverless)           │
│  • Controladores REST:                                            │
│    AuthController, UsuarioController, TallerController,           │
│    NotificationController                                         │
└─────────┬───────────────────────┼───────────────────────┬─────────┘
          │                       │                       │
          ▼                       ▼                       ▼
┌───────────────────┐   ┌───────────────────┐   ┌───────────────────┐
│    AWS Cognito    │   │  Base de Datos    │   │      ntfy.sh      │
│   (User Pool)     │   │   PostgreSQL      │   │ (Push SSE Móvil)  │
│ • Valida credenciales│ │ (Supabase / EC2)  │   │ • Solo se invoca  │
│ • Emite tokens JWT│   │ • JPA / Hibernate │   │   desde el backend│
│   (Id / Access)   │   │ • Tablas:         │   │   en POST         │
│                   │   │   usuarios,       │   │   /api/notificar  │
│                   │   │   usuario_roles,  │   │ • Topico SHA-256: │
│                   │   │   evento_taller,  │   │   app_vibe_<hash> │
│                   │   │   asistencia      │   │                   │
└───────────────────┘   └───────────────────┘   └───────────────────┘
```

---

## 2. Flujo Paso a Paso de un Mantenedor (Crear Taller en Web -> Ver en Móvil)

1. **En la Web (`DashboardPage.tsx`):**
   - El Docente completa el formulario `+ Nueva Iniciativa` y presiona **Guardar**.
   - El navegador ejecuta `fetch('/api/talleres', { method: 'POST', body: JSON.stringify(nuevoTaller) })`.
   - En **Chrome DevTools -> Network -> Fetch/XHR**, se observa únicamente la petición HTTP `POST /api/talleres` con respuesta `201 Created` (ya **no** se envía ninguna petición directa a `ntfy.sh` al crear un taller).
2. **En el Backend y Base de Datos:**
   - El endpoint `/api/talleres` recibe el JSON, le asigna ID y persiste el registro en la base de datos compartida.
3. **En la App Móvil (`SyncPass-Android` -> `DocenteScreen.kt`):**
   - El usuario inicia sesión **una sola vez** en `LoginScreen.kt` y `AppNavHost.kt` lo dirige directamente a `DocenteScreen.kt`.
   - Al abrir la pantalla (o presionar **Actualizar API**), `TallerRemoteRepository.fetchTalleres()` ejecuta `GET /api/talleres` y muestra inmediatamente el nuevo taller creado en la Web (y viceversa si se crea o elimina desde Android).

---

## 3. ¿Dónde está nuestra Base de Datos?

1. **Esquema Relacional (PostgreSQL / Supabase):**
   - El script DDL completo se encuentra en [`docs/syncpass-ddl.sql`](./syncpass-ddl.sql) con las 9 tablas del modelo relacional (`usuario`, `docente`, `estudiante_voluntario`, `beneficiario`, `proyecto_comunitario`, `evento_taller`, `registro_asistencia`, `encuesta_valoracion`, `sync_log`).
2. **Conexión en el Backend (`fs2-api`):**
   - En `src/main/resources/application.yml`, Spring Data JPA + Hibernate se conecta mediante `DATABASE_URL` (`jdbc:postgresql://...`) con `ddl-auto: update`, mapeando las entidades `@Entity` (`Usuario`, `Taller`).
3. **Persistencia Local en Android (Offline-First):**
   - En el dispositivo móvil se mantiene caché local para operar sin señal en terreno y sincronizar mediante `TallerRemoteRepository` hacia `/api/talleres` y `/api/usuarios`.

---

## 4. Respuestas Exactas a las Preguntas del Profesor

### A. ¿Cuál es la diferencia entre el `View` y el `ViewModel` en Android (Arquitectura MVVM)?
- **`View` (La Vista — ej. `LoginScreen.kt`, `DocenteScreen.kt`):**
  - Son las funciones `@Composable` de Jetpack Compose.
  - **Única responsabilidad:** Dibujar la interfaz gráfica en pantalla según el estado actual (`uiState`) y avisarle al `ViewModel` cuando el usuario toca un botón o escribe texto (ej. `viewModel.onLoginClick()`).
  - **Lo que NUNCA hace:** No contiene lógica de negocio, no valida reglas complejas ni llama directamente a Retrofit o a la base de datos. Si rotas la pantalla del teléfono, la Vista se destruye y se vuelve a dibujar.
- **`ViewModel` (El Modelo de Vista — ej. `LoginViewModel.kt`, `HomeViewModel.kt`):**
  - Es una clase que hereda de `androidx.lifecycle.ViewModel`.
  - **Única responsabilidad:** Mantener el estado de la pantalla (`StateFlow<UiState>`) y ejecutar la lógica de negocio llamando al `Repository` dentro de corrutinas (`viewModelScope.launch { ... }`).
  - **Ventaja clave:** **Sobrevive a los cambios de configuración** (como rotar el celular) y no tiene ninguna referencia a componentes visuales de Android.

### B. ¿Cuál es la librería que usamos para el enrutamiento (navegación) en React Web?
- **`wouter`** (declarada en `package.json`: `"wouter": "^3.13.0"` y usada en `src/App.tsx`: `import { Route, Switch, Link, useLocation } from 'wouter'`).
- **¿Por qué usamos `wouter` en lugar de `react-router-dom`?**
  - Porque `wouter` es la librería minimalista de enrutamiento para React recomendada por el profesor en `fs2-web`: pesa menos de `2 KB`, funciona con Hooks (`useLocation`) y componentes `<Switch>` y `<Route path="/dashboard">`, permitiendo cambiar de vista en una SPA (Single Page Application) sin recargar el navegador.

---

## 5. Ubicación de los Mockups de Figma

Los mockups interactivos de las 4 vistas por rol están disponibles en:
- [`docs/mockups/syncpass-figma-mockups.html`](./mockups/syncpass-figma-mockups.html)
