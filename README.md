# SyncPass Móvil — Sistema de Acreditación y Evaluación Territorial

> **Institución:** Duoc UC • Escuela de Informática y Telecomunicaciones  
> **Asignatura:** DSY1105 · Desarrollo de Aplicaciones Móviles  
> **Integrantes (Dupla de Trabajo):**  
> - Mariano Acevedo ([@NanooDev](https://github.com/NanooDev) · `marianoacevedo1809@gmail.com`)  
> - Álvaro León (`alv.leon@duocuc.cl`)  
> **Versión:** 2.0.0

---

## 1. Descripción del Proyecto

**SyncPass Móvil** es la aplicación nativa para Android desarrollada en **Kotlin y Jetpack Compose** para el ecosistema de **Vinculación con el Medio de Duoc UC**. Permite a los docentes y estudiantes voluntarios coordinar talleres comunitarios, acreditar a los beneficiarios en segundos mediante códigos QR (incluso sin conexión a internet) y capturar encuestas inmediatas de satisfacción en sedes sociales, colegios y juntas de vecinos.

---

## 2. Arquitectura de Software

La aplicación sigue los principios de **Clean Architecture y MVVM (Model-View-ViewModel)** recomendados por Google:

```text
com.example.kotlin_login/
├── core/                   # Núcleo de la aplicación
│   ├── config/             # Configuración desacoplada (BuildConfig / .env)
│   └── jwt/                # Decodificador JWT y modelo SessionUser
├── data/                   # Capa de Datos (Data Layer)
│   ├── remote/             # Retrofit 2, OkHttp, DTOs y Endpoints REST
│   └── repository/         # Implementación de repositorios de autenticación
├── domain/                 # Capa de Dominio (Domain Layer)
│   └── model/              # Modelos de negocio (UserRole, TallerEvento, etc.)
├── ui/                     # Capa de Presentación (Presentation Layer)
│   ├── admin/              # Pantalla Administrador General (AdminScreen)
│   ├── beneficiario/       # Pantalla Beneficiario Comunitario (BeneficiarioScreen)
│   ├── components/         # Componentes modulares (QrCodeView, QrScannerView, TopBar, Card)
│   ├── docente/            # Pantalla Docente / Coordinador (DocenteScreen)
│   ├── home/               # Hub central de módulos (HomeScreen)
│   ├── login/              # Portal de acceso con Cognito y demo (LoginScreen)
│   ├── navigation/         # Enrutamiento tipado (AppNavHost, AppScreen)
│   ├── theme/              # Paleta cromática clara oficial y tipografía
│   └── voluntario/         # Pantalla Acreditación en Terreno (VoluntarioScreen)
└── MainActivity.kt         # Punto de entrada y observador de ciclo de vida
```

---

## 3. Matriz de Roles Implementados (RBAC)

1. **Docente / Coordinador:** Monitoreo de quórum en tiempo real, administración de talleres y generación de pases QR para proyección o impresión.
2. **Estudiante Voluntario:** Acreditación en terreno con escáner de códigos QR, soporte completo Offline-First y botón de sincronización diferida con la nube.
3. **Beneficiario Comunitario:** Despliegue de pase digital personal QR y módulo de encuesta inmediata de satisfacción ciudadana de 30 segundos.
4. **Administrador General:** Indicadores globales de impacto territorial y auditoría de accesos y roles de usuario.

---

## 4. Índice de Documentación Técnica (`docs/`)

Toda la documentación técnica exigida por la cátedra se encuentra en la carpeta [`docs/`](./docs/):

- [**`docs/05_identidad_visual_y_colores.md`**](./docs/05_identidad_visual_y_colores.md): Especificación de la paleta cromática clara, sobria, sin emojis y justificación de diseño.
- [**`docs/06_roles_y_pantallas_syncpass.md`**](./docs/06_roles_y_pantallas_syncpass.md): Detalle técnico de cada pantalla, componentes QR y navegación NavHost.
- [**`docs/07_explicacion_cognito_app_client.md`**](./docs/07_explicacion_cognito_app_client.md): Justificación técnica de por qué se requiere un App Client dedicado en AWS Cognito para la app móvil.
- [**`docs/08_plan_despliegue_ec2_api.md`**](./docs/08_plan_despliegue_ec2_api.md): Plan paso a paso para desplegar la API Java (`fs2-api`) en un servidor AWS EC2 con systemd y Nginx.
- [**`docs/09_integracion_trello_automatizacion.md`**](./docs/09_integracion_trello_automatizacion.md): Guía de integración de Trello y script automatizado (`scripts/sync_trello.ps1`).
- [**`docs/10_guia_colaboracion_dupla_git.md`**](./docs/10_guia_colaboracion_dupla_git.md): Estrategia de colaboración en dupla, división de módulos y comandos de autoría Git para Mariano Acevedo y Álvaro León.
- [**`docs/caso.md`**](./docs/caso.md): Formato oficial del caso Duoc UC y problemática en terreno.
- [**`docs/syncpass-ddl.sql`**](./docs/syncpass-ddl.sql): Modelo relacional DDL de la base de datos de vinculación.

---

## 5. Modo de Evaluación y Demostración Rápida

Para facilitar la revisión por parte de la comisión docente sin depender de servicios externos:
1. Al abrir la aplicación en el emulador o dispositivo físico, la pantalla de inicio de sesión dispone de una sección:
   **ACCESO DIRECTO POR ROL (EVALUACIÓN)**
2. Presione cualquiera de los botones (**Docente**, **Voluntario**, **Beneficiario**, **Admin**) para ingresar de forma inmediata al perfil deseado con todas sus capacidades operativas activas.
3. También puede utilizar el botón **Continuar con Amazon Cognito** para validar el flujo federado oficial vía navegador web.
