# ¿De qué trata SyncPass?

**SyncPass** es un sistema de acreditación digital y vinculación comunitaria desarrollado para **Duoc UC**, en el marco de las asignaturas *DSY1105 – Fullstack 2* y *Desarrollo Móvil*.

Su finalidad es digitalizar, organizar y dar trazabilidad al registro de asistencia en actividades de vinculación comunitaria, permitiendo que docentes, estudiantes voluntarios, beneficiarios y administradores gestionen estos procesos desde una aplicación móvil, con soporte tanto **online** como **offline**.

## Propósito del proyecto

El objetivo principal es facilitar el control y seguimiento de proyectos comunitarios mediante:

- **Acreditación digital** mediante códigos QR para agilizar el ingreso y registro de asistencia.
- **Trazabilidad** de quién participa, en qué actividad, cuándo y desde qué dispositivo.
- **Operación en terreno**: gracias al modo offline-first, se puede registrar asistencia incluso sin conexión a internet.
- **Centralización** de la información entre el equipo docente, estudiantes voluntarios, beneficiarios y administración.
- **Retroalimentación** mediante encuestas de satisfacción al finalizar una actividad.

## Roles del sistema

SyncPass contempla **4 roles** con vistas y funcionalidades específicas:

| Rol | Funcionalidad principal |
|---|---|
| **Docente / Coordinador** | Dashboard de proyectos comunitarios, monitoreo en vivo de actividades y reportes de vinculación. |
| **Estudiante Voluntario** | Acreditación móvil mediante escáner de QR, búsqueda manual de participantes y registro de asistencia con soporte offline. |
| **Beneficiario / Vecino** | Visualización de su pase digital con código QR y respuesta a la encuesta de satisfacción tras la actividad. |
| **Administrador / Superadmin** | Gestión de sedes Duoc UC, asignación de roles, control de usuarios y auditoría de sincronización. |

## Funcionalidades clave

- **Autenticación con JWT**: Inicio de sesión centralizado conectado al backend (`/auth/login`).
- **Acreditación con QR**: Generación y escaneo de códigos QR para registrar asistencias de forma rápida y segura.
- **Modo offline-first**: Uso de **Room/SQLite** local para guardar registros en terreno. Al recuperar conexión, estos se sincronizan con el backend.
- **Registro de asistencia**: Control detallado por evento/taller con fecha, hora, usuario y estado de sincronización.
- **Encuestas de valoración**: Permite a los beneficiarios evaluar la actividad al momento.
- **Auditoría de sincronización**: Seguimiento de qué registros fueron creados localmente, cuándo se sincronizaron y desde qué dispositivo.
- **Gestión de proyectos comunitarios**: Vinculación entre docentes, estudiantes, beneficiarios y actividades.

## Arquitectura del sistema

SyncPass se compone de tres piezas principales:

| Componente | Descripción |
|---|---|
| **Aplicación Móvil (Android)** | Desarrollada en Kotlin con Jetpack Compose. Implementa MVVM, ViewModel + StateFlow, Retrofit, Room y decodificación JWT. |
| **Backend REST API** | Expone los endpoints para autenticación, proyectos, eventos, asistencias, encuestas y sincronización. Desplegado en Linode/AWS EC2. |
| **Base de Datos** | PostgreSQL (orientado a Supabase). Modelo relacional documentado en [`syncpass-ddl.sql`](syncpass-ddl.sql), con campos `is_synced`, `created_locally_at`, `synced_at` y `device_id` pensados para sincronización offline. |

## Tecnologías utilizadas

### Móvil (Android)
- **Kotlin** + **Jetpack Compose** + **Material 3**
- **Arquitectura MVVM** (ViewModel, StateFlow, UiState)
- **Retrofit 2** + **OkHttp 3** + **Gson** para consumo REST
- **Room / SQLite** para persistencia offline
- **JWT Decoder** para extracción de claims (`sub`, `role`, `authorities`)

### Web (Frontend complementario)
- **React** + **TypeScript** + **Wouter** (`proyecto-react-fs2`)

### Backend & Base de Datos
- **API REST** (Node/Express u equivalente según despliegue)
- **PostgreSQL** (Supabase)
- **Autenticación JWT**

## Documentación del repositorio

- [`README.md`](../README.md) – Información general, requerimientos y tecnologías.
- [`syncpass-ddl.sql`](syncpass-ddl.sql) – Modelo relacional (DDL) con entidades, relaciones y lógica offline-first.
- [`mockups/syncpass-figma-mockups.html`](mockups/syncpass-figma-mockups.html) – Wireframes interactivos de las 4 vistas del sistema.

## Idea general

**SyncPass busca unir tecnología y vinculación comunitaria.** Permite acreditar, registrar y dar seguimiento a actividades solidarias de forma ágil, confiable y adaptable al contexto real: muchas veces sin conexión estable en jornadas vecinales, pero con la necesidad de mantener un registro íntegro, sincronizado y auditable para Duoc UC.